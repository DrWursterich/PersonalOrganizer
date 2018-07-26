package settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import menus.FontPicker;
import menus.OptionsDialog;
import menus.TreeItem;
import menus.TreeView;
import settings.Setting;
import util.Translator;

/**
 * Class that represents a Window for the SettingController.
 * @author Mario Schäper
 */
public class SettingsWindow {
	private final Stage stage = new Stage();
	private final VBox root = new VBox();
	private final Scene scene = new Scene(this.root);
	private final HBox buttonBox = new HBox();
	private final TreeView tree = new TreeView();
	private final ScrollPane scrollPane = new ScrollPane();
	private final SplitPane contentBox = new SplitPane(this.tree, this.scrollPane);
	private final Button applyButton = new Button();
	private final Button cancelButton = new Button();
	private final Button restoreButton = new Button();
	private final Region bufferRegionLeft = new Region();
	private final Region bufferRegionRight = new Region();
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
		default void initialize(Setting<E> setting) {
			customInputs.add(this);
			this.setOnAction(e -> SettingsWindow.hasChanged = true);
			this.setSetting(setting);
			this.setValue(setting.getValue());
		}

		void setOnAction(EventHandler<ActionEvent> value);

		void setValue(E value);

		E getValue();

		/**
		 * @return the default value. Should be the corresponding Setting.
		 */
		Setting<E> getSetting();

		/**
		 * @param defaultValue the default value. Should be the corresponding Setting.
		 */
		void setSetting(Setting<E> setting);

		/**
		 * Restores the default Setting.
		 */
		default void toDefault() {
			this.setValue(this.getSetting().defaultValue);
		}

