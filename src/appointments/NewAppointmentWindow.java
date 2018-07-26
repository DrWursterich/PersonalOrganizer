package appointments;

import static javafx.scene.layout.Priority.ALWAYS;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
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
import database.DatabaseController;
import menus.ContextMenu;
import menus.MenuItem;
import menus.OptionsDialog;
import util.Duration;
import util.Translator;

public class NewAppointmentWindow {
	private static final String ADD_TAB_TEXT = " + ";
	private TextField titleField = this.textField(this.trans("titlePrompt"), true, -1);
	private TextArea descriptionArea = new TextArea();
	private Label categoryLabel = this.label(this.trans("category", "label"));
	private ComboBox<Category> categoryBox = new ComboBox<>();
	private VBox categoryContainer = this.box(new VBox(this.categoryLabel, this.categoryBox));
	private Label priorityLabel = this.label(this.trans("priority", "label"));
	private ComboBox<Priority> priorityBox = new ComboBox<>();
	private Button createPriorityButton = new Button(NewAppointmentWindow.ADD_TAB_TEXT);
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
	private StringProperty repetetionHoursFieldText = this.trans("repetetion", "hoursPrompt");
	private StringProperty repetetionMinutesFieldText = this.trans("repetetion", "minutesPrompt");
	private StringProperty repetetionEndLabelText = this.trans("repetetion", "endDateLabel");

	private class CustomTab extends Tab {
		private Label dateFromLabel = NewAppointmentWindow.this.label(NewAppointmentWindow.this.dateFromLabelText);
		private DatePicker dateFromPicker = new DatePicker();
		private TextField dateFromHourField = NewAppointmentWindow.this.textField(NewAppointmentWindow.this.dateFromHourFieldText, false, 23);
		private TextField dateFromMinuteField = NewAppointmentWindow.this.textField(NewAppointmentWindow.this.dateFromMinuteFieldText, false, 59);
		private HBox dateFromTimeBox = NewAppointmentWindow.this.box(new HBox(this.dateFromHourField, this.dateFromMinuteField));
		private Label dateToLabel = NewAppointmentWindow.this.label(NewAppointmentWindow.this.dateToLabelText);
		private DatePicker dateToPicker = new DatePicker();
		private TextField dateToHourField = NewAppointmentWindow.this.textField(NewAppointmentWindow.this.dateToHourFieldText, false, 23);
		private TextField dateToMinuteField = NewAppointmentWindow.this.textField(NewAppointmentWindow.this.dateToMinuteFieldText, false, 59);
		private HBox dateToTimeBox = NewAppointmentWindow.this.box(new HBox(this.dateToHourField, this.dateToMinuteField));
		private VBox tabContentLeft = NewAppointmentWindow.this.box(new VBox(this.dateFromLabel, this.dateFromPicker,
				this.dateFromTimeBox, this.dateToLabel, this.dateToPicker, this.dateToTimeBox));
		private Label durationLabel = NewAppointmentWindow.this.label(NewAppointmentWindow.this.durationLabelText);
		private TextField durationMonthsField = NewAppointmentWindow.this.textField(NewAppointmentWindow.this.durationMonthsFieldText, false, -1);
		private TextField durationDaysField = NewAppointmentWindow.this.textField(NewAppointmentWindow.this.durationDaysFieldText, false, 30);
		private HBox durationDaysBox = NewAppointmentWindow.this.box(new HBox(this.durationMonthsField, this.durationDaysField));
		private TextField durationHoursField = NewAppointmentWindow.this.textField(NewAppointmentWindow.this.durationHoursFieldText, false, 23);
		private TextField durationMinutesField = NewAppointmentWindow.this.textField(NewAppointmentWindow.this.durationMinutesFieldText, false, 59);
		private HBox durationTimeBox = NewAppointmentWindow.this.box(new HBox(this.durationHoursField, this.durationMinutesField));
		private Label repetetionLabel = NewAppointmentWindow.this.label(NewAppointmentWindow.this.repetetionLabelText);
		private TextField repetetionMonthsField = NewAppointmentWindow.this.textField(NewAppointmentWindow.this.repetetionMonthsFieldText, false, -1);
		private TextField repetetionDaysField = NewAppointmentWindow.this.textField(NewAppointmentWindow.this.repetetionDaysFieldText, false, 30);
		private HBox repetetionDaysBox = NewAppointmentWindow.this.box(new HBox(this.repetetionMonthsField, this.repetetionDaysField));
		private TextField repetetionHoursField = NewAppointmentWindow.this.textField(NewAppointmentWindow.this.repetetionHoursFieldText, false, 23);
		private TextField repetetionMinutesField = NewAppointmentWindow.this.textField(NewAppointmentWindow.this.repetetionMinutesFieldText, false, 59);
		private HBox repetetionTimeBox = NewAppointmentWindow.this.box(new HBox(this.repetetionHoursField, this.repetetionMinutesField));
		private Label repetetionEndLabel = NewAppointmentWindow.this.label(NewAppointmentWindow.this.repetetionEndLabelText);
		private DatePicker repetetionEndPicker = new DatePicker();
		private VBox tabContentRight = NewAppointmentWindow.this.box(new VBox(this.durationLabel, this.durationDaysBox, this.durationTimeBox,
				this.repetetionLabel, this.repetetionDaysBox, this.repetetionTimeBox, this.repetetionEndLabel, this.repetetionEndPicker));
		private HBox tabContent = NewAppointmentWindow.this.box(new HBox(this.tabContentLeft, this.tabContentRight));

