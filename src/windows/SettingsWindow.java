package windows;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import de.schaeper.fx.scene.controls.fontPicker.FontPicker;
import de.schaeper.fx.scene.text.font.Font;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import logging.LoggingController;
import menus.TreeItem;
import menus.TreeView;
import settings.Setting;
import settings.SettingController;
import util.Translator;

/**
 * Class that represents a Window for the SettingController.
 * @author Mario Schäper
 */
public class SettingsWindow extends Window {
	private ScrollPane scrollPane = this.scrollPane();
	private TreeView tree = this.treeViewEx();
	private SplitPane contentBox = this.splitPane(this.tree, this.scrollPane);
	private Button applyButton = this.buttonTranslatable("settings.buttons.apply");
	private Button cancelButton = this.buttonTranslatable("settings.buttons.cancel");
	private Button restoreButton = this.buttonTranslatable("settings.buttons.restoreDefault");
	private Region bufferRegionLeft = this.region();
	private Region bufferRegionRight = this.region();
	private HBox buttonBox = this.hBox(
			this.cancelButton, this.bufferRegionLeft, this.restoreButton,
			this.bufferRegionRight, this.applyButton);
	private static boolean hasChanged = false;

	/**
	 * Interface for Input-Nodes that represent a Setting.
	 * @author Mario Schäper
	 */
	private interface CustomInput<E> {
		static List<CustomInput<?>> customInputs = new ArrayList<>();

		/**
		 * Adds the Node to a static List to simplyfy Iterating.<br/>
		 * Should be called when creating an Instance implementing this Interface.
		 */
		default void initialize(Setting<E> setting) {
			CustomInput.customInputs.add(this);
			this.setOnAction(e -> SettingsWindow.hasChanged = true);
			this.setSetting(setting);
			this.toSettingValue();
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
		 * Applies the Setting to the Value of the Input.
		 */
		default void toSettingValue() {
			this.setValue(this.getSetting().getValue());
		}

		/**
		 * Restores the default Setting.
		 */
		default void toDefault() {
			this.setValue(this.getSetting().getDefault());
		}

		/**
		 * Applies the Value of the input to the Setting.
		 */
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

		InpFontPicker(final Setting<Font> setting) {
			super(setting.getValue());
			this.initialize(setting);
		}

		@Override
		public Setting<Font> getSetting() {
			return this.setting;
		}

		@Override
		public void setSetting(final Setting<Font> setting) {
			this.setting = setting;
		}
	}

	/**
	 * Wrapper Class for {@link javafx.scene.control.GridPane GridPane} allowing for
	 * intuitive Creation of Panes for Settings.
	 * @author Mario Schäper
	 */
	private class SettingsPane extends GridPane implements NodeInitializer {
		public SettingsPane (SettingRow...settingRows) {
			super();
			this.setHgap(20);
			this.setVgap(10);
			ColumnConstraints columnLeft = SettingsWindow.this.columnConstraints();
			ColumnConstraints columnRight = SettingsWindow.this.columnConstraints();
			columnLeft.setHgrow(Priority.ALWAYS);
			this.getColumnConstraints().addAll(columnLeft, columnRight);
			Arrays.stream(settingRows).forEach(this::addSetting);
		}

		public void addSetting(SettingRow settingRow) {
			this.addColumn(0, this.labelTranslatable(settingRow.getTranslatableLabel()));
			this.addColumn(1, settingRow.getNode());
		}

		@Override
		public void initColumnConstraints(ColumnConstraints columnConstraints) {
			columnConstraints.setHalignment(HPos.LEFT);
		}

		@Override
		public void initLabel(Label label) {

		}
	}

	/**
	 * Immutable Class, that groups the Data of a Row of
	 * the {@link SettingsPane SettingsPane}.
	 * @author Mario Schäper
	 */
	private class SettingRow {
		private final String translatableLabel;
		private final Node node;

		public SettingRow(String translatableLabel, Node node) {
			this.translatableLabel = translatableLabel;
			this.node = node;
		}

		public String getTranslatableLabel() {
			return this.translatableLabel;
		}

		public Node getNode() {
			return this.node;
		}
	}

	protected SettingsWindow () {
		this.rootTranslatable(new VBox(this.contentBox, this.buttonBox), 250, 100, "settings.title");

		this.stage.setOnCloseRequest(e -> {
			if (SettingsWindow.hasChanged) {
				OptionsDialog.executeOption(
						Translator.translate("settings.dialogs.unsavedChanges.title"),
						Translator.translate("settings.dialogs.unsavedChanges.message"),
						new OptionsDialog.Option(
							this.buttonTypeTranslatable("general.yes", ButtonData.YES),
							this.tree::apply),
						new OptionsDialog.Option(
							this.buttonTypeTranslatable("general.no", ButtonData.NO),
							() -> {
								CustomInput.customInputs.stream()
									.forEach(CustomInput::toSettingValue);}),
						new OptionsDialog.Option(
							this.buttonTypeTranslatable(
								"general.cancel", ButtonData.CANCEL_CLOSE),
							e::consume));
			}
		});

		this.applyButton.setOnAction(e -> {
			this.tree.apply();
			SettingsWindow.hasChanged = false;
		});

		this.cancelButton.setOnAction(e -> {
			this.stage.hide();
		});

		this.restoreButton.setOnAction(e -> {
			try {
				Translator.setLanguage(Translator.DEFAULT_LANGUAGE);
			} catch (Exception ex) {
				LoggingController.log(Level.WARNING, "An Error occurred restoring the Language");
				OptionsDialog.showMessage(
						Translator.translate("settings.dialogs.restoreLang.title"),
						Translator.translate("settings.dialogs.restoreLang.message"));
			}
			SettingController.setDefaults();
			SettingController.save();
			for (CustomInput<?> inp : CustomInput.customInputs) {
				inp.toDefault();
			}
			SettingsWindow.hasChanged = false;
		});
	}

