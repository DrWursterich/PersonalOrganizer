package appointments;

import static javafx.scene.layout.Priority.ALWAYS;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import menus.ContextMenu;
import menus.MenuItem;
import util.Translator;

public class NewAppointmentWindow {
	private TextField titleField = this.textField("newAppointment", "titlePrompt");
	private TextArea descriptionArea = new TextArea();
	private Label categoryLabel = this.label("newAppointment", "category", "label");
	private ComboBox<Category> categoryBox = new ComboBox<>();
	private VBox categoryContainer = this.box(new VBox(this.categoryLabel, this.categoryBox));
	private Label priorityLabel = this.label("newAppointment", "priority", "label");
	private ComboBox<Priority> priorityBox = new ComboBox<>();
	private Button createPriorityButton = new Button("+");
	private Tooltip createPriorityButtonTooltip = new Tooltip();
	private HBox priorityInputs = this.box(new HBox(this.priorityBox, this.createPriorityButton));
	private VBox priorityContainer = this.box(new VBox(this.priorityLabel, this.priorityInputs));
	private HBox comboBoxPane = this.box(new HBox(this.categoryContainer, this.priorityContainer));
	private VBox leftPane = this.box(new VBox(this.titleField, this.descriptionArea, this.comboBoxPane));
	private Tab firstTab = new Tab("#01");
	private Tab addTab = this.addTab();
	private TabPane tabPane = new TabPane(this.firstTab, this.addTab);
	private Stage stage = new Stage();
	private SplitPane root = new SplitPane(this.leftPane, this.tabPane);
	private Scene scene = new Scene(this.root);

	public NewAppointmentWindow(Stage parentStage) {
		VBox.setVgrow(this.descriptionArea, ALWAYS);
		HBox.setHgrow(this.categoryBox, ALWAYS);
		HBox.setHgrow(this.categoryContainer, ALWAYS);
		HBox.setHgrow(this.priorityInputs, ALWAYS);
		HBox.setHgrow(this.priorityBox, ALWAYS);
		HBox.setHgrow(this.priorityContainer, ALWAYS);

		this.leftPane.setPadding(new Insets(10));

		this.descriptionArea.setPrefWidth(300);
		this.descriptionArea.setMaxWidth(Double.MAX_VALUE);
		this.descriptionArea.promptTextProperty().bind(
				Translator.translationProperty("newAppointment", "descriptionPrompt"));

		this.categoryBox.setMaxWidth(Double.MAX_VALUE);
		this.categoryBox.setPrefWidth(250);
		this.categoryBox.setEditable(true);

		this.priorityBox.setMaxWidth(Double.MAX_VALUE);
		this.priorityBox.setPrefWidth(210);

		this.createPriorityButton.setMinWidth(25);

		this.createPriorityButtonTooltip.textProperty().bind(
				Translator.translationProperty("newAppointment", "priority", "createTooltip"));
		Tooltip.install(this.createPriorityButton, this.createPriorityButtonTooltip);

		this.tabPane.setPrefWidth(300);
		this.tabPane.setMinWidth(200);
		this.tabPane.setTabMinWidth(25);

		this.firstTab.setContent(this.tabContent());
		this.firstTab.setClosable(false);
		this.firstTab.setContextMenu(new ContextMenu(
				new MenuItem(Translator.translationProperty("newAppointment", "tabContextMenu", "closeAll"),
						e -> {for (int i=this.tabPane.getTabs().size()-2;i>0;i--) {
							this.closeTab(this.tabPane.getTabs().get(i));
						};})
				));

		this.stage.setMinHeight(300);
		this.stage.setMinWidth(380);
		this.stage.titleProperty().bind(Translator.translationProperty("newAppointment", "title"));
		this.stage.initModality(Modality.WINDOW_MODAL);
		this.stage.initOwner(parentStage);
		this.stage.setScene(this.scene);
	}

	public void show() {
		this.stage.showAndWait();
	}

	private Tab contextMenu(Tab tab) {
		tab.setContextMenu(new ContextMenu(
				new MenuItem(Translator.translationProperty("newAppointment", "tabContextMenu", "close"),
						e -> {this.closeTab(tab);}),
				new SeparatorMenuItem(),
				new MenuItem(Translator.translationProperty("newAppointment", "tabContextMenu", "closeAll"),
						e -> {for (int i=this.tabPane.getTabs().size()-2;i>0;i--) {
							this.closeTab(this.tabPane.getTabs().get(i));
						};})
				));
		return tab;
	}

	private void closeTab(Tab tab) {
		if (tab.getOnCloseRequest() != null) {
			tab.getOnCloseRequest().handle(null);
		}
		if (tab.getOnClosed() != null) {
			tab.getOnClosed().handle(null);
		}
		this.tabPane.getTabs().remove(tab);
	}

