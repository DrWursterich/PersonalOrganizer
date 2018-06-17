package appointments;

import static javafx.scene.layout.Priority.ALWAYS;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import database.DatabaseController;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import menus.ContextMenu;
import menus.MenuItem;
import util.Translator;

public class NewAppointmentWindow {
	private TextField titleField = this.textField(this.trans("titlePrompt"), true, -1);
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
	private TabPane tabPane = new TabPane();
	private Button cancelButton = new Button();
	private Button acceptButton = new Button();
	private HBox buttonBox = this.box(new HBox(this.cancelButton, this.acceptButton));
	private VBox rightPane = this.box(new VBox(this.tabPane, this.buttonBox));
	private Stage stage = new Stage();
	private SplitPane root = new SplitPane(this.leftPane, this.rightPane);
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

	private class CustomTab extends Tab {
		private Label dateFromLabel = label(dateFromLabelText);
		private DatePicker dateFromPicker = new DatePicker();
		private TextField dateFromHourField = textField(dateFromHourFieldText, false, 23);
		private TextField dateFromMinuteField = textField(dateFromMinuteFieldText, false, 59);
		private HBox dateFromTimeBox = box(new HBox(dateFromHourField, dateFromMinuteField));
		private Label dateToLabel = label(dateToLabelText);
		private DatePicker dateToPicker = new DatePicker();
		private TextField dateToHourField = textField(dateToHourFieldText, false, 23);
		private TextField dateToMinuteField = textField(dateToMinuteFieldText, false, 59);
		private HBox dateToTimeBox = box(new HBox(dateToHourField, dateToMinuteField));
		private VBox tabContentLeft = box(new VBox(dateFromLabel, dateFromPicker,
				dateFromTimeBox, dateToLabel, dateToPicker, dateToTimeBox));
		private Label durationLabel = label(durationLabelText);
		private TextField durationMonthsField = textField(durationMonthsFieldText, false, -1);
		private TextField durationDaysField = textField(durationDaysFieldText, false, 30);
		private HBox durationDaysBox = box(new HBox(durationMonthsField, durationDaysField));
		private TextField durationHoursField = textField(durationHoursFieldText, false, 23);
		private TextField durationMinutesField = textField(durationMinutesFieldText, false, 59);
		private HBox durationTimeBox = box(new HBox(durationHoursField, durationMinutesField));
		private Label repetetionLabel = label(repetetionLabelText);
		private TextField repetetionMonthsField = textField(repetetionMonthsFieldText, false, -1);
		private TextField repetetionDaysField = textField(repetetionDaysFieldText, false, 30);
		private HBox repetetionDaysBox = box(new HBox(repetetionMonthsField, repetetionDaysField));
		private TextField repetetionHours = textField(repetetionHoursText, false, 23);
		private TextField repetetionMinutes = textField(repetetionMinutesText, false, 59);
		private HBox repetetionTimeBox = box(new HBox(repetetionHours, repetetionMinutes));
		private Label repetetionEndLabel = label(repetetionEndLabelText);
		private DatePicker repetetionEndPicker = new DatePicker();
		private VBox tabContentRight = box(new VBox(durationLabel, durationDaysBox, durationTimeBox, repetetionLabel,
				repetetionDaysBox, repetetionTimeBox, repetetionEndLabel, repetetionEndPicker));
		private HBox tabContent = box(new HBox(tabContentLeft, tabContentRight));

		private ChangeListener<Object> updateDateTo = (observable, oldValue, newValue) -> {
			if ((oldValue == null || !oldValue.equals(newValue)) &&
					this.dateFromPicker.getValue() != null) {
				int result = fieldVal(this.dateFromMinuteField) + fieldVal(this.durationMinutesField);
				this.dateToMinuteField.setText("" + (result % 60));
				result = result / 60 + fieldVal(this.dateFromHourField) + fieldVal(this.durationHoursField);
				this.dateToHourField.setText("" + (result % 24));
				this.dateToPicker.setValue(this.dateFromPicker.getValue().plusDays(result / 24
						+ fieldVal(this.durationDaysField)).plusMonths(fieldVal(this.durationMonthsField)));
			}
		};

