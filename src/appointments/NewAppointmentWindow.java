package appointments;

import static javafx.scene.layout.Priority.ALWAYS;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
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
	private TextField titleField = this.textField("newAppointment.titlePrompt", true, -1);
	private TextArea descriptionArea = new TextArea();
	private Label categoryLabel = this.label("newAppointment.category.label");
	private ComboBox<Category> categoryBox = new ComboBox<>();
	private VBox categoryContainer = this.box(new VBox(this.categoryLabel, this.categoryBox));
	private Label priorityLabel = this.label("newAppointment.priority.label");
	private ComboBox<Priority> priorityBox = new ComboBox<>();
	private Button createPriorityButton = new Button(NewAppointmentWindow.ADD_TAB_TEXT);
	private Tooltip createPriorityButtonTooltip = new Tooltip();
	private HBox priorityInputs = this.box(new HBox(this.priorityBox, this.createPriorityButton));
	private VBox priorityContainer = this.box(new VBox(this.priorityLabel, this.priorityInputs));
	private HBox comboBoxPane = this.box(new HBox(this.categoryContainer, this.priorityContainer));
	private VBox leftPane = this.box(
			new VBox(this.titleField, this.descriptionArea, this.comboBoxPane));
	private TabPane tabPane = new TabPane();
	private Button cancelButton = new Button();
	private Button acceptButton = new Button();
	private HBox buttonBox = this.box(new HBox(this.cancelButton, this.acceptButton));
	private VBox rightPane = this.box(new VBox(this.tabPane, this.buttonBox));
	private Stage stage = new Stage();
	private SplitPane root = new SplitPane(this.leftPane, this.rightPane);
	private Scene scene = new Scene(this.root);

	private class CustomTab extends Tab {
		private NewAppointmentWindow window = NewAppointmentWindow.this;
		private ObservableList<Tab> tabs = this.window.tabPane.getTabs();
		private Label dateFromLabel = this.window.label("newAppointment.dateFrom.label");
		private DatePicker dateFromPicker = new DatePicker();
		private TextField dateFromHourField =
				this.window.textField("newAppointment.dateFrom.hourPrompt", false, 23);
		private TextField dateFromMinuteField =
				this.window.textField("newAppointment.dateFrom.minutePrompt", false, 59);
		private HBox dateFromTimeBox =
				this.window.box(new HBox(this.dateFromHourField, this.dateFromMinuteField));
		private Label dateToLabel = this.window.label("newAppointment.dateTo.label");
		private DatePicker dateToPicker = new DatePicker();
		private TextField dateToHourField =
				this.window.textField("newAppointment.dateTo.hourPrompt", false, 23);
		private TextField dateToMinuteField =
				this.window.textField("newAppointment.dateTo.minutePrompt", false, 59);
		private HBox dateToTimeBox =
				this.window.box(new HBox(this.dateToHourField, this.dateToMinuteField));
		private VBox tabContentLeft = this.window.box(new VBox(
				this.dateFromLabel, this.dateFromPicker, this.dateFromTimeBox,
				this.dateToLabel, this.dateToPicker, this.dateToTimeBox));
		private Label durationLabel = this.window.label("newAppointment.duration.label");
		private TextField durationMonthsField =
				this.window.textField("newAppointment.duration.monthsPrompt", false, -1);
		private TextField durationDaysField =
				this.window.textField("newAppointment.duration.daysPrompt", false, 30);
		private HBox durationDaysBox = this.window.box(new HBox(
				this.durationMonthsField, this.durationDaysField));
		private TextField durationHoursField =
				this.window.textField("newAppointment.duration.hoursPrompt", false, 23);
		private TextField durationMinutesField =
				this.window.textField("newAppointment.duration.minutesPrompt", false, 59);
		private HBox durationTimeBox = this.window.box(new HBox(
				this.durationHoursField, this.durationMinutesField));
		private Label repetetionLabel = this.window.label("newAppointment.repetetion.label");
		private TextField repetetionMonthsField =
				this.window.textField("newAppointment.repetetion.monthsPrompt", false, -1);
		private TextField repetetionDaysField =
				this.window.textField("newAppointment.repetetion.daysPrompt", false, 30);
		private HBox repetetionDaysBox = this.window.box(new HBox(
				this.repetetionMonthsField, this.repetetionDaysField));
		private TextField repetetionHoursField =
				this.window.textField("newAppointment.repetetion.hoursPrompt", false, 23);
		private TextField repetetionMinutesField =
				this.window.textField("newAppointment.repetetion.minutesPrompt", false, 59);
		private HBox repetetionTimeBox = this.window.box(new HBox(
				this.repetetionHoursField, this.repetetionMinutesField));
		private Label repetetionEndLabel =
				this.window.label("newAppointment.repetetion.endDateLabel");
		private DatePicker repetetionEndPicker = new DatePicker();
		private VBox tabContentRight = this.window.box(new VBox(
				this.durationLabel, this.durationDaysBox, this.durationTimeBox,
				this.repetetionLabel, this.repetetionDaysBox, this.repetetionTimeBox,
				this.repetetionEndLabel, this.repetetionEndPicker));
		private HBox tabContent = this.window.box(new HBox(
				this.tabContentLeft, this.tabContentRight));

		private ChangeListener<Object> updateDateTo = (observable, oldValue, newValue) -> {
			if ((oldValue == null || !oldValue.equals(newValue))
					&& this.dateFromPicker.getValue() != null) {
				int result = this.window.fieldVal(this.dateFromMinuteField)
						+ this.window.fieldVal(this.durationMinutesField);
				this.dateToMinuteField.setText("" + (result % 60));
				result = result / 60 + this.window.fieldVal(this.dateFromHourField)
						+ this.window.fieldVal(this.durationHoursField);
				this.dateToHourField.setText("" + (result % 24));
				this.dateToPicker.setValue(this.dateFromPicker.getValue().plusDays(result / 24
						+ this.window.fieldVal(this.durationDaysField)).plusMonths(
								this.window.fieldVal(this.durationMonthsField)));
			}
		};

		private ChangeListener<Object> updateDuration = (observable, oldValue, newValue) -> {
			if ((oldValue == null || !oldValue.equals(newValue))
					&& this.dateFromPicker.getValue() != null
					&& this.dateToPicker.getValue() != null) {
				int startMinute = this.window.fieldVal(this.dateFromMinuteField);
				int startHour = this.window.fieldVal(this.dateFromHourField);
				int endMinute = this.window.fieldVal(this.dateToMinuteField);
				int endHour = this.window.fieldVal(this.dateToHourField);
				long minutes = TimeUnit.MINUTES.convert((new GregorianCalendar(
							this.dateToPicker.getValue().getYear(),
							this.dateToPicker.getValue().getMonthValue(),
							this.dateToPicker.getValue().getDayOfMonth(), endHour, endMinute)
						).getTimeInMillis() - (new GregorianCalendar(
							this.dateFromPicker.getValue().getYear(),
							this.dateFromPicker.getValue().getMonthValue(),
							this.dateFromPicker.getValue().getDayOfMonth(), startHour, startMinute)
						).getTimeInMillis(), TimeUnit.MILLISECONDS);

				this.durationHoursField.setText("" + (int)(TimeUnit.HOURS.convert(
						minutes - (minutes % 60), TimeUnit.MINUTES) % 24));
				this.durationMinutesField.setText("" + (int)(minutes % 60));
				this.durationDaysField.setText("" + (this.dateFromPicker.getValue().until(
						this.dateToPicker.getValue()).getDays()
						- ((startMinute + 60 * startHour) > (endMinute + 60 * endHour) ? 1 : 0)));
				this.durationMonthsField.setText("" + (this.dateFromPicker.getValue().until(
						this.dateToPicker.getValue()).getMonths()
						+ this.dateFromPicker.getValue().until(
								this.dateToPicker.getValue()).getYears() * 12
						- ((startMinute + 60 * startHour) > (endMinute + 60 * endHour)
								&& this.dateFromPicker.getValue().until(
										this.dateToPicker.getValue()).getDays() == 0 ? 1 : 0)));
			}
		};

		public CustomTab() {
			super();
			this.setText(NewAppointmentWindow.ADD_TAB_TEXT);
			this.initialize();
			this.setClosable(false);
			this.setOnSelectionChanged(e -> {
				if (this.isSelected()) {
					this.setText(String.format("#%02d", this.tabs.size()));
					this.setOnSelectionChanged(f -> {});
					this.setClosable(true);
					this.setContextMenu();
					this.setOnCloseRequest(f -> {
						for (int i = this.tabs.indexOf(this); i < this.tabs.size() - 1; i++) {
							this.tabs.get(i).setText(String.format("#%02d", i));
						}});
					this.tabs.add(this.tabs.size(), new CustomTab());
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
					new MenuItem("newAppointment.tabContextMenu.close", e -> {
								this.close();
							}),
					new SeparatorMenuItem(),
					new MenuItem("newAppointment.tabContextMenu.closeAll", e -> {
							for (int i = this.tabs.size() - 2;i > 0; i--) {
								((CustomTab)this.tabs.get(i)).close();
							};})));
		}

		void close() {
			if (this.getOnCloseRequest() != null) {
				this.getOnCloseRequest().handle(null);
			}
			if (this.getOnClosed() != null) {
				this.getOnClosed().handle(null);
			}
			this.tabs.remove(this);
		}

		GregorianCalendar getDateFrom() {
			return new GregorianCalendar(
					this.window.fieldVal(this.dateFromPicker, o -> o.getYear()),
					this.window.fieldVal(this.dateFromPicker, o -> o.getMonthValue()-1),
					this.window.fieldVal(this.dateFromPicker, o -> o.getDayOfMonth()),
					this.window.fieldVal(this.dateFromHourField),
					this.window.fieldVal(this.dateFromMinuteField));
		}

		GregorianCalendar getDateTo() {
			return new GregorianCalendar(
					this.window.fieldVal(this.dateToPicker, o -> o.getYear()),
					this.window.fieldVal(this.dateToPicker, o -> o.getMonthValue()-1),
					this.window.fieldVal(this.dateToPicker, o -> o.getDayOfMonth()),
					this.window.fieldVal(this.dateToHourField),
					this.window.fieldVal(this.dateToMinuteField));
		}

		Duration getRepetition() {
			return new Duration(
					this.window.fieldVal(this.repetetionMonthsField),
					this.window.fieldVal(this.repetetionDaysField),
					this.window.fieldVal(this.repetetionHoursField),
					this.window.fieldVal(this.repetetionMinutesField));
		}

		GregorianCalendar getRepetitionEnd() {
			return new GregorianCalendar(
					this.window.fieldVal(this.repetetionEndPicker,
							o -> o.getYear(), this.dateFromPicker),
					this.window.fieldVal(this.repetetionEndPicker,
							o -> o.getMonthValue()-1, this.dateFromPicker),
					this.window.fieldVal(this.repetetionEndPicker,
							o -> o.getDayOfMonth(), this.dateFromPicker));
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
		this.descriptionArea.promptTextProperty().bind(
				Translator.translationProperty("newAppointment.descriptionPrompt"));

		this.categoryBox.setMaxWidth(Double.MAX_VALUE);
		this.categoryBox.setPrefWidth(250);
		this.categoryBox.setEditable(true);

		this.priorityBox.setMaxWidth(Double.MAX_VALUE);
		this.priorityBox.setPrefWidth(210);

		this.createPriorityButton.setMinWidth(25);

		this.createPriorityButtonTooltip.textProperty().bind(
				Translator.translationProperty("newAppointment.priority.createTooltip"));
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
				new MenuItem("newAppointment.tabContextMenu.closeAll", e -> {
					for (int i=this.tabPane.getTabs().size()-2;i>0;i--) {
						((CustomTab)this.tabPane.getTabs().get(i)).close();
					};})));

		this.cancelButton.textProperty().bind(Translator.translationProperty("general.cancel"));
		this.cancelButton.setMaxWidth(Double.MAX_VALUE);
		this.cancelButton.setOnAction(e -> {
			this.stage.close();
		});

		this.acceptButton.textProperty().bind(Translator.translationProperty("general.ok"));
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
						Translator.translate("newAppointment.dialogs.invalidData.title"),
						Translator.translate("newAppointment.dialogs.invalidData.message"));
			}
		});

		this.stage.setMinHeight(350);
		this.stage.setMinWidth(380);
		this.stage.titleProperty().bind(Translator.translationProperty("newAppointment.title"));
		this.stage.initModality(Modality.WINDOW_MODAL);
		this.stage.initOwner(parentStage);
		this.stage.setScene(this.scene);
	}

	public void show() {
		this.stage.showAndWait();
	}

	private Label label(String text) {
		Label ret = new Label();
		ret.textProperty().bind(Translator.translationProperty(text));
		return ret;
	}

	private TextField textField(String promptText, boolean allowText, int maxValue) {
		TextField ret = new TextField();
		ret.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(ret, ALWAYS);
		ret.promptTextProperty().bind(Translator.translationProperty(promptText));
		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(Translator.translationProperty(promptText));
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
