package windows;

import database.DatabaseController;
import database.category.Category;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.Translator;

public class CreateCategoryWindow extends Window {
	private Stage stage = new Stage();
	private Label nameLabel = this.label("createCategory.name.label");
	private TextField nameField = this.textField("createCategory.name.prompt");
	private VBox nameVBox = new VBox(this.nameLabel, this.nameField);
	private Label descriptionLabel = this.label("createCategory.description.label");
	private TextArea descriptionField = this.textArea(
			"createCategory.description.prompt");
	private VBox descriptionVBox = new VBox(this.descriptionLabel, this.descriptionField);
	private Button acceptButton = this.button("general.ok");
	private Region buttonBufferRegion = new Region();
	private Button cancelButton = this.button("general.cancel");
	private HBox buttonHBox = new HBox(
			this.cancelButton, this.buttonBufferRegion, this.acceptButton);
	private VBox root = new VBox(this.nameVBox, this.descriptionVBox, this.buttonHBox);
	private Scene scene = new Scene(this.root);

	protected CreateCategoryWindow() {
		this.stage.setMinHeight(350);
		this.stage.setMinWidth(380);
		this.stage.titleProperty().bind(
				Translator.translationProperty("createCategory.title"));
		this.stage.initModality(Modality.APPLICATION_MODAL);
		this.stage.setScene(this.scene);

		VBox.setVgrow(this.descriptionVBox, Priority.ALWAYS);
		VBox.setVgrow(this.descriptionField, Priority.ALWAYS);
		HBox.setHgrow(this.buttonBufferRegion, Priority.ALWAYS);
		VBox.setMargin(this.buttonHBox, new Insets(10, 0, 0, 0));

		this.root.setPadding(new Insets(10));
		this.root.setSpacing(10);

		this.cancelButton.setCancelButton(true);
		this.cancelButton.setOnAction(e -> {
			this.stage.close();
		});

		this.acceptButton.setDefaultButton(true);
		this.acceptButton.setOnAction(e -> {
			String name = this.nameField.getText();
			if (name != null && !name.trim().equals("")) {
				String description = this.descriptionField.getText();
				DatabaseController.addCategory(Category.createInstance(
						name.trim(), description != null ? description.trim() : ""));
				this.stage.close();
			}
		});
	}

	@Override
	public void show() {
		this.stage.showAndWait();
	}

	private Label label(String translationPropertyText) {
		Label ret = new Label();
		ret.textProperty().bind(Translator.translationProperty(translationPropertyText));
		return ret;
	}

	private TextField textField(String textTranslationText) {
		TextField ret = new TextField();
		ret.promptTextProperty().bind(Translator.translationProperty(textTranslationText));
		return ret;
	}

	private TextArea textArea(String textTranslationText) {
		TextArea ret = new TextArea();
		ret.promptTextProperty().bind(Translator.translationProperty(textTranslationText));
		return ret;
	}

	private Button button(String labelTranslationText) {
		Button ret = new Button();
		ret.textProperty().bind(Translator.translationProperty(labelTranslationText));
		ret.setPrefWidth(100);
		return ret;
	}
}
