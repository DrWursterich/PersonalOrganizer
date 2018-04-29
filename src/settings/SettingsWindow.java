package settings;

import util.Translator;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.collections.FXCollections;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
	private boolean hasChanged = false;

	public SettingsWindow (Stage parentStage) {
		HBox.setHgrow(this.bufferRegionLeft, Priority.ALWAYS);
		HBox.setHgrow(this.bufferRegionRight, Priority.ALWAYS);
		this.stage.setTitle(Translator.translate("settings", "title"));
		this.stage.initModality(Modality.WINDOW_MODAL);
		this.stage.initOwner(parentStage);
		this.stage.setScene(this.scene);
		this.applyButton.setOnAction(e -> {
			this.tree.apply();
		});
		this.cancelButton.setOnAction(e -> {
			this.stage.hide();
		});
		this.restoreButton.setOnAction(e -> {
			Settings.setDefaults();
		});
		this.applyButton.textProperty().bind(Translator.translationProperty("settings", "buttons", "apply"));
		this.cancelButton.textProperty().bind(Translator.translationProperty("settings", "buttons", "cancel"));
		this.restoreButton.textProperty().bind(Translator.translationProperty("settings", "buttons", "restoreDefault"));
		this.contentBox.getChildren().addAll(this.tree, this.scrollPane);
		this.buttonBox.getChildren().addAll(this.cancelButton, this.bufferRegionLeft,
				this.restoreButton, this.bufferRegionRight, this.applyButton);
		this.tree.getItems().addAll(new TreeItem[]{generalItem(), this.languageItem(), this.viewsItem()});
		this.tree.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
			if (n instanceof TreeItem) {
				this.scrollPane.setContent(((TreeItem)n).getContent());
			}
		});
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
			if (this.hasChanged) {
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
		this.hasChanged = false;
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
		ArrayList<String> options = new ArrayList<String>();
		File languageFolder = new File(".\\config\\language");
		for (File f : languageFolder.listFiles()) {
			String fileName = f.getName();
			options.add(fileName.substring(0, fileName.indexOf('.')));
		}
		ComboBox<String> languageSelection = new ComboBox<>(FXCollections.observableList(options));
		Label languageSelectionLabel = new Label();
		languageSelection.setValue(Translator.getLanguage());
		languageSelection.setOnAction(e -> this.hasChanged = true);
		languageSelectionLabel.textProperty().bind(
				Translator.translationProperty("settings", "language", "languageLabel"));
		pane.add(languageSelectionLabel, 0, 0);
		pane.add(languageSelection, 1, 0);
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
		Label topBarBackgroundColorPickerLabel = new Label();
		Label topBarStrokeColorPickerLabel = new Label();
		Label backgroundLeftColorPickerLabel = new Label();
		Label backgroundRightColorPickerLabel = new Label();
		Label appointmentBackgroundColorPickerLabel = new Label();
		Label appointmentStrokeColorPickerLabel = new Label();
		ColorPicker topBarBackgroundColorPicker = new ColorPicker(Settings.DAYVIEW_TOPBAR_BACKGROUND_COLOR.getValue());
		ColorPicker topBarStrokeColorPicker = new ColorPicker(Settings.DAYVIEW_TOPBAR_STROKE_COLOR.getValue());
		ColorPicker backgroundLeftColorPicker = new ColorPicker(Settings.DAYVIEW_BACKGROUND_LEFT_COLOR.getValue());
		ColorPicker backgroundRightColorPicker = new ColorPicker(Settings.DAYVIEW_BACKGROUND_RIGHT_COLOR.getValue());
		ColorPicker appointmentBackgroundColorPicker = new ColorPicker(Settings.DAYVIEW_APPOINTMENT_BACKGROUND_COLOR.getValue());
		ColorPicker appointmentStrokeColorPicker = new ColorPicker(Settings.DAYVIEW_APPOINTMENT_STROKE_COLOR.getValue());
		topBarBackgroundColorPicker.setOnAction(e -> this.hasChanged = true);
		topBarStrokeColorPicker.setOnAction(e -> this.hasChanged = true);
		backgroundLeftColorPicker.setOnAction(e -> this.hasChanged = true);
		backgroundRightColorPicker.setOnAction(e -> this.hasChanged = true);
		appointmentBackgroundColorPicker.setOnAction(e -> this.hasChanged = true);
		appointmentStrokeColorPicker.setOnAction(e -> this.hasChanged = true);
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
		pane.add(topBarBackgroundColorPickerLabel, 0, 0);
		pane.add(topBarBackgroundColorPicker, 1, 0);
		pane.add(topBarStrokeColorPickerLabel, 0, 1);
		pane.add(topBarStrokeColorPicker, 1, 1);
		pane.add(backgroundLeftColorPickerLabel, 0, 2);
		pane.add(backgroundLeftColorPicker, 1, 2);
		pane.add(backgroundRightColorPickerLabel, 0, 3);
		pane.add(backgroundRightColorPicker, 1, 3);
		pane.add(appointmentBackgroundColorPickerLabel, 0, 4);
		pane.add(appointmentBackgroundColorPicker, 1, 4);
		pane.add(appointmentStrokeColorPickerLabel, 0, 5);
		pane.add(appointmentStrokeColorPicker, 1, 5);
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
