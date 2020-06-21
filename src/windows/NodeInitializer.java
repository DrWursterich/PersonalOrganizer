package windows;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import menus.DatePicker;
import util.NumberField;
import util.Translator;

public interface NodeInitializer {
	static Label newLabelTranslatable(String text) {
		Label label = new Label();
		label.textProperty().bind(Translator.translationProperty(text));
		return label;
	}

	static TextField newTextFieldTranslatable(String promptText) {
		TextField textField = new TextField();
		textField.promptTextProperty().bind(Translator.translationProperty(promptText));
		return textField;
	}

	static TextArea newTextAreaTranslatable(String promptText) {
		TextArea textArea = new TextArea();
		textArea.promptTextProperty().bind(Translator.translationProperty(promptText));
		return textArea;
	}

	static NumberField newNumberFieldTranslatable(String promptText) {
		NumberField numberField = new NumberField();
		numberField.promptTextProperty().bind(Translator.translationProperty(promptText));
		return numberField;
	}

	static NumberField newNumberFieldTranslatable(String promptText, int value) {
		NumberField numberField = new NumberField(value);
		numberField.promptTextProperty().bind(Translator.translationProperty(promptText));
		return numberField;
	}

	static NumberField newNumberFieldTranslatable(String promptText, int minValue, int maxValue) {
		NumberField numberField = new NumberField(minValue, maxValue);
		numberField.promptTextProperty().bind(Translator.translationProperty(promptText));
		return numberField;
	}

	static NumberField newNumberFieldTranslatable(String promptText, int value, int minValue, int maxValue) {
		NumberField numberField = new NumberField(value, minValue, maxValue);
		numberField.promptTextProperty().bind(Translator.translationProperty(promptText));
		return numberField;
	}

	static Button newButtonTranslatable(String label) {
		Button button = new Button();
		button.textProperty().bind(Translator.translationProperty(label));
		return button;
	}

	static ButtonType newButtonTypeTranslatable(String text) {
		ButtonType buttonType = new ButtonType(Translator.translate(text));
		return buttonType;
	}

	static ButtonType newButtonTypeTranslatable(String text, ButtonData buttonData) {
		ButtonType buttonType = new ButtonType(Translator.translate(text), buttonData);
		return buttonType;
	}

	static Tooltip newTooltipTranslatable(String text) {
		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(Translator.translationProperty(text));
		return tooltip;
	}

	static Tooltip newTooltipTranslatable(String text, Node node) {
		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(Translator.translationProperty(text));
		Tooltip.install(node, tooltip);
		return tooltip;
	}

	static Stage newStageTranslatable(String title) {
		Stage stage = new Stage();
		stage.titleProperty().bind(Translator.translationProperty(title));
		return stage;
	}

	static Stage newStageTranslatable(String title, double width, double height) {
		Stage stage = new Stage();
		stage.setMinWidth(width);
		stage.setMinHeight(height);
		stage.titleProperty().bind(Translator.translationProperty(title));
		return stage;
	}

	static TitledPane newTitledPaneTranslatable(String title) {
		TitledPane titledPane = new TitledPane();
		titledPane.textProperty().bind(Translator.translationProperty(title));
		return titledPane;
	}

	static TitledPane newTitledPaneTranslatable(String title, Node node) {
		TitledPane titledPane = new TitledPane();
		titledPane.textProperty().bind(Translator.translationProperty(title));
		titledPane.setContent(node);
		return titledPane;
	}

	default void initStage(Stage stage) {};

	default void initScene(Scene scene) {};

	default void initTitledPane(TitledPane titledPane) {};

	default void initRegion(Region region) {};

	default void initSplitPane(SplitPane splitPane) {};

	default void initAccordion(Accordion accordion) {};

	default void initGridPane(GridPane gridPane) {};

	default void initScrollPane(ScrollPane scrollPane) {};

	default void initTabPane(TabPane tabPane) {};

	default void initTreeView(TreeView<?> treeView) {};

	default void initTreeViewEx(menus.TreeView treeView) {};

	default void initHBox(HBox hBox) {};

	default void initVBox(VBox vBox) {};

	default void initColumnConstraints(ColumnConstraints columnConstraints) {};

	default void initTooltip(Tooltip tooltip) {};

	default void initComboBox(ComboBox<?> comboBox) {};

	default void initLabel(Label label) {};

	default void initTextField(TextField textField) {};

	default void initNumberField(NumberField numberField) {};

	default void initTextArea(TextArea textArea) {};

	default void initButtonType(ButtonType buttonType) {};

	default void initButton(Button button) {};

	default void initDatePicker(DatePicker datePicker) {};

	default Stage stage(String title) {
		Stage stage = new Stage();
		stage.setTitle(title);
		this.initStage(stage);
		return stage;
	}

