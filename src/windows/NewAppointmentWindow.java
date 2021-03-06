package windows;

import static javafx.scene.layout.Priority.ALWAYS;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import database.DatabaseController;
import database.appointment.AppointmentGroup;
import database.appointment.AppointmentItem;
import database.category.Category;
import database.category.CategoryListCell;
import database.priority.Priority;
import database.priority.PriorityListCell;
import menus.DatePicker;
import menus.ContextMenu;
import menus.MenuItem;
import util.Duration;
import util.NumberField;

public class NewAppointmentWindow extends Window {
	private static final String ADD_TAB_TEXT = " + ";
	private TextField titleField = this.textFieldTranslatable("newAppointment.titlePrompt");
	private TextArea descriptionArea = this.textAreaTranslatable("newAppointment.descriptionPrompt");
	private Label categoryLabel = this.labelTranslatable("newAppointment.category.label");
	private ComboBox<Category> categoryBox = this.comboBox(Category.class);
	private Button manageCategoryButton = this.button(NewAppointmentWindow.ADD_TAB_TEXT);
	private HBox categoryInputs = this.hBox(this.categoryBox, this.manageCategoryButton);
	private VBox categoryContainer = this.vBox(this.categoryLabel, this.categoryInputs);
	private Label priorityLabel = this.labelTranslatable("newAppointment.priority.label");
	private ComboBox<Priority> priorityBox = this.comboBox(Priority.class);
	private Button managePriorityButton = this.button(NewAppointmentWindow.ADD_TAB_TEXT);
	private HBox priorityInputs = this.hBox(this.priorityBox, this.managePriorityButton);
	private VBox priorityContainer = this.vBox(this.priorityLabel, this.priorityInputs);
	private HBox comboBoxPane = this.hBox(this.categoryContainer, this.priorityContainer);
	private VBox leftPane = this.vBox(this.titleField, this.descriptionArea, this.comboBoxPane);
	private TabPane tabPane = new TabPane();
	private CustomTab firstTab = new CustomTab("#01");
	private Button cancelButton = this.buttonTranslatable("general.cancel");
	private Button acceptButton = this.buttonTranslatable("general.ok");
	private HBox buttonBox = this.hBox(this.cancelButton, this.acceptButton);
	private VBox rightPane = this.vBox(this.tabPane, this.buttonBox);

	private class CustomTab extends Tab implements NodeInitializer {
		private NewAppointmentWindow window = NewAppointmentWindow.this;
		private ObservableList<Tab> tabs = this.window.tabPane.getTabs();
		private Label dateFromLabel = this.labelTranslatable("newAppointment.dateFrom.label");
		private DatePicker dateFromPicker = this.datePicker();
		private NumberField dateFromHourField = this.numberFieldTranslatable(
				"newAppointment.dateFrom.hourPrompt", 0, 23);
		private NumberField dateFromMinuteField = this.numberFieldTranslatable(
				"newAppointment.dateFrom.minutePrompt", 0, 59);
		private HBox dateFromTimeBox =this.hBox(
				this.dateFromHourField, this.dateFromMinuteField);
		private Label dateToLabel = this.labelTranslatable("newAppointment.dateTo.label");
		private DatePicker dateToPicker = this.datePicker();
		private NumberField dateToHourField = this.numberFieldTranslatable(
				"newAppointment.dateTo.hourPrompt", 0, 23);
		private NumberField dateToMinuteField = this.numberFieldTranslatable(
				"newAppointment.dateTo.minutePrompt", 0, 59);
		private HBox dateToTimeBox = this.hBox(this.dateToHourField, this.dateToMinuteField);
		private VBox tabContentLeft = this.vBox(
				this.dateFromLabel, this.dateFromPicker, this.dateFromTimeBox,
				this.dateToLabel, this.dateToPicker, this.dateToTimeBox);
		private Label durationLabel = this.labelTranslatable("newAppointment.duration.label");
		private NumberField durationMonthsField =
				this.numberFieldTranslatable("newAppointment.duration.monthsPrompt", 0, Integer.MAX_VALUE);
		private NumberField durationDaysField =
				this.numberFieldTranslatable("newAppointment.duration.daysPrompt", 0, 30);
		private HBox durationDaysBox = this.hBox(this.durationMonthsField, this.durationDaysField);
		private NumberField durationHoursField =
				this.numberFieldTranslatable("newAppointment.duration.hoursPrompt", 0, 23);
		private NumberField durationMinutesField =
				this.numberFieldTranslatable("newAppointment.duration.minutesPrompt", 0, 59);
		private HBox durationTimeBox = this.hBox(this.durationHoursField, this.durationMinutesField);
		private Label repetetionLabel = this.labelTranslatable("newAppointment.repetetion.label");
		private NumberField repetetionMonthsField =
				this.numberFieldTranslatable("newAppointment.repetetion.monthsPrompt", 0, Integer.MAX_VALUE);
		private NumberField repetetionDaysField =
				this.numberFieldTranslatable("newAppointment.repetetion.daysPrompt", 0, 30);
		private HBox repetetionDaysBox = this.hBox(
				this.repetetionMonthsField, this.repetetionDaysField);
		private NumberField repetetionHoursField =
				this.numberFieldTranslatable("newAppointment.repetetion.hoursPrompt", 0, 23);
		private NumberField repetetionMinutesField =
				this.numberFieldTranslatable("newAppointment.repetetion.minutesPrompt", 0, 59);
		private HBox repetetionTimeBox = this.hBox(
				this.repetetionHoursField, this.repetetionMinutesField);
		private Label repetetionEndLabel =
				this.labelTranslatable("newAppointment.repetetion.endDateLabel");
		private DatePicker repetetionEndPicker = this.datePicker();
		private VBox tabContentRight = this.vBox(
				this.durationLabel, this.durationDaysBox, this.durationTimeBox,
				this.repetetionLabel, this.repetetionDaysBox, this.repetetionTimeBox,
				this.repetetionEndLabel, this.repetetionEndPicker);
		private HBox tabContent = this.hBox(this.tabContentLeft, this.tabContentRight);

