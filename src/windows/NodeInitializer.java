package windows;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import menus.DatePicker;
import util.NumberField;
import util.Translator;

public interface NodeInitializer {
	default void initStage(Stage stage) {};

	default void initScene(Scene scene) {};

	default void initTitledPane(TitledPane titledPane) {};

	default void initRegion(Region region) {};

	default void initSplitPane(SplitPane splitPane) {};

	default void initAccordion(Accordion accordion) {};

	default void initScrollPane(ScrollPane scrollPane) {};

	default void initTreeView(TreeView<?> treeView) {};

	default void initTreeViewEx(menus.TreeView treeView) {};

	default void initHBox(HBox hBox) {};

	default void initVBox(VBox vBox) {};

	default void initLabel(Label label) {};

	default void initTextField(TextField textField) {};

	default void initNumberField(NumberField numberField) {};

	default void initTextArea(TextArea textArea) {};

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
		return numberField;
	}

	default NumberField numberField(String promptText, int value) {
		NumberField numberField = new NumberField(value);
		numberField.setPromptText(promptText);
		this.initTextField(numberField);
		return numberField;
	}

	default NumberField numberField(String promptText, int minValue, int maxValue) {
		NumberField numberField = new NumberField(minValue, maxValue);
		numberField.setPromptText(promptText);
		this.initTextField(numberField);
		return numberField;
	}

	default NumberField numberField(String promptText, int value, int minValue, int maxValue) {
		NumberField numberField = new NumberField(value, minValue, maxValue);
		numberField.setPromptText(promptText);
		this.initTextField(numberField);
		return numberField;
	}

	default NumberField numberFieldTranslatable(String promptText) {
		NumberField numberField = new NumberField();
		numberField.promptTextProperty().bind(Translator.translationProperty(promptText));
		this.initTextField(numberField);
		return numberField;
	}

	default NumberField numberFieldTranslatable(String promptText, int value) {
		NumberField numberField = new NumberField(value);
		numberField.promptTextProperty().bind(Translator.translationProperty(promptText));
		this.initTextField(numberField);
		return numberField;
	}

	default NumberField numberFieldTranslatable(String promptText, int minValue, int maxValue) {
		NumberField numberField = new NumberField(minValue, maxValue);
		numberField.promptTextProperty().bind(Translator.translationProperty(promptText));
		this.initTextField(numberField);
		return numberField;
	}

	default NumberField numberFieldTranslatable(String promptText, int value, int minValue, int maxValue) {
		NumberField numberField = new NumberField(value, minValue, maxValue);
		numberField.promptTextProperty().bind(Translator.translationProperty(promptText));
		this.initTextField(numberField);
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
}