	default Stage stage(String title, double width, double height) {
		Stage stage = new Stage();
		stage.setTitle(title);
		stage.setMinWidth(width);
		stage.setMinHeight(height);
		this.initStage(stage);
		return stage;
	}

	default Stage stageTranslatable(String title) {
		Stage stage = new Stage();
		stage.titleProperty().bind(Translator.translationProperty(title));
		this.initStage(stage);
		return stage;
	}

	default Stage stageTranslatable(String title, double width, double height) {
		Stage stage = new Stage();
		stage.titleProperty().bind(Translator.translationProperty(title));
		stage.setMinWidth(width);
		stage.setMinHeight(height);
		this.initStage(stage);
		return stage;
	}

	default Scene scene(Region pane) {
		Scene scene = new Scene(pane);
		this.initScene(scene);
		return scene;
	}

	default Scene scene(Region pane, double width, double height) {
		Scene scene = new Scene(pane, width, height);
		this.initScene(scene);
		return scene;
	}

	default HBox hBox(Node... children) {
		HBox hbox = new HBox(children);
		this.initHBox(hbox);
		return hbox;
	}

	default VBox vBox(Node... children) {
		VBox vBox = new VBox(children);
		this.initVBox(vBox);
		return vBox;
	}

	default Label label(String text) {
		Label label = new Label(text);
		this.initLabel(label);
		return label;
	}

	default Label labelTranslatable(String text) {
		Label label = new Label();
		label.textProperty().bind(Translator.translationProperty(text));
		this.initLabel(label);
		return label;
	}

	default TextField textField(String promptText) {
		TextField textField = new TextField(promptText);
		this.initTextField(textField);
		return textField;
	}

	default TextField textFieldTranslatable(String promptText) {
		TextField textField = new TextField();
		textField.promptTextProperty().bind(Translator.translationProperty(promptText));
		this.initTextField(textField);
		return textField;
	}

	default NumberField numberField(String promptText) {
		NumberField numberField = new NumberField();
		numberField.setPromptText(promptText);
		this.initTextField(numberField);
		this.initNumberField(numberField);
		return numberField;
	}

	default NumberField numberField(String promptText, int value) {
		NumberField numberField = new NumberField(value);
		numberField.setPromptText(promptText);
		this.initTextField(numberField);
		this.initNumberField(numberField);
		return numberField;
	}

	default NumberField numberField(String promptText, int minValue, int maxValue) {
		NumberField numberField = new NumberField(minValue, maxValue);
		numberField.setPromptText(promptText);
		this.initTextField(numberField);
		this.initNumberField(numberField);
		return numberField;
	}

	default NumberField numberField(String promptText, int value, int minValue, int maxValue) {
		NumberField numberField = new NumberField(value, minValue, maxValue);
		numberField.setPromptText(promptText);
		this.initTextField(numberField);
		this.initNumberField(numberField);
		return numberField;
	}

	default NumberField numberFieldTranslatable(String promptText) {
		NumberField numberField = new NumberField();
		numberField.promptTextProperty().bind(Translator.translationProperty(promptText));
		this.initTextField(numberField);
		this.initNumberField(numberField);
		return numberField;
	}

	default NumberField numberFieldTranslatable(String promptText, int value) {
		NumberField numberField = new NumberField(value);
		numberField.promptTextProperty().bind(Translator.translationProperty(promptText));
		this.initTextField(numberField);
		this.initNumberField(numberField);
		return numberField;
	}

	default NumberField numberFieldTranslatable(String promptText, int minValue, int maxValue) {
		NumberField numberField = new NumberField(minValue, maxValue);
		numberField.promptTextProperty().bind(Translator.translationProperty(promptText));
		this.initTextField(numberField);
		this.initNumberField(numberField);
		return numberField;
	}

	default NumberField numberFieldTranslatable(String promptText, int value, int minValue, int maxValue) {
		NumberField numberField = new NumberField(value, minValue, maxValue);
		numberField.promptTextProperty().bind(Translator.translationProperty(promptText));
		this.initTextField(numberField);
		this.initNumberField(numberField);
		return numberField;
	}

	default TextArea textArea(String promptText) {
		TextArea textArea = new TextArea(promptText);
		this.initTextArea(textArea);
		return textArea;
	}

	default TextArea textAreaTranslatable(String promptText) {
		TextArea textArea = new TextArea();
		textArea.promptTextProperty().bind(Translator.translationProperty(promptText));
		this.initTextArea(textArea);
		return textArea;
	}

	default TitledPane titledPane(String title) {
		TitledPane titledPane = new TitledPane();
		titledPane.setText(title);
		this.initTitledPane(titledPane);
		return titledPane;
	}