	/**
	 * @return the {@link TreeItem TreeItem} for the "general" option
	 */
	private TreeItem generalItem() {
		return new TreeItem("settings.general.name", new SettingsPane());
	}

	/**
	 * @return the {@link TreeItem TreeItem} for the "language" option
	 */
	private TreeItem languageItem() {
		ArrayList<String> options = new ArrayList<>();
		File languageFolder = new File("config/language");
		for (File f : languageFolder.listFiles()) {
			String fileName = f.getName();
			options.add(fileName.substring(0, fileName.indexOf('.')));
		}
		ComboBox<String> languageSelection = new InpComboBox<>(
				FXCollections.observableList(options), SettingController.get().LANGUAGE);
		return new TreeItem(
				"settings.language.name",
				new SettingsPane(new SettingRow(
						"settings.language.languageLabel", languageSelection)),
				e -> {
					if (!Translator.getLanguage().equals(languageSelection.getValue())) {
						try {
							Translator.setLanguage(languageSelection.getValue());
						} catch (Exception ex) {
							LoggingController.log(Level.SEVERE, "Unable to load Language Settings for " +
									languageSelection.getValue() + ": " + ex.getMessage());
							OptionsDialog.showMessage(
									Translator.translate("settings.dialogs.langSettingInvalid.title"),
									Translator.translate("settings.dialogs.langSettingInvalid.message"));
						}
					}
				});
	}

	/**
	 * @return the {@link TreeItem TreeItem} for the "views" option
	 */
	private TreeItem viewsItem() {
		TreeItem ti = new TreeItem("settings.views.name", new SettingsPane());
		ti.getChildren().add(this.dayViewItem());
		return ti;
	}

	/**
	 * @return the {@link TreeItem TreeItem} for the "dayView" option
	 */
	private TreeItem dayViewItem() {
		return new TreeItem(
				"settings.views.dayView.name",
				new SettingsPane(
					new SettingRow(
						"settings.views.dayView.topBarBackgroundColorLabel",
						new InpColorPicker(
							SettingController.get().DAYVIEW_TOPBAR_BACKGROUND_COLOR)),
					new SettingRow(
						"settings.views.dayView.topBarStrokeColorLabel",
						new InpColorPicker(
							SettingController.get().DAYVIEW_TOPBAR_STROKE_COLOR)),
					new SettingRow(
						"settings.views.dayView.backgroundLeftColorLabel",
						new InpColorPicker(
							SettingController.get().DAYVIEW_BACKGROUND_LEFT_COLOR)),
					new SettingRow(
						"settings.views.dayView.backgroundRightColorLabel",
						new InpColorPicker(
							SettingController.get().DAYVIEW_BACKGROUND_RIGHT_COLOR)),
					new SettingRow(
						"settings.views.dayView.appointmentBackgroundColorLabel",
						new InpColorPicker(
							SettingController.get().DAYVIEW_APPOINTMENT_BACKGROUND_COLOR)),
					new SettingRow(
						"settings.views.dayView.appointmentStrokeColorLabel",
						new InpColorPicker(
							SettingController.get().DAYVIEW_APPOINTMENT_STROKE_COLOR)),
					new SettingRow(
						"settings.views.dayView.appointmentSubjectFontLabel",
						new InpFontPicker(
							SettingController.get().DAYVIEW_APPOINTMENT_SUBJECT_FONT)),
					new SettingRow(
						"settings.views.dayView.appointmentDescriptionFontLabel",
						new InpFontPicker(
							SettingController.get().DAYVIEW_APPOINTMENT_DESCRIPTION_FONT)),
					new SettingRow(
						"settings.views.dayView.timeStampFontLabel",
						new InpFontPicker(
							SettingController.get().DAYVIEW_TIMESTAMP_FONT))),
				e -> {
					if (SettingsWindow.hasChanged) {
						for (CustomInput<?> inp : CustomInput.customInputs) {
							inp.applySetting();
						}
						SettingController.save();
					}
				});
	}

	/**
	 * Invokes {@link javafx.stage.Stage#showAndWait() showAndWait}.
	 */
	@Override
	public void show() {
		SettingsWindow.hasChanged = false;
		this.stage.showAndWait();
	}

	@Override
	public void initSplitPane(SplitPane splitPane) {
		VBox.setVgrow(splitPane, Priority.ALWAYS);
		splitPane.setPrefWidth(600);
		splitPane.setDividerPositions(0.3);
	}

	@Override
	public void initTreeViewEx(TreeView treeView) {
		treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		treeView.getItems().addAll(new TreeItem[]{
				this.generalItem(),
				this.languageItem(),
				this.viewsItem()});
		treeView.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
			if (n != null && n instanceof TreeItem) {
				this.scrollPane.setContent(((TreeItem)n).getContent());
			}
		});
		treeView.getSelectionModel().clearAndSelect(0);
		treeView.setMinWidth(100);
	}

	@Override
	public void initScrollPane(ScrollPane scrollPane) {
		HBox.setHgrow(scrollPane, Priority.ALWAYS);
		scrollPane.setMinWidth(100);
		scrollPane.setFitToWidth(true);
	}

	@Override
	public void initRegion(Region region) {
		HBox.setHgrow(region, Priority.ALWAYS);
	}
}
