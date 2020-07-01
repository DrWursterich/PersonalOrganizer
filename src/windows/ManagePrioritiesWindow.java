package windows;

import java.util.ArrayList;
import database.DatabaseController;
import database.appointment.AppointmentGroup;
import database.priority.Priority;
import database.priority.PriorityListCell;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import menus.AlarmIterator;
import menus.ContextMenu;
import menus.Menu;
import menus.MenuBar;
import menus.MenuItem;

public class ManagePrioritiesWindow extends Window {
	private ListView<Priority> prioritiesList = new ListView<>();
	private Label nameLabel = this.labelTranslatable(
			"managePriorities.properties.title.label");
	private TextField nameField = this.textFieldTranslatable(
			"managePriorities.properties.title.prompt");
	private VBox nameVBox = this.vBox(this.nameLabel, this.nameField);
	private Label alarmsLabel = this.labelTranslatable(
			"managePriorities.properties.alarms.label");
	private AlarmIterator alarmIterator = new AlarmIterator();
	private ScrollPane alarmPane = this.scrollPane(
			new StackPane(this.alarmIterator));
	private VBox alarmVBox = this.vBox(
			this.alarmsLabel, this.alarmPane);
	private VBox propertiesVBox = this.vBox(
			this.nameVBox, this.alarmVBox);
	private TitledPane propertiesPane = this.titledPaneTranslatable(
			"managePriorities.properties.name", this.propertiesVBox);
	private ListView<AppointmentGroup> appointmentList = new ListView<>();
	private TitledPane appointmentsPane = this.titledPaneTranslatable(
			"managePriorities.appointments.name", this.appointmentList);
	private Accordion propertiesAccordion = this.accordion(
			this.propertiesPane, this.appointmentsPane);
	private SplitPane contentPane = this.splitPane(
			this.prioritiesList, this.propertiesAccordion);
	private ArrayList<Priority> changedPriorities = new ArrayList<>();
	private ChangeListener<Number> changeListener = (v, o, n) -> {
			final Priority priority = this.prioritiesList.getSelectionModel().getSelectedItem();
			if (o != n && n != null && ! this.changedPriorities.contains(priority)) {
				this.changedPriorities.add(priority);
			}
		};

	protected ManagePrioritiesWindow() {
		this.rootTranslatable(this.vBox(this.menuBar(), this.contentPane),
				320, 380, "managePriorities.title");
		this.propertiesVBox.setSpacing(25);
		StackPane.setMargin(this.alarmIterator, new Insets(10));
		this.prioritiesList.setCellFactory(param -> new PriorityListCell() {
				final ManagePrioritiesWindow parent = ManagePrioritiesWindow.this;
				@Override
				protected void installContextMenu() {
					final MenuItem deleteMenuItem = new MenuItem(
							"managePriorities.menu.edit.delete",
							e -> this.parent.removePriority(
								this.parent.prioritiesList.getSelectionModel().getSelectedItem()));
					deleteMenuItem.setDisable(
							Priority.NONE.equals(this.getItem()));
					this.setContextMenu(new ContextMenu(deleteMenuItem));
				}
		});
		this.prioritiesList.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
				if (n != null) {
					if (n.equals(Priority.NONE)) {
						this.propertiesPane.setDisable(true);
						this.propertiesAccordion.setExpandedPane(null);
					} else {
						this.propertiesPane.setDisable(false);
						this.propertiesAccordion.setExpandedPane(this.propertiesPane);
						this.nameField.setText(n.getName());
						this.alarmIterator.applyList(
								n.getAlarmList(),
								(alarm, item) -> {
									final int minutes = alarm.getMinutes();
									item.monthsProperty().removeListener(this.changeListener);
									item.daysProperty().removeListener(this.changeListener);
									item.hoursProperty().removeListener(this.changeListener);
									item.minutesProperty().removeListener(this.changeListener);
									item.setMonths(alarm.getMonths());
									item.setHours(minutes / 60);
									item.setMinutes(minutes % 60);
									item.monthsProperty().addListener(this.changeListener);
									item.daysProperty().addListener(this.changeListener);
									item.hoursProperty().addListener(this.changeListener);
									item.minutesProperty().addListener(this.changeListener);
								});
						this.propertiesAccordion.setDisable(false);
					}
				} else {
					this.propertiesAccordion.setDisable(true);
				}
		});
	}

	@Override
	public void initialize() {
		this.propertiesAccordion.setDisable(true);
		final ObservableList<Priority> items = FXCollections.observableArrayList(
				DatabaseController.getPriorities());
		this.prioritiesList.setItems(items);
		if (items != null && items.size() > 0) {
			this.prioritiesList.getSelectionModel().select(
					items.get(0).equals(Priority.NONE)
						? items.size() > 1
							? items.get(1)
							: null
						: items.get(0));
		}
	}

	private MenuBar menuBar() {
		final MenuItem deleteMenuItem = new MenuItem(
				"managePriorities.menu.edit.delete", e -> {
					final Priority item = this.prioritiesList.getSelectionModel().getSelectedItem();
					if (item != null) {
						this.removePriority(item);
					}
				}, "Ctrl+D");
		deleteMenuItem.disableProperty().bind(
				this.prioritiesList.getSelectionModel().selectedItemProperty().isEqualTo(Priority.NONE));
		return new MenuBar(
			new Menu("managePriorities.menu.edit.name",
				new MenuItem("managePriorities.menu.edit.add", e -> {
					WindowController.showWindow(CreatePriorityWindow.class, this.stage);
					this.propertiesAccordion.setDisable(true);
					this.prioritiesList.setItems(
							FXCollections.observableArrayList(
								DatabaseController.getPriorities()));
					final ObservableList<Priority> items = this.prioritiesList.getItems();
					if (items != null && items.size() > 0) {
						this.prioritiesList.getSelectionModel().select(items.size() - 1);
					}
				}, "Ctrl+N"),
				deleteMenuItem));
	}

	private void removePriority(final Priority priority) {
		if (OptionsDialog.getBooleanNamespace(
				"managePriorities.dialogs.delete")) {
			this.prioritiesList.getItems().remove(priority);
			DatabaseController.removePriority(priority);
		}
	}

	private void applyPriorityChanges() {
		DatabaseController.addPriorities(this.changedPriorities);
		this.changedPriorities.clear();
	}

	@Override
	public void initStage(final Stage stage) {
		stage.setOnCloseRequest(e -> {
			this.applyPriorityChanges();
		});
	}

	@Override
	public void initSplitPane(final SplitPane splitPane) {
		VBox.setVgrow(splitPane, javafx.scene.layout.Priority.ALWAYS);
	}

	@Override
	public void initAccordion(final Accordion accordion) {
		accordion.expandedPaneProperty().addListener((v, o, n) -> {
			final Priority selectedPriority = this.prioritiesList.getSelectionModel().getSelectedItem();
			if (this.appointmentsPane.equals(n) && selectedPriority != null) {
				this.appointmentList.setItems(
						FXCollections.observableArrayList(
							DatabaseController.getPriorityAppointments(
								selectedPriority)));
			}
		});
	}

	@Override
	public void initTextField(final TextField textField) {
		textField.textProperty().addListener((v, o, n) -> {
				final Priority priority = this.prioritiesList.getSelectionModel().getSelectedItem();
				final String value = priority.getName();
				if (value != null && !value.equals(n)) {
					if (!this.changedPriorities.contains(priority)) {
						this.changedPriorities.add(priority);
					}
				}
				priority.setName(n);
		});
	}

	@Override
	public void initScrollPane(final ScrollPane scrollPane) {
		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setFitToWidth(true);
	}
}