		private ChangeListener<Object> updateDateTo = (observable, oldValue, newValue) -> {
			if ((oldValue == null || !oldValue.equals(newValue)) &&
					this.dateFromPicker.getValue() != null) {
				int result = NewAppointmentWindow.this.fieldVal(this.dateFromMinuteField) + NewAppointmentWindow.this.fieldVal(this.durationMinutesField);
				this.dateToMinuteField.setText("" + (result % 60));
				result = result / 60 + NewAppointmentWindow.this.fieldVal(this.dateFromHourField) + NewAppointmentWindow.this.fieldVal(this.durationHoursField);
				this.dateToHourField.setText("" + (result % 24));
				this.dateToPicker.setValue(this.dateFromPicker.getValue().plusDays(result / 24
						+ NewAppointmentWindow.this.fieldVal(this.durationDaysField)).plusMonths(NewAppointmentWindow.this.fieldVal(this.durationMonthsField)));
			}
		};

		private ChangeListener<Object> updateDuration = (observable, oldValue, newValue) -> {
			if ((oldValue == null || !oldValue.equals(newValue)) &&
					this.dateFromPicker.getValue() != null && this.dateToPicker.getValue() != null) {
				int startMinute = NewAppointmentWindow.this.fieldVal(this.dateFromMinuteField);
				int startHour = NewAppointmentWindow.this.fieldVal(this.dateFromHourField);
				int endMinute = NewAppointmentWindow.this.fieldVal(this.dateToMinuteField);
				int endHour = NewAppointmentWindow.this.fieldVal(this.dateToHourField);
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
			this.setText(NewAppointmentWindow.ADD_TAB_TEXT);
			this.initialize();
			this.setClosable(false);
			this.setOnSelectionChanged(e -> {
				if (this.isSelected()) {
					this.setText(String.format("#%02d", NewAppointmentWindow.this.tabPane.getTabs().size()));
					this.setOnSelectionChanged(f -> {});
					this.setClosable(true);
					this.setContextMenu();
					this.setOnCloseRequest(f -> {
						for (int i=NewAppointmentWindow.this.tabPane.getTabs().indexOf(this);i<NewAppointmentWindow.this.tabPane.getTabs().size()-1;i++) {
							NewAppointmentWindow.this.tabPane.getTabs().get(i).setText(String.format("#%02d", i));
						}
					});
					NewAppointmentWindow.this.tabPane.getTabs().add(NewAppointmentWindow.this.tabPane.getTabs().size(), new CustomTab());
				}
			});
		}

		public CustomTab(String string) {
			super(string);
			this.initialize();
			this.setContextMenu();
		}

		private void initialize() {
			this.tabContent.setPadding(new Insets(10));

			this.dateFromPicker.setMaxWidth(Double.MAX_VALUE);
			this.dateToPicker.setMaxWidth(Double.MAX_VALUE);
			this.repetetionEndPicker.setMaxWidth(Double.MAX_VALUE);

			this.durationMinutesField.textProperty().addListener(this.updateDateTo);
			this.durationHoursField.textProperty().addListener(this.updateDateTo);
			this.durationDaysField.textProperty().addListener(this.updateDateTo);
			this.durationMonthsField.textProperty().addListener(this.updateDateTo);

			this.dateFromPicker.valueProperty().addListener(this.updateDuration);
			this.dateFromHourField.textProperty().addListener(this.updateDuration);
			this.dateFromMinuteField.textProperty().addListener(this.updateDuration);
			this.dateToPicker.valueProperty().addListener(this.updateDuration);
			this.dateToHourField.textProperty().addListener(this.updateDuration);
			this.dateToMinuteField.textProperty().addListener(this.updateDuration);

			HBox.setHgrow(this.tabContentRight, ALWAYS);
			HBox.setHgrow(this.tabContentLeft, ALWAYS);

			this.setContent(this.tabContent);
		}

		private void setContextMenu() {
			this.setContextMenu(new ContextMenu(
					new MenuItem(NewAppointmentWindow.this.trans("tabContextMenu", "close"),
							e -> {this.close();}),
					new SeparatorMenuItem(),
					new MenuItem(NewAppointmentWindow.this.trans("tabContextMenu", "closeAll"),
							e -> {for (int i=NewAppointmentWindow.this.tabPane.getTabs().size()-2;i>0;i--) {
								((CustomTab)NewAppointmentWindow.this.tabPane.getTabs().get(i)).close();
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
			NewAppointmentWindow.this.tabPane.getTabs().remove(this);
		}

		GregorianCalendar getDateFrom() {
			return new GregorianCalendar(
					NewAppointmentWindow.this.fieldVal(this.dateFromPicker, o -> o.getYear()),
					NewAppointmentWindow.this.fieldVal(this.dateFromPicker, o -> o.getMonthValue()-1),
					NewAppointmentWindow.this.fieldVal(this.dateFromPicker, o -> o.getDayOfMonth()),
					NewAppointmentWindow.this.fieldVal(this.dateFromHourField),
					NewAppointmentWindow.this.fieldVal(this.dateFromMinuteField));
		}

		GregorianCalendar getDateTo() {
			return new GregorianCalendar(
					NewAppointmentWindow.this.fieldVal(this.dateToPicker, o -> o.getYear()),
					NewAppointmentWindow.this.fieldVal(this.dateToPicker, o -> o.getMonthValue()-1),
					NewAppointmentWindow.this.fieldVal(this.dateToPicker, o -> o.getDayOfMonth()),
					NewAppointmentWindow.this.fieldVal(this.dateToHourField),
					NewAppointmentWindow.this.fieldVal(this.dateToMinuteField));
		}

		Duration getRepetition() {
			return new Duration(
					NewAppointmentWindow.this.fieldVal(this.repetetionMonthsField),
					NewAppointmentWindow.this.fieldVal(this.repetetionDaysField),
					NewAppointmentWindow.this.fieldVal(this.repetetionHoursField),
					NewAppointmentWindow.this.fieldVal(this.repetetionMinutesField));
		}

		GregorianCalendar getRepetitionEnd() {
			return new GregorianCalendar(
					NewAppointmentWindow.this.fieldVal(this.repetetionEndPicker, o -> o.getYear(), this.dateFromPicker),
					NewAppointmentWindow.this.fieldVal(this.repetetionEndPicker, o -> o.getMonthValue()-1, this.dateFromPicker),
					NewAppointmentWindow.this.fieldVal(this.repetetionEndPicker, o -> o.getDayOfMonth(), this.dateFromPicker));
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
					if (!ADD_TAB_TEXT.equals(tabs.get(i).getText())) {
						appointments.add(new DatabaseController.AppointmentItem(
								((CustomTab)tabs.get(i)).getDateFrom(),
								((CustomTab)tabs.get(i)).getDateTo(),
								((CustomTab)tabs.get(i)).getRepetition(),
								((CustomTab)tabs.get(i)).getRepetitionEnd()));
					}
				}
				DatabaseController.addAppointment(new DatabaseController.AppointmentContainer(
						this.titleField.getText(),
						this.descriptionArea.getText(),
						this.categoryBox.getValue(),
						this.priorityBox.getValue(),
						appointments));
				this.stage.close();
			} else {
				OptionsDialog.showMessage(
						Translator.translate(
								"newAppointment", "dialogs", "invalidData", "title"),
						Translator.translate(
								"newAppointment", "dialogs", "invalidData", "message"));
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
			ret.setTextFormatter(new TextFormatter<>(new StringConverter<String>() {
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

	private int fieldVal(DatePicker field, Function<LocalDate, Integer> function, int standard) {
		return field.getValue() != null ? function.apply(field.getValue()) : standard;
	}

	private int fieldVal(DatePicker field, Function<LocalDate, Integer> function) {
		return this.fieldVal(field, function, function.apply(LocalDate.now()));
	}

	private int fieldVal(DatePicker field, Function<LocalDate, Integer> function,
			DatePicker standard) {
		return this.fieldVal(field, function, this.fieldVal(standard, function));
	}
}