	default TitledPane titledPane(String title, Node node) {
		TitledPane titledPane = new TitledPane();
		titledPane.setText(title);
		titledPane.setContent(node);
		this.initTitledPane(titledPane);
		return titledPane;
	}

	default TitledPane titledPaneTranslatable(String title) {
		TitledPane titledPane = new TitledPane();
		titledPane.textProperty().bind(Translator.translationProperty(title));
		this.initTitledPane(titledPane);
		return titledPane;
	}

	default TitledPane titledPaneTranslatable(String title, Node node) {
		TitledPane titledPane = new TitledPane();
		titledPane.textProperty().bind(Translator.translationProperty(title));
		titledPane.setContent(node);
		this.initTitledPane(titledPane);
		return titledPane;
	}

	default Button button(String label) {
		Button button = new Button(label);
		this.initButton(button);
		return button;
	}

	default Button buttonTranslatable(String label) {
		Button button = new Button();
		button.textProperty().bind(Translator.translationProperty(label));
		this.initButton(button);
		return button;
	}

	default Region region() {
		Region region = new Region();
		this.initRegion(region);
		return region;
	}

	default SplitPane splitPane(Node... children) {
		SplitPane splitPane = new SplitPane(children);
		this.initSplitPane(splitPane);
		return splitPane;
	}

	default Accordion accordion(TitledPane... children) {
		Accordion accordion = new Accordion(children);
		this.initAccordion(accordion);
		return accordion;
	}

	default DatePicker datePicker() {
		DatePicker datePicker = new DatePicker();
		this.initDatePicker(datePicker);
		return datePicker;
	}

	default ScrollPane scrollPane() {
		ScrollPane scrollPane = new ScrollPane();
		this.initScrollPane(scrollPane);
		return scrollPane;
	}

	default ScrollPane scrollPane(final Node node) {
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(node);
		this.initScrollPane(scrollPane);
		return scrollPane;
	}

	default TreeView<String> treeView() {
		return this.treeView(String.class);
	}

	default <T> TreeView<T> treeView(Class<T> type) {
		TreeView<T> treeView = new TreeView<>();
		this.initTreeView(treeView);
		return treeView;
	}

	default menus.TreeView treeViewEx() {
		menus.TreeView treeView = new menus.TreeView();
		this.initTreeViewEx(treeView);
		return treeView;
	}

	default GridPane gridPane() {
		GridPane gridPane = new GridPane();
		this.initGridPane(gridPane);
		return gridPane;
	}

	default ColumnConstraints columnConstraints() {
		ColumnConstraints columnConstraints = new ColumnConstraints();
		this.initColumnConstraints(columnConstraints);
		return columnConstraints;
	}

	default ButtonType buttonType(String text) {
		ButtonType buttonType = new ButtonType(text);
		this.initButtonType(buttonType);
		return buttonType;
	}

	default ButtonType buttonTypeTranslatable(String text) {
		ButtonType buttonType = new ButtonType(Translator.translate(text));
		this.initButtonType(buttonType);
		return buttonType;
	}

	default ButtonType buttonType(String text, ButtonData buttonData) {
		ButtonType buttonType = new ButtonType(text, buttonData);
		this.initButtonType(buttonType);
		return buttonType;
	}

	default ButtonType buttonTypeTranslatable(String text, ButtonData buttonData) {
		ButtonType buttonType = new ButtonType(Translator.translate(text), buttonData);
		this.initButtonType(buttonType);
		return buttonType;
	}

	default Tooltip tooltip(String text) {
		Tooltip tooltip = new Tooltip(text);
		this.initTooltip(tooltip);
		return tooltip;
	}

	default Tooltip tooltipTranslatable(String text) {
		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(Translator.translationProperty(text));
		this.initTooltip(tooltip);
		return tooltip;
	}

	default Tooltip tooltip(String text, Node node) {
		Tooltip tooltip = new Tooltip(text);
		Tooltip.install(node, tooltip);
		this.initTooltip(tooltip);
		return tooltip;
	}

	default Tooltip tooltipTranslatable(String text, Node node) {
		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(Translator.translationProperty(text));
		Tooltip.install(node, tooltip);
		this.initTooltip(tooltip);
		return tooltip;
	}

	default <T> ComboBox<T> comboBox(Class<T> comboBoxClass) {
		ComboBox<T> comboBox = new ComboBox<>();
		this.initComboBox(comboBox);
		return comboBox;
	}

	default <T> ComboBox<T> comboBox(Class<T> comboBoxClass, ObservableList<T> items) {
		ComboBox<T> comboBox = new ComboBox<>(items);
		this.initComboBox(comboBox);
		return comboBox;
	}

	default TabPane tabPane(Tab...tabs) {
		TabPane tabPane = new TabPane(tabs);
		this.initTabPane(tabPane);
		return tabPane;
	}
}

