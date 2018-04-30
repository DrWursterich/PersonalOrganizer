package settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import util.Translator;

/**
 * Class that represents a Window for the Settings.
 * @author Mario Schäper
 */
public class SettingsWindow {
	private Stage stage = new Stage();
	private VBox root = new VBox();
	private Scene scene = new Scene(this.root);
	private HBox contentBox = new HBox();
	private HBox buttonBox = new HBox();
	private TreeView tree = new TreeView();
	private ScrollPane scrollPane = new ScrollPane();
	private Button applyButton = new Button();
	private Button cancelButton = new Button();
	private Button restoreButton = new Button();
	private Region bufferRegionLeft = new Region();
	private Region bufferRegionRight = new Region();
	private Dialog<ButtonType> exitDialog = new Dialog<>();
	private Window exitDialogWindow = exitDialog.getDialogPane().getScene().getWindow();
	private ButtonType yesButton = new ButtonType(Translator.translate("general", "yes"), ButtonBar.ButtonData.YES);
	private ButtonType noButton = new ButtonType(Translator.translate("general", "no"), ButtonBar.ButtonData.NO);
	private ButtonType exitButton = new ButtonType("", ButtonBar.ButtonData.CANCEL_CLOSE);
	private static boolean hasChanged = false;

	/**
	 * Interface for Input-Nodes, which represent a Setting.
	 * @author Mario Schäper
	 */
	private interface CustomInput<E> {
		static List<CustomInput<?>> customInputs = new ArrayList<>();

		/**
		 * Adds the Node to a static List to simplyfy Iterating.<br/>
		 * Should be called when creating an Instance implementing this Interface.
		 */
		default void initialize(E defaultValue) {
			customInputs.add(this);
			this.setOnAction(e -> hasChanged = true);
			this.setDefault(defaultValue);
			this.toDefault();
		}

		void setOnAction(EventHandler<ActionEvent> value);

		void setValue(E value);

		/**
		 * @return the default value. Should be the corresponding Setting.
		 */
		E getDefault();

		/**
		 * @param defaultValue the default value. Should be the corresponding Setting.
		 */
		void setDefault(E defaultValue);

		/**
		 * Restores the default Setting.
		 */
		default void toDefault() {
			this.setValue(this.getDefault());
		};
	}

	/**
	 * Wrapper Class for {@link javafx.scene.control.ColorPicker ColorPicker} implementing
	 * {@link CustomInput CustomInput}.
	 * @author Mario Schäper
	 */
	private class InpColorPicker extends ColorPicker implements CustomInput<Color> {
		private Color defaultValue;

		InpColorPicker(Color defaultValue) {
			super();
			this.initialize(defaultValue);
		}

		public Color getDefault() {
			return this.defaultValue;
		}

		public void setDefault(Color defaultValue) {
			this.defaultValue = defaultValue;
		}
	}

	/**
	 * Wrapper Class for {@link javafx.scene.control.ComboBox ComboBox} implementing
	 * {@link CustomInput CustomInput}.
	 * @author Mario Schäper
	 */
	private class InpComboBox<E> extends ComboBox<E> implements CustomInput<E> {
		private E defaultValue;

		InpComboBox(ObservableList<E> options, E defaultValue) {
			super(options);
			this.initialize(defaultValue);
		}

		public E getDefault() {
			return this.defaultValue;
		}

		public void setDefault(E defaultValue) {
			this.defaultValue = defaultValue;
		}
	}