		private ChangeListener<Object> updateDuration = (observable, oldValue, newValue) -> {
			if ((oldValue == null || !oldValue.equals(newValue)) &&
					this.dateFromPicker.getValue() != null && this.dateToPicker.getValue() != null) {
				int startMinute = fieldVal(dateFromMinuteField);
				int startHour = fieldVal(dateFromHourField);
				int endMinute = fieldVal(dateToMinuteField);
				int endHour = fieldVal(dateToHourField);
				long minutes = TimeUnit.MINUTES.convert(
						(new GregorianCalendar(
							this.dateToPicker.getValue().getYear(), this.dateToPicker.getValue().getMonthValue(),
							this.dateToPicker.getValue().getDayOfMonth(), endHour, endMinute)).getTimeInMillis() -
						(new GregorianCalendar(
							this.dateFromPicker.getValue().getYear(), this.dateFromPicker.getValue().getMonthValue(),
							this.dateFromPicker.getValue().getDayOfMonth(), startHour, startMinute)).getTimeInMillis(),
						TimeUnit.MILLISECONDS);

				this.durationHoursField.setText("" +
						(int)(TimeUnit.HOURS.convert(minutes - (minutes % 60), TimeUnit.MINUTES) % 24));
				this.durationMinutesField.setText("" + (int)(minutes % 60));
				this.durationDaysField.setText("" + (
						this.dateFromPicker.getValue().until(this.dateToPicker.getValue()).getDays() -
						((startMinute + 60*startHour) > (endMinute + 60*endHour) ? 1 : 0)));
				this.durationMonthsField.setText("" + (
						this.dateFromPicker.getValue().until(this.dateToPicker.getValue()).getMonths() +
						this.dateFromPicker.getValue().until(this.dateToPicker.getValue()).getYears() * 12 -
						((startMinute + 60*startHour) > (endMinute + 60*endHour) &&
						this.dateFromPicker.getValue().until(this.dateToPicker.getValue()).getDays() == 0 ? 1 : 0)));
			}
		};

		public CustomTab() {
			super();
			this.setText(" + ");
			this.initialize();
			this.setClosable(false);
			this.setOnSelectionChanged(e -> {
				if (this.isSelected()) {
					this.setText(String.format("#%02d", tabPane.getTabs().size()));
					this.setOnSelectionChanged(f -> {});
					this.setClosable(true);
					this.setContextMenu();
					this.setOnCloseRequest(f -> {
						for (int i=tabPane.getTabs().indexOf(this);i<tabPane.getTabs().size()-1;i++) {
							tabPane.getTabs().get(i).setText(String.format("#%02d", i));
						}
					});
					tabPane.getTabs().add(tabPane.getTabs().size(), new CustomTab());
				}
			});
		}

		public CustomTab(String string) {
			super(string);
			this.initialize();
			this.setContextMenu();
		}

		private void initialize() {
			tabContent.setPadding(new Insets(10));

			dateFromPicker.setMaxWidth(Double.MAX_VALUE);
			dateToPicker.setMaxWidth(Double.MAX_VALUE);
			repetetionEndPicker.setMaxWidth(Double.MAX_VALUE);

			durationMinutesField.textProperty().addListener(updateDateTo);
			durationHoursField.textProperty().addListener(updateDateTo);
			durationDaysField.textProperty().addListener(updateDateTo);
			durationMonthsField.textProperty().addListener(updateDateTo);

			dateFromPicker.valueProperty().addListener(updateDuration);
			dateFromHourField.textProperty().addListener(updateDuration);
			dateFromMinuteField.textProperty().addListener(updateDuration);
			dateToPicker.valueProperty().addListener(updateDuration);
			dateToHourField.textProperty().addListener(updateDuration);
			dateToMinuteField.textProperty().addListener(updateDuration);

			HBox.setHgrow(tabContentRight, ALWAYS);
			HBox.setHgrow(tabContentLeft, ALWAYS);

			this.setContent(tabContent);
		}

		private void setContextMenu() {
			this.setContextMenu(new ContextMenu(
					new MenuItem(trans("tabContextMenu", "close"),
							e -> {this.close();}),
					new SeparatorMenuItem(),
					new MenuItem(trans("tabContextMenu", "closeAll"),
							e -> {for (int i=tabPane.getTabs().size()-2;i>0;i--) {
								((CustomTab)tabPane.getTabs().get(i)).close();
							};})
					));
		}

		void close() {
			if (this.getOnCloseRequest() != null) {
				this.getOnCloseRequest().handle(null);
			}
			if (this.getOnClosed() != null) {
				this.getOnClosed().handle(null);
			}
			tabPane.getTabs().remove(this);
		}

		GregorianCalendar getDateFrom() {
			return new GregorianCalendar(
					this.dateFromPicker.getValue().getYear(),
					this.dateFromPicker.getValue().getMonthValue(),
					this.dateFromPicker.getValue().getDayOfMonth(),
					fieldVal(this.dateFromHourField),
					fieldVal(this.dateFromMinuteField));
		}

		GregorianCalendar getDateTo() {
			return new GregorianCalendar(
					this.dateToPicker.getValue().getYear(),
					this.dateToPicker.getValue().getMonthValue(),
					this.dateToPicker.getValue().getDayOfMonth(),
					fieldVal(this.dateToHourField),
					fieldVal(this.dateToMinuteField));
		}
	}

