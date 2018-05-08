package appointments;

import static javafx.scene.layout.Priority.ALWAYS;
import javafx.beans.property.StringProperty;
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
	private TextField titleField = this.textField(this.trans("titlePrompt"));
	private TextArea descriptionArea = new TextArea();
	private Label categoryLabel = this.label(this.trans("category", "label"));
	private ComboBox<Category> categoryBox = new ComboBox<>();
	private VBox categoryContainer = this.box(new VBox(this.categoryLabel, this.categoryBox));
	private Label priorityLabel = this.label(this.trans("priority", "label"));
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

	private StringProperty dateFromLabelText = this.trans("dateFrom", "label");
	private StringProperty dateFromHourFieldText = this.trans("dateFrom", "hourPrompt");
	private StringProperty dateFromMinuteFieldText = this.trans("dateFrom", "minutePrompt");
	private StringProperty dateToLabelText = this.trans("dateTo", "label");
	private StringProperty dateToHourFieldText = this.trans("dateTo", "hourPrompt");
	private StringProperty dateToMinuteFieldText = this.trans("dateTo", "minutePrompt");
	private StringProperty durationLabelText = this.trans("duration", "label");
	private StringProperty durationMonthsFieldText = this.trans("duration", "monthsPrompt");
	private StringProperty durationDaysFieldText = this.trans("duration", "daysPrompt");
	private StringProperty durationHoursFieldText = this.trans("duration", "hoursPrompt");
	private StringProperty durationMinutesFieldText = this.trans("duration", "minutesPrompt");
	private StringProperty repetetionLabelText = this.trans("repetetion", "label");
	private StringProperty repetetionMonthsFieldText = this.trans("repetetion", "monthsPrompt");
	private StringProperty repetetionDaysFieldText = this.trans("repetetion", "daysPrompt");
	private StringProperty repetetionHoursText = this.trans("repetetion", "hoursPrompt");
	private StringProperty repetetionMinutesText = this.trans("repetetion", "minutesPrompt");
	private StringProperty repetetionEndLabelText = this.trans("repetetion", "endDateLabel");

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
		Label dateFromLabel = this.label(this.dateFromLabelText);
		DatePicker dateFromPicker = new DatePicker();
		TextField dateFromHourField = this.textField(this.dateFromHourFieldText);
		TextField dateFromMinuteField = this.textField(dateFromMinuteFieldText);
		HBox dateFromTimeBox = this.box(new HBox(dateFromHourField, dateFromMinuteField));
		Label dateToLabel = this.label(dateToLabelText);
		DatePicker dateToPicker = new DatePicker();
		TextField dateToHourField = this.textField(dateToHourFieldText);
		TextField dateToMinuteField = this.textField(dateToMinuteFieldText);
		HBox dateToTimeBox = this.box(new HBox(dateToHourField, dateToMinuteField));
		VBox tabContentLeft = this.box(new VBox(dateFromLabel, dateFromPicker,
				dateFromTimeBox, dateToLabel, dateToPicker, dateToTimeBox));
		Label durationLabel = this.label(durationLabelText);
		TextField durationMonthsField = this.textField(durationMonthsFieldText);
		TextField durationDaysField = this.textField(durationDaysFieldText);
		HBox durationDaysBox = this.box(new HBox(durationMonthsField, durationDaysField));
		TextField durationHoursField = this.textField(durationHoursFieldText);
		TextField durationMinutesField = this.textField(durationMinutesFieldText);
		HBox durationTimeBox = this.box(new HBox(durationHoursField, durationMinutesField));
		Label repetetionLabel = this.label(repetetionLabelText);
		TextField repetetionMonthsField = this.textField(repetetionMonthsFieldText);
		TextField repetetionDaysField = this.textField(repetetionDaysFieldText);
		HBox repetetionDaysBox = this.box(new HBox(repetetionMonthsField, repetetionDaysField));
		TextField repetetionHours = this.textField(repetetionHoursText);
		TextField repetetionMinutes = this.textField(repetetionMinutesText);
		HBox repetetionTimeBox = this.box(new HBox(repetetionHours, repetetionMinutes));
		Label repetetionEndLabel = this.label(repetetionEndLabelText);
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

	private Label label(StringProperty text) {
		Label ret = new Label();
		ret.textProperty().bind(text);
		return ret;
	}

	private TextField textField(StringProperty promptText) {
		TextField ret = new TextField();
		ret.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(ret, ALWAYS);
		ret.promptTextProperty().bind(promptText);
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

	private StringProperty trans(String...keys) {
		String[] newKeys = new String[keys.length+1];
		newKeys[0] = "newAppointment";
		System.arraycopy(keys, 0, newKeys, 1, keys.length);
		return Translator.translationProperty(newKeys);
	}
}