	public SettingsWindow (Stage parentStage) {
		HBox.setHgrow(this.bufferRegionLeft, Priority.ALWAYS);
		HBox.setHgrow(this.bufferRegionRight, Priority.ALWAYS);
		HBox.setHgrow(this.scrollPane, Priority.ALWAYS);
		VBox.setVgrow(this.contentBox, Priority.ALWAYS);
		this.stage.setMinWidth(250);
		this.stage.setMinHeight(100);
		this.stage.setTitle(Translator.translate("settings", "title"));
		this.stage.initModality(Modality.WINDOW_MODAL);
		this.stage.initOwner(parentStage);
		this.stage.setScene(this.scene);
		this.applyButton.setOnAction(e -> {
			this.tree.apply();
			hasChanged = false;
		});
		this.cancelButton.setOnAction(e -> {
			this.stage.hide();
		});
		this.restoreButton.setOnAction(e -> {
			Settings.setDefaults();
			try {
				Translator.setLanguage(Translator.DEFAULT_LANGUAGE);
			} catch (Exception ex) {
				System.out.println("An Error occurred restoring the Language");
			}
			for (CustomInput<?> inp : CustomInput.customInputs) {
				inp.toDefault();
			}
		});
		this.applyButton.textProperty().bind(Translator.translationProperty("settings", "buttons", "apply"));
		this.cancelButton.textProperty().bind(Translator.translationProperty("settings", "buttons", "cancel"));
		this.restoreButton.textProperty().bind(Translator.translationProperty("settings", "buttons", "restoreDefault"));
		this.contentBox.getChildren().addAll(this.tree, this.scrollPane);
		this.buttonBox.getChildren().addAll(this.cancelButton, this.bufferRegionLeft,
				this.restoreButton, this.bufferRegionRight, this.applyButton);
		this.tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.tree.getItems().addAll(new TreeItem[]{generalItem(), this.languageItem(), this.viewsItem()});
		this.tree.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
			if (n instanceof TreeItem) {
				this.scrollPane.setContent(((TreeItem)n).getContent());
			}
		});
		this.tree.getSelectionModel().clearAndSelect(0);
		this.scrollPane.setPrefWidth(400);
		this.scrollPane.setFitToWidth(true);
		this.exitDialog.titleProperty().bind(Translator.translationProperty("settings", "closeDialog", "title"));
		this.exitDialog.getDialogPane().contentTextProperty().bind(
				Translator.translationProperty("settings", "closeDialog", "saveQuestion"));
		this.exitDialog.getDialogPane().getButtonTypes().addAll(this.yesButton, this.noButton);
		this.exitDialog.getDialogPane().addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			if (e.getCode() == KeyCode.ESCAPE) {
				this.exitDialogWindow.hide();
			}
		});
		this.exitDialogWindow.setOnCloseRequest(e -> {
			this.exitDialog.setResult(this.exitButton);
			this.exitDialogWindow.hide();
			e.consume();
		});
		this.root.getChildren().addAll(this.contentBox, this.buttonBox);
		this.stage.setOnCloseRequest(e -> {
			if (hasChanged) {
				ButtonType result = this.exitDialog.showAndWait().get();
				if (((ButtonType)result).getButtonData().equals(this.exitButton.getButtonData())) {
					e.consume();
				}
				if (((ButtonType)result).getButtonData().equals(this.yesButton.getButtonData())) {
					this.tree.apply();
				}
			}
		});
	}

	/**
	 * Invokes {@link javafx.stage.Stage#showAndWait() showAndWait}.
	 */
	public void show() {
		hasChanged = false;
		this.stage.showAndWait();
	}

	/**
	 * @return the {@link TreeItem TreeItem} for the "general" option
	 */
	private TreeItem generalItem() {
		Pane pane = new Pane();
		return new TreeItem(Translator.translationProperty("settings", "general", "name"), pane);
	}

	/**
	 * @return the {@link TreeItem TreeItem} for the "language" option
	 */
	private TreeItem languageItem() {
		GridPane pane = new GridPane();
		ColumnConstraints columnLeft = new ColumnConstraints();
		ColumnConstraints columnRight = new ColumnConstraints();
		ArrayList<String> options = new ArrayList<String>();
		File languageFolder = new File(".\\config\\language");
		for (File f : languageFolder.listFiles()) {
			String fileName = f.getName();
			options.add(fileName.substring(0, fileName.indexOf('.')));
		}
		ComboBox<String> languageSelection = new InpComboBox<String>(FXCollections.observableList(options),
				Translator.getLanguage());
		Label languageSelectionLabel = new Label();
		languageSelectionLabel.textProperty().bind(
				Translator.translationProperty("settings", "language", "languageLabel"));
		columnLeft.setHalignment(HPos.LEFT);
		columnRight.setHalignment(HPos.LEFT);
		columnLeft.setHgrow(Priority.ALWAYS);
		pane.getColumnConstraints().addAll(columnLeft, columnRight);
		pane.addColumn(0, languageSelectionLabel);
		pane.addColumn(1, languageSelection);
		pane.setHgap(20);
		pane.setVgap(10);
		return new TreeItem(Translator.translationProperty("settings", "language", "name"), pane, e -> {
			if (!Translator.getLanguage().equals(languageSelection.getValue())) {
				try {
					Translator.setLanguage(languageSelection.getValue());
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	/**
	 * @return the {@link TreeItem TreeItem} for the "views" option
	 */
	private TreeItem viewsItem() {
		Pane pane = new Pane();
		TreeItem ti = new TreeItem(Translator.translationProperty("settings", "views", "name"), pane);
		ti.getChildren().add(this.dayViewItem());
		return ti;
	}

	/**
	 * @return the {@link TreeItem TreeItem} for the "dayView" option
	 */
	private TreeItem dayViewItem() {
		GridPane pane = new GridPane();
		ColumnConstraints columnLeft = new ColumnConstraints();
		ColumnConstraints columnRight = new ColumnConstraints();
		Label topBarBackgroundColorPickerLabel = new Label();
		Label topBarStrokeColorPickerLabel = new Label();
		Label backgroundLeftColorPickerLabel = new Label();
		Label backgroundRightColorPickerLabel = new Label();
		Label appointmentBackgroundColorPickerLabel = new Label();
		Label appointmentStrokeColorPickerLabel = new Label();
		ColorPicker topBarBackgroundColorPicker = new InpColorPicker(
				Settings.DAYVIEW_TOPBAR_BACKGROUND_COLOR.getValue());
		ColorPicker topBarStrokeColorPicker = new InpColorPicker(
				Settings.DAYVIEW_TOPBAR_STROKE_COLOR.getValue());
		ColorPicker backgroundLeftColorPicker = new InpColorPicker(
				Settings.DAYVIEW_BACKGROUND_LEFT_COLOR.getValue());
		ColorPicker backgroundRightColorPicker = new InpColorPicker(
				Settings.DAYVIEW_BACKGROUND_RIGHT_COLOR.getValue());
		ColorPicker appointmentBackgroundColorPicker = new InpColorPicker(
				Settings.DAYVIEW_APPOINTMENT_BACKGROUND_COLOR.getValue());
		ColorPicker appointmentStrokeColorPicker = new InpColorPicker(
				Settings.DAYVIEW_APPOINTMENT_STROKE_COLOR.getValue());
		topBarBackgroundColorPickerLabel.textProperty().bind(
				Translator.translationProperty("settings", "views", "dayView", "topBarBackgroundColorLabel"));
		topBarStrokeColorPickerLabel.textProperty().bind(
				Translator.translationProperty("settings", "views", "dayView", "topBarStrokeColorLabel"));
		backgroundLeftColorPickerLabel.textProperty().bind(
				Translator.translationProperty("settings", "views", "dayView", "backgroundLeftColorLabel"));
		backgroundRightColorPickerLabel.textProperty().bind(
				Translator.translationProperty("settings", "views", "dayView", "backgroundRightColorLabel"));
		appointmentBackgroundColorPickerLabel.textProperty().bind(
				Translator.translationProperty("settings", "views", "dayView", "appointmentBackgroundColorLabel"));
		appointmentStrokeColorPickerLabel.textProperty().bind(
				Translator.translationProperty("settings", "views", "dayView", "appointmentStrokeColorLabel"));
		columnLeft.setHalignment(HPos.LEFT);
		columnRight.setHalignment(HPos.LEFT);
		columnLeft.setHgrow(Priority.ALWAYS);
		pane.getColumnConstraints().addAll(columnLeft, columnRight);
		pane.addColumn(0, 	topBarBackgroundColorPickerLabel,
							topBarStrokeColorPickerLabel,
							backgroundLeftColorPickerLabel,
							backgroundRightColorPickerLabel,
							appointmentBackgroundColorPickerLabel,
							appointmentStrokeColorPickerLabel);
		pane.addColumn(1, 	topBarBackgroundColorPicker,
							topBarStrokeColorPicker,
							backgroundLeftColorPicker,
							backgroundRightColorPicker,
							appointmentBackgroundColorPicker,
							appointmentStrokeColorPicker);
		pane.setHgap(20);
		pane.setVgap(10);
		return new TreeItem(Translator.translationProperty("settings", "views", "dayView", "name"), pane, e -> {
			Settings.DAYVIEW_TOPBAR_BACKGROUND_COLOR.setValue(topBarBackgroundColorPicker.getValue());
			Settings.DAYVIEW_TOPBAR_STROKE_COLOR.setValue(topBarStrokeColorPicker.getValue());
			Settings.DAYVIEW_BACKGROUND_LEFT_COLOR.setValue(backgroundLeftColorPicker.getValue());
			Settings.DAYVIEW_BACKGROUND_RIGHT_COLOR.setValue(backgroundRightColorPicker.getValue());
			Settings.DAYVIEW_APPOINTMENT_BACKGROUND_COLOR.setValue(appointmentBackgroundColorPicker.getValue());
			Settings.DAYVIEW_APPOINTMENT_STROKE_COLOR.setValue(appointmentStrokeColorPicker.getValue());
		});
	}
}