	private HBox tabContent() {
		Label dateFromLabel = this.label("newAppointment", "dateFrom", "label");
		DatePicker dateFromPicker = new DatePicker();
		TextField dateFromHourField = this.textField("newAppointment", "dateFrom", "hourPrompt");
		TextField dateFromMinuteField = this.textField("newAppointment", "dateFrom", "minutePrompt");
		HBox dateFromTimeBox = this.box(new HBox(dateFromHourField, dateFromMinuteField));
		Label dateToLabel = this.label("newAppointment", "dateTo", "label");
		DatePicker dateToPicker = new DatePicker();
		TextField dateToHourField = this.textField("newAppointment", "dateTo", "hourPrompt");
		TextField dateToMinuteField = this.textField("newAppointment", "dateTo", "minutePrompt");
		HBox dateToTimeBox = this.box(new HBox(dateToHourField, dateToMinuteField));
		VBox tabContentLeft = this.box(new VBox(dateFromLabel, dateFromPicker,
				dateFromTimeBox, dateToLabel, dateToPicker, dateToTimeBox));
		Label durationLabel = this.label("newAppointment", "duration", "label");
		TextField durationMonthsField = this.textField("newAppointment", "duration", "monthsPrompt");
		TextField durationDaysField = this.textField("newAppointment", "duration", "daysPrompt");
		HBox durationDaysBox = this.box(new HBox(durationMonthsField, durationDaysField));
		TextField durationHoursField = this.textField("newAppointment", "duration", "hoursPrompt");
		TextField durationMinutesField = this.textField("newAppointment", "duration", "minutesPrompt");
		HBox durationTimeBox = this.box(new HBox(durationHoursField, durationMinutesField));
		Label repetetionLabel = this.label("newAppointment", "repetetion", "label");
		TextField repetetionMonthsField = this.textField("newAppointment", "repetetion", "monthsPrompt");
		TextField repetetionDaysField = this.textField("newAppointment", "repetetion", "daysPrompt");
		HBox repetetionDaysBox = this.box(new HBox(repetetionMonthsField, repetetionDaysField));
		TextField repetetionHours = this.textField("newAppointment", "repetetion", "hoursPrompt");
		TextField repetetionMinutes = this.textField("newAppointment", "repetetion", "minutesPrompt");
		HBox repetetionTimeBox = this.box(new HBox(repetetionHours, repetetionMinutes));
		Label repetetionEndLabel = this.label("newAppointment", "repetetion", "endDateLabel");
		DatePicker repetetionEndPicker = new DatePicker();
		VBox tabContentRight = this.box(new VBox(durationLabel, durationDaysBox, durationTimeBox, repetetionLabel,
				repetetionDaysBox, repetetionTimeBox, repetetionEndLabel, repetetionEndPicker));
		HBox tabContent = this.box(new HBox(tabContentLeft, tabContentRight));

		tabContent.setPadding(new Insets(10));

		dateFromPicker.setMaxWidth(Double.MAX_VALUE);
		dateToPicker.setMaxWidth(Double.MAX_VALUE);
		repetetionEndPicker.setMaxWidth(Double.MAX_VALUE);

		HBox.setHgrow(tabContentRight, ALWAYS);
		HBox.setHgrow(tabContentLeft, ALWAYS);

		return tabContent;
	}

	private Tab addTab() {
		Tab addTab = new Tab("+");
		addTab.setClosable(false);
		addTab.setOnSelectionChanged(e -> {
			if (addTab.isSelected()) {
				addTab.setText(String.format("#%02d", this.tabPane.getTabs().size()));
				addTab.setContent(this.tabContent());
				addTab.setOnSelectionChanged(f -> {});
				addTab.setClosable(true);
				addTab.setOnCloseRequest(f -> {
					for (int i=this.tabPane.getTabs().indexOf(addTab);i<this.tabPane.getTabs().size()-1;i++) {
						this.tabPane.getTabs().get(i).setText(String.format("#%02d", i));
					}
				});
				this.contextMenu(addTab);
				this.tabPane.getTabs().add(this.tabPane.getTabs().size(), this.addTab());
			}
		});
		return addTab;
	}

	private Label label(String...keys) {
		Label ret = new Label();
		ret.textProperty().bind(Translator.translationProperty(keys));
		return ret;
	}

	private TextField textField(String...keys) {
		TextField ret = new TextField();
		ret.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(ret, ALWAYS);
		ret.promptTextProperty().bind(Translator.translationProperty(keys));
		return ret;
	}

	private HBox box(HBox box) {
		box.setSpacing(5);
		return box;
	}

	private VBox box(VBox box) {
		box.setSpacing(5);
		return box;
	}
}
