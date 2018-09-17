package windows;

import database.DatabaseController;
import database.category.Category;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class CreateCategoryWindow extends Window {
	private Label nameLabel = this.labelTranslatable("createCategory.name.label");
	private TextField nameField = this.textFieldTranslatable("createCategory.name.prompt");
	private VBox nameVBox = this.vBox(this.nameLabel, this.nameField);
	private Label descriptionLabel = this.labelTranslatable(
			"createCategory.description.label");
	private TextArea descriptionField = this.textAreaTranslatable(
			"createCategory.description.prompt");
	private VBox descriptionVBox = this.vBox(
			this.descriptionLabel, this.descriptionField);
	private Button acceptButton = this.buttonTranslatable("general.ok");
	private Region buttonBufferRegion = this.region();
	private Button cancelButton = this.buttonTranslatable("general.cancel");
	private HBox buttonHBox = this.hBox(
			this.cancelButton, this.buttonBufferRegion, this.acceptButton);
	private VBox root = this.rootTranslatable(
			this.vBox(this.nameVBox, this.descriptionVBox, this.buttonHBox),
			350, 380, "createCategory.title");

	protected CreateCategoryWindow() {
		VBox.setVgrow(this.descriptionVBox, Priority.ALWAYS);

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

	@Override
	public void initHBox(HBox hBox) {
		VBox.setMargin(hBox, new Insets(10, 0, 0, 0));
	}

	@Override
	public void initRegion(Region region) {
		HBox.setHgrow(region, Priority.ALWAYS);
	}

	@Override
	public void initTextArea(TextArea textArea) {
		VBox.setVgrow(textArea, Priority.ALWAYS);
	}

	@Override
	public void initButton(Button button) {
		button.setPrefWidth(100);
	}
}