		private ChangeListener<Object> updateDateTo = (observable, oldValue, newValue) -> {
			if ((oldValue == null || !oldValue.equals(newValue))
					&& this.dateFromPicker.getValue() != null) {
				int result = this.dateFromMinuteField.getValue()
						+ this.durationMinutesField.getValue();
				this.dateToMinuteField.setText("" + (result % 60));
				result = result / 60
						+ this.dateFromHourField.getValue()
						+ this.durationHoursField.getValue();
				this.dateToHourField.setText("" + (result % 24));
				this.dateToPicker.setValue(
						this.dateFromPicker.getValue().plusDays(
								result / 24
								+ this.durationDaysField.getValue()
						).plusMonths(
								this.durationMonthsField.getValue()));
			}
		};

		private ChangeListener<Object> updateDuration = (observable, oldValue, newValue) -> {
			if ((oldValue == null || !oldValue.equals(newValue))
					&& this.dateFromPicker.getValue() != null
					&& this.dateToPicker.getValue() != null) {
				int startMinute = this.dateFromMinuteField.getValue();
				int startHour = this.dateFromHourField.getValue();
				int endMinute = this.dateToMinuteField.getValue();
				int endHour = this.dateToHourField.getValue();
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
					this.dateFromPicker.getYear(),
					this.dateFromPicker.getMonth(),
					this.dateFromPicker.getDayOfMonth(),
					this.dateFromHourField.getValue(),
					this.dateFromMinuteField.getValue());
		}

		GregorianCalendar getDateTo() {
			return new GregorianCalendar(
					this.dateToPicker.getYear(),
					this.dateToPicker.getMonth(),
					this.dateToPicker.getDayOfMonth(),
					this.dateToHourField.getValue(),
					this.dateToMinuteField.getValue());
		}

		Duration getRepetition() {
			return new Duration(
					this.repetetionMonthsField.getValue(),
					this.repetetionDaysField.getValue(),
					this.repetetionHoursField.getValue(),
					this.repetetionMinutesField.getValue());
		}

		GregorianCalendar getRepetitionEnd() {
			return new GregorianCalendar(
					this.repetetionEndPicker.getYear(
							this.dateFromPicker.getYear()),
					this.repetetionEndPicker.getMonth(
							this.dateFromPicker.getMonth()),
					this.repetetionEndPicker.getDayOfMonth(
							this.dateFromPicker.getDayOfMonth()));
		}

		@Override
		public void initHBox(HBox hBox) {
			NewAppointmentWindow.this.initHBox(hBox);
		}

		@Override
		public void initVBox(VBox vBox) {
			NewAppointmentWindow.this.initVBox(vBox);
			HBox.setHgrow(vBox, ALWAYS);
		}

		@Override
		public void initTextField(TextField textField) {
			NewAppointmentWindow.this.initTextField(textField);
		}

		@Override
		public void initNumberField(NumberField numberField) {
			this.initTextField(numberField);
		}

		@Override
		public void initDatePicker(DatePicker datePicker) {
			datePicker.setMaxWidth(Double.MAX_VALUE);
		}
	}