	public NewAppointmentWindow(Stage parentStage) {
		VBox.setVgrow(this.descriptionArea, ALWAYS);
		HBox.setHgrow(this.categoryBox, ALWAYS);
		HBox.setHgrow(this.categoryContainer, ALWAYS);
		HBox.setHgrow(this.priorityInputs, ALWAYS);
		HBox.setHgrow(this.priorityBox, ALWAYS);
		HBox.setHgrow(this.priorityContainer, ALWAYS);
		VBox.setVgrow(this.tabPane, ALWAYS);
		HBox.setHgrow(this.cancelButton, ALWAYS);
		HBox.setHgrow(this.acceptButton, ALWAYS);

		this.leftPane.setPadding(new Insets(10));

		this.descriptionArea.setPrefWidth(300);
		this.descriptionArea.setMaxWidth(Double.MAX_VALUE);
		this.descriptionArea.promptTextProperty().bind(this.trans("descriptionPrompt"));

		this.categoryBox.setMaxWidth(Double.MAX_VALUE);
		this.categoryBox.setPrefWidth(250);
		this.categoryBox.setEditable(true);

		this.priorityBox.setMaxWidth(Double.MAX_VALUE);
		this.priorityBox.setPrefWidth(210);

		this.createPriorityButton.setMinWidth(25);

		this.createPriorityButtonTooltip.textProperty().bind(this.trans("priority", "createTooltip"));
		Tooltip.install(this.createPriorityButton, this.createPriorityButtonTooltip);

		this.rightPane.setPadding(new Insets(10));

		CustomTab firstTab = new CustomTab("#01");
		CustomTab addTab = new CustomTab();

		this.tabPane.setPrefWidth(300);
		this.tabPane.setMinWidth(200);
		this.tabPane.setTabMinWidth(25);
		this.tabPane.getTabs().addAll(firstTab, addTab);
		this.tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

		firstTab.setClosable(false);
		firstTab.setContextMenu(new ContextMenu(
				new MenuItem(this.trans("tabContextMenu", "closeAll"),
						e -> {for (int i=this.tabPane.getTabs().size()-2;i>0;i--) {
							((CustomTab)this.tabPane.getTabs().get(i)).close();
						};})
				));

		this.cancelButton.textProperty().bind(Translator.translationProperty("general", "cancel"));
		this.cancelButton.setMaxWidth(Double.MAX_VALUE);
		this.cancelButton.setOnAction(e -> {
			this.stage.close();
		});

		this.acceptButton.textProperty().bind(Translator.translationProperty("general", "ok"));
		this.acceptButton.setMaxWidth(Double.MAX_VALUE);
		this.acceptButton.setOnAction(e -> {
			if (this.titleField.getText() != null && this.titleField.getText() != "" &&
					firstTab.getDateFrom().before(firstTab.getDateTo())) {
				ArrayList<DatabaseController.AppointmentItem> appointments = new ArrayList<>();
				ObservableList<Tab> tabs = this.tabPane.getTabs();
				for (int i=0;i<tabs.size();i++) {
					if (this.tabPane.getTabs().get(i).isClosable()) {
						appointments.add(new DatabaseController.AppointmentItem(
								((CustomTab)tabs.get(i)).getDateFrom(),
								((CustomTab)tabs.get(i)).getDateTo()));
					}
				}
				DatabaseController.addAppointment(new DatabaseController.AppointmentContainer(
						this.titleField.getText(),
						this.descriptionArea.getText() == null ? "" : this.descriptionArea.getText(),
						this.categoryBox.getValue(), this.priorityBox.getValue(), appointments));
				this.stage.close();
			}
		});

		this.stage.setMinHeight(350);
		this.stage.setMinWidth(380);
		this.stage.titleProperty().bind(this.trans("title"));
		this.stage.initModality(Modality.WINDOW_MODAL);
		this.stage.initOwner(parentStage);
		this.stage.setScene(this.scene);
	}

	public void show() {
		this.stage.showAndWait();
	}

	private Label label(StringProperty text) {
		Label ret = new Label();
		ret.textProperty().bind(text);
		return ret;
	}

	private TextField textField(StringProperty promptText, boolean allowText, int maxValue) {
		TextField ret = new TextField();
		ret.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(ret, ALWAYS);
		ret.promptTextProperty().bind(promptText);
		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(promptText);
		ret.setTooltip(tooltip);
		if (!allowText) {
			ret.setTextFormatter(new TextFormatter<String>(new StringConverter<String>() {
				@Override
				public String toString(String object) {
					if ("".equals(object)) {
						return "";
					}
					int ret = 0;
					try {
						ret = Math.max(0, Integer.parseInt(object));
					} catch (NumberFormatException e) {
						return "";
					}
					return "" + (maxValue == -1 ? ret : Math.min(ret, maxValue));
				}

				@Override
				public String fromString(String string) {
					if ("".equals(string)) {
						return "";
					}
					int ret = 0;
					try {
						ret = Math.max(0, Integer.parseInt(string));
					} catch (NumberFormatException e) {}
					return "" + (maxValue == -1 ? ret : Math.min(ret, maxValue));
				}
			}));
		}
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

	private int fieldVal(TextField field) {
		try {
			return field.getText() == null ? 0 : Integer.parseInt(field.getText());
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