		default void applySetting() {
			this.getSetting().setValue(this.getValue());
		}
	}

	/**
	 * Wrapper Class for {@link javafx.scene.control.ColorPicker ColorPicker} implementing
	 * {@link CustomInput CustomInput}.
	 * @author Mario Schäper
	 */
	private class InpColorPicker extends ColorPicker implements CustomInput<Color> {
		private Setting<Color> setting;

		InpColorPicker(Setting<Color> setting) {
			super();
			this.initialize(setting);
		}

		@Override
		public Setting<Color> getSetting() {
			return this.setting;
		}

		@Override
		public void setSetting(Setting<Color> setting) {
			this.setting = setting;
		}
	}

	/**
	 * Wrapper Class for {@link javafx.scene.control.ComboBox ComboBox} implementing
	 * {@link CustomInput CustomInput}.
	 * @author Mario Schäper
	 */
	private class InpComboBox<E> extends ComboBox<E> implements CustomInput<E> {
		private Setting<E> setting;

		InpComboBox(ObservableList<E> options, Setting<E> setting) {
			super(options);
			this.initialize(setting);
		}

		@Override
		public Setting<E> getSetting() {
			return this.setting;
		}

		@Override
		public void setSetting(Setting<E> setting) {
			this.setting = setting;
		}
	}

	/**
	 * Wrapper Class for {@link javafx.scene.control.ComboBox ComboBox} implementing
	 * {@link CustomInput CustomInput}.
	 * @author Mario Schäper
	 */
	private class InpFontPicker extends FontPicker implements CustomInput<Font> {
		private Setting<Font> setting;

		InpFontPicker(Setting<Font> setting) {
			super(setting.getValue());
			this.initialize(setting);
		}

		@Override
		public Setting<Font> getSetting() {
			return this.setting;
		}

		@Override
		public void setSetting(Setting<Font> setting) {
			this.setting = setting;
		}
	}

	public SettingsWindow (Stage parentStage) {
		HBox.setHgrow(this.bufferRegionLeft, Priority.ALWAYS);
		HBox.setHgrow(this.bufferRegionRight, Priority.ALWAYS);
		HBox.setHgrow(this.scrollPane, Priority.ALWAYS);
		VBox.setVgrow(this.contentBox, Priority.ALWAYS);

		this.stage.setMinWidth(250);
		this.stage.setMinHeight(100);
		this.stage.titleProperty().bind(Translator.translationProperty("settings", "title"));
		this.stage.initModality(Modality.WINDOW_MODAL);
		this.stage.initOwner(parentStage);
		this.stage.setScene(this.scene);
		this.stage.setOnCloseRequest(e -> {
			if (SettingsWindow.hasChanged) {
				ButtonType yes = new ButtonType(
						Translator.translate("general", "yes"), ButtonData.YES);
				ButtonType no = new ButtonType(
						Translator.translate("general", "no"), ButtonData.NO);
				ButtonType cancel = new ButtonType(
						Translator.translate("general", "cancel"), ButtonData.CANCEL_CLOSE);
				ButtonType result = OptionsDialog.getOption(
						Translator.translate("settings", "dialogs", "unsavedChanges", "title"),
						Translator.translate("settings", "dialogs", "unsavedChanges", "message"),
						yes, no, cancel);
				if (cancel.getButtonData().equals(result.getButtonData())) {
					e.consume();
				}
				if (yes.getButtonData().equals(result.getButtonData())) {
					this.tree.apply();
				}
			}
		});

		this.applyButton.textProperty().bind(Translator.translationProperty("settings", "buttons", "apply"));
		this.applyButton.setOnAction(e -> {
			this.tree.apply();
			SettingsWindow.hasChanged = false;
		});

		this.cancelButton.textProperty().bind(
				Translator.translationProperty("settings", "buttons", "cancel"));
		this.cancelButton.setOnAction(e -> {
			this.stage.hide();
		});

		this.restoreButton.textProperty().bind(
				Translator.translationProperty("settings", "buttons", "restoreDefault"));
		this.restoreButton.setOnAction(e -> {
			try {
				Translator.setLanguage(Translator.DEFAULT_LANGUAGE);
			} catch (final Exception ex) {
				System.out.println("An Error occurred restoring the Language");
			}
			for (final CustomInput<?> inp : CustomInput.customInputs) {
				inp.toDefault();
			}
			SettingController.setDefaults();
			SettingController.save();
		});

		this.buttonBox.getChildren().addAll(this.cancelButton, this.bufferRegionLeft,
				this.restoreButton, this.bufferRegionRight, this.applyButton);

		this.tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.tree.getItems().addAll(new TreeItem[]{this.generalItem(), this.languageItem(), this.viewsItem()});
		this.tree.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
			if (n instanceof TreeItem) {
				this.scrollPane.setContent(((TreeItem)n).getContent());
			}
		});
		this.tree.getSelectionModel().clearAndSelect(0);
		this.tree.setMinWidth(100);

		this.scrollPane.setMinWidth(100);
		this.scrollPane.setFitToWidth(true);

		this.contentBox.setPrefWidth(600);
		this.contentBox.setDividerPositions(0.3);
		this.root.getChildren().addAll(this.contentBox, this.buttonBox);
	}

	/**
	 * Invokes {@link javafx.stage.Stage#showAndWait() showAndWait}.
	 */
	public void show() {
		SettingsWindow.hasChanged = false;
		this.stage.showAndWait();
	}

	/**
	 * @return the {@link TreeItem TreeItem} for the "general" option
	 */
	private TreeItem generalItem() {
		final Pane pane = new Pane();
		return new TreeItem(Translator.translationProperty("settings", "general", "name"), pane);
	}

	/**
	 * @return the {@link TreeItem TreeItem} for the "language" option
	 */
	private TreeItem languageItem() {
		final GridPane pane = new GridPane();
		final ColumnConstraints columnLeft = new ColumnConstraints();
		final ColumnConstraints columnRight = new ColumnConstraints();
		final ArrayList<String> options = new ArrayList<>();
		final File languageFolder = new File("config/language");
		for (final File f : languageFolder.listFiles()) {
			final String fileName = f.getName();
			options.add(fileName.substring(0, fileName.indexOf('.')));
		}
		final ComboBox<String> languageSelection = new InpComboBox<>(
				FXCollections.observableList(options), SettingController.get().LANGUAGE);
		final Label languageSelectionLabel = new Label();
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
				} catch (final IOException ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	/**
	 * @return the {@link TreeItem TreeItem} for the "views" option
	 */
	private TreeItem viewsItem() {
		final Pane pane = new Pane();
		final TreeItem ti = new TreeItem(Translator.translationProperty("settings", "views", "name"), pane);
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
		Label timeStampFontPickerLabel = new Label();
		ColorPicker topBarBackgroundColorPicker = new InpColorPicker(
				SettingController.get().DAYVIEW_TOPBAR_BACKGROUND_COLOR);
		ColorPicker topBarStrokeColorPicker = new InpColorPicker(
				SettingController.get().DAYVIEW_TOPBAR_STROKE_COLOR);
		ColorPicker backgroundLeftColorPicker = new InpColorPicker(
				SettingController.get().DAYVIEW_BACKGROUND_LEFT_COLOR);
		ColorPicker backgroundRightColorPicker = new InpColorPicker(
				SettingController.get().DAYVIEW_BACKGROUND_RIGHT_COLOR);
		ColorPicker appointmentBackgroundColorPicker = new InpColorPicker(
				SettingController.get().DAYVIEW_APPOINTMENT_BACKGROUND_COLOR);
		ColorPicker appointmentStrokeColorPicker = new InpColorPicker(
				SettingController.get().DAYVIEW_APPOINTMENT_STROKE_COLOR);
		FontPicker timeStampFontPicker = new InpFontPicker(
				SettingController.get().DAYVIEW_TIMESTAMP_FONT);
		topBarBackgroundColorPickerLabel.textProperty().bind(Translator.translationProperty(
				"settings", "views", "dayView", "topBarBackgroundColorLabel"));
		topBarStrokeColorPickerLabel.textProperty().bind(Translator.translationProperty(
				"settings", "views", "dayView", "topBarStrokeColorLabel"));
		backgroundLeftColorPickerLabel.textProperty().bind(Translator.translationProperty(
				"settings", "views", "dayView", "backgroundLeftColorLabel"));
		backgroundRightColorPickerLabel.textProperty().bind(Translator.translationProperty(
				"settings", "views", "dayView", "backgroundRightColorLabel"));
		appointmentBackgroundColorPickerLabel.textProperty().bind(Translator.translationProperty(
				"settings", "views", "dayView", "appointmentBackgroundColorLabel"));
		appointmentStrokeColorPickerLabel.textProperty().bind(Translator.translationProperty(
				"settings", "views", "dayView", "appointmentStrokeColorLabel"));
		timeStampFontPickerLabel.textProperty().bind(Translator.translationProperty(
				"settings", "views", "dayView", "timeStampFontLabel"));
		columnLeft.setHalignment(HPos.LEFT);
		columnRight.setHalignment(HPos.LEFT);
		columnLeft.setHgrow(Priority.ALWAYS);
		pane.getColumnConstraints().addAll(columnLeft, columnRight);
		pane.addColumn(0,
				topBarBackgroundColorPickerLabel,
				topBarStrokeColorPickerLabel,
				backgroundLeftColorPickerLabel,
				backgroundRightColorPickerLabel,
				appointmentBackgroundColorPickerLabel,
				appointmentStrokeColorPickerLabel,
				timeStampFontPickerLabel);
		pane.addColumn(1,
				topBarBackgroundColorPicker,
				topBarStrokeColorPicker,
				backgroundLeftColorPicker,
				backgroundRightColorPicker,
				appointmentBackgroundColorPicker,
				appointmentStrokeColorPicker,
				timeStampFontPicker);
		pane.setHgap(20);
		pane.setVgap(10);
		return new TreeItem(Translator.translationProperty("settings", "views", "dayView", "name"),
				pane, e -> {
					for (CustomInput<?> inp : CustomInput.customInputs) {
						inp.applySetting();
					}
					SettingController.save();
				});
	}
}