	protected NewAppointmentWindow() {
		this.rootTranslatable(
				this.splitPane(this.leftPane, this.rightPane),
				350, 380, "newAppointment.title");
		this.tooltipTranslatable(
				"newAppointment.category.manageTooltip",
				this.manageCategoryButton);
		this.tooltipTranslatable(
				"newAppointment.priority.manageTooltip",
				this.managePriorityButton);
		HBox.setHgrow(this.categoryInputs, ALWAYS);
		HBox.setHgrow(this.categoryContainer, ALWAYS);
		HBox.setHgrow(this.priorityInputs, ALWAYS);
		HBox.setHgrow(this.priorityContainer, ALWAYS);

		this.leftPane.setPadding(new Insets(10));

		this.categoryBox.getItems().addAll(DatabaseController.getCategories());
		this.categoryBox.setCellFactory(e -> new CategoryListCell());
		this.categoryBox.setButtonCell(new CategoryListCell());
		this.categoryBox.getSelectionModel().select(Category.NONE);

		this.priorityBox.getItems().addAll(DatabaseController.getPriorities());
		this.priorityBox.setCellFactory(e -> new PriorityListCell());
		this.priorityBox.setButtonCell(new PriorityListCell());
		this.priorityBox.getSelectionModel().select(Priority.NONE);

		this.rightPane.setPadding(new Insets(10));

		this.initTabPane(this.tabPane);

		this.firstTab.setClosable(false);
		this.firstTab.setContextMenu(new ContextMenu(
				new MenuItem("newAppointment.tabContextMenu.closeAll", e -> {
					for (int i=this.tabPane.getTabs().size()-2;i>0;i--) {
						((CustomTab)this.tabPane.getTabs().get(i)).close();
					};})));

		this.cancelButton.setOnAction(e -> {
			this.stage.close();});

		this.acceptButton.setOnAction(e -> {
			if (this.titleField.getText() != null && this.titleField.getText() != ""
					&& this.firstTab.getDateFrom().before(this.firstTab.getDateTo())) {
				ArrayList<AppointmentItem> appointments = new ArrayList<>();
				ObservableList<Tab> tabs = this.tabPane.getTabs();
				for (int i=0;i<tabs.size();i++) {
					if (!ADD_TAB_TEXT.equals(tabs.get(i).getText())
							&& tabs.get(i) instanceof CustomTab) {
						CustomTab tab = (CustomTab)tabs.get(i);
						appointments.add(new AppointmentItem(
								tab.getDateFrom(),
								tab.getDateTo(),
								tab.getRepetition(),
								tab.getRepetitionEnd()));
					}
				}
				DatabaseController.addAppointment(new AppointmentGroup(
						this.titleField.getText(),
						this.descriptionArea.getText(),
						this.categoryBox.getValue(),
						this.priorityBox.getValue(),
						appointments));
				this.stage.close();
			} else {
				OptionsDialog.showMessageNamespace(
						"newAppointment.dialogs.invalidData");
			}});

		this.manageCategoryButton.setOnAction(e -> {
			WindowController.showWindow(ManageCategoriesWindow.class, this.stage);});
		this.managePriorityButton.setOnAction(e -> {
			WindowController.showWindow(ManagePrioritiesWindow.class, this.stage);});
	}

	@Override
	public void show() {
		this.stage.showAndWait();
	}

	@Override
	public void initTextField(TextField textField) {
		textField.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(textField, ALWAYS);
		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(textField.promptTextProperty());
		textField.setTooltip(tooltip);
	}

	@Override
	public void initTextArea(TextArea textArea) {
		VBox.setVgrow(textArea, ALWAYS);
		textArea.setPrefWidth(300);
		textArea.setMaxWidth(Double.MAX_VALUE);
	}

	@Override
	public void initButton(Button button) {
		HBox.setHgrow(button, ALWAYS);
		button.setMaxWidth(Double.MAX_VALUE);
		button.setMinWidth(40);
	}

	@Override
	public void initComboBox(ComboBox<?> comboBox) {
		HBox.setHgrow(comboBox, ALWAYS);
		comboBox.setMaxWidth(Double.MAX_VALUE);
		comboBox.setPrefWidth(210);
	}

	@Override
	public void initHBox(HBox hBox) {
		hBox.setSpacing(5);
	}

	@Override
	public void initVBox(VBox vBox) {
		vBox.setSpacing(5);
	}

	@Override
	public void initTabPane(TabPane tabPane) {
		VBox.setVgrow(tabPane, ALWAYS);
		tabPane.setPrefWidth(300);
		tabPane.setMinWidth(200);
		tabPane.setTabMinWidth(25);
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		tabPane.getTabs().addAll(this.firstTab, new CustomTab());
	}
}
