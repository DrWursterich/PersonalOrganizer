package windows;

import java.util.stream.Collectors;
import database.DatabaseController;
import database.period.Period;
import database.priority.Priority;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import menus.AlarmIterator;

public class CreatePriorityWindow extends Window {
	private Label nameLabel = this.labelTranslatable(
			"createPriority.name.label");
	private TextField nameField = this.textFieldTranslatable(
			"createPriority.name.prompt");
	private VBox nameVBox = this.vBox(this.nameLabel, this.nameField);
	private Label alarmsLabel = this.labelTranslatable(
			"createPriority.properties.alarms.label");
	private AlarmIterator alarmIterator = new AlarmIterator();
	private ScrollPane alarmPane = this.scrollPane(
			new StackPane(this.alarmIterator));
	private VBox alarmVBox = this.vBox(
			this.alarmsLabel, this.alarmPane);
	private Button acceptButton = this.buttonTranslatable("general.ok");
	private Region buttonBufferRegion = this.region();
	private Button cancelButton = this.buttonTranslatable("general.cancel");
	private HBox buttonHBox = this.hBox(
			this.cancelButton, this.buttonBufferRegion, this.acceptButton);
	private VBox root = this.rootTranslatable(
			this.vBox(this.nameVBox, this.alarmVBox, this.buttonHBox),
			350,
			380,
			"createPriority.title");

	protected CreatePriorityWindow() {
		VBox.setVgrow(this.alarmVBox, javafx.scene.layout.Priority.ALWAYS);
		StackPane.setMargin(this.alarmIterator, new Insets(10));

		this.root.setPadding(new Insets(10));
		this.root.setSpacing(10);

		this.cancelButton.setCancelButton(true);
		this.cancelButton.setOnAction( e -> {
			this.stage.close();
		});

		this.acceptButton.setDefaultButton(true);
		this.acceptButton.setOnAction(e -> {
			final String name = this.nameField.getText();
			if (name == null || name.trim().equals("")) {
				return;
			}
			DatabaseController.addPriority(Priority.createInstance(
					name.trim(),
					this.alarmIterator.getItems().stream()
							.map(n -> Period.createInstance(
								n.getMinutes() + n.getHours() * 60,
								n.getDays(),
								n.getMonths()))
							.collect(Collectors.toList())));
			this.stage.close();
		});
	}

	@Override
	public void show() {
		this.stage.showAndWait();
	}

	@Override
	public void initHBox(final HBox hBox) {
		VBox.setMargin(hBox, new Insets(10, 0, 0, 0));
	}

	@Override
	public void initRegion(final Region region) {
		HBox.setHgrow(region, javafx.scene.layout.Priority.ALWAYS);
	}

	@Override
	public void initButton(final Button button) {
		button.setPrefWidth(100);
	}
}

