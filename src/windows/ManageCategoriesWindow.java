package windows;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import database.DatabaseController;
import database.appointment.AppointmentGroup;
import database.category.Category;
import database.category.CategoryListCell;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import menus.ContextMenu;
import menus.Menu;
import menus.MenuBar;
import menus.MenuItem;

public class ManageCategoriesWindow extends Window {
	private ListView<Category> categoriesList = new ListView<>();
	private Label nameLabel = this.labelTranslatable(
			"manageCategories.properties.title.label");
	private TextField nameField = this.textFieldTranslatable(
			"manageCategories.properties.title.prompt");
	private VBox nameVBox = this.vBox(this.nameLabel, this.nameField);
	private Label descriptionLabel = this.labelTranslatable(
			"manageCategories.properties.description.label");
	private TextArea descriptionField = this.textAreaTranslatable(
			"manageCategories.properties.description.prompt");
	private VBox descriptionVBox = this.vBox(
			this.descriptionLabel, this.descriptionField);
	private VBox propertiesVBox = this.vBox(
			this.nameVBox, this.descriptionVBox);
	private TitledPane propertiesPane = this.titledPaneTranslatable(
			"manageCategories.properties.name", this.propertiesVBox);
	private ListView<AppointmentGroup> appointmentList = new ListView<>();
	private TitledPane appointmentsPane = this.titledPaneTranslatable(
			"manageCategories.appointments.name", this.appointmentList);
	private Accordion propertiesAccordion = this.accordion(
			this.propertiesPane, this.appointmentsPane);
	private SplitPane contentPane = this.splitPane(
			this.categoriesList, this.propertiesAccordion);
	private ArrayList<Category> changedCategories = new ArrayList<>();

	private class CategoryChangeListener implements ChangeListener<String> {
		private ManageCategoriesWindow parent = ManageCategoriesWindow.this;
		private Callback<Category, String> getter;
		private BiConsumer<Category, String> setter;

		public CategoryChangeListener(
				Callback<Category, String> getter,
				BiConsumer<Category, String> setter) {
			this.getter = getter;
			this.setter = setter;
		}

		@Override
		public void changed(@SuppressWarnings("rawtypes") ObservableValue o, String v, String n) {
			Category category = this.parent.categoriesList.getSelectionModel().getSelectedItem();
			String value = this.getter.call(category);
			if (value != null && !value.equals(n)) {
				if (!this.parent.changedCategories.contains(category)) {
					this.parent.changedCategories.add(category);
				}
			}
			this.setter.accept(category, n);
		}
	}

	protected ManageCategoriesWindow() {
		this.rootTranslatable(this.vBox(this.menuBar(), this.contentPane),
				320, 380, "manageCategories.title");

		this.propertiesVBox.setSpacing(25);

		this.categoriesList.setCellFactory(param -> {
			return new CategoryListCell() {
				@Override
				protected void installContextMenu() {
					MenuItem deleteMenuItem = new MenuItem("manageCategories.menu.edit.delete", e -> {
							ManageCategoriesWindow.this.removeCategory(
									ManageCategoriesWindow.this.categoriesList
										.getSelectionModel().getSelectedItem());
						});
					deleteMenuItem.setDisable(Category.NONE.equals(this.getItem()));
					this.setContextMenu(new ContextMenu(deleteMenuItem));
				}
			};
		});
		this.categoriesList.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
			if (n != null) {
				if (n.equals(Category.NONE)) {
					this.propertiesPane.setDisable(true);
					this.propertiesAccordion.setExpandedPane(null);
				} else {
					this.propertiesPane.setDisable(false);
					this.propertiesAccordion.setExpandedPane(this.propertiesPane);
					this.nameField.setText(n.getName());
					this.descriptionField.setText(n.getDescription());
				}
				this.propertiesAccordion.setDisable(false);
			} else {
				this.propertiesAccordion.setDisable(true);
			}
		});
	}

	@Override
	public void initialize() {
		this.propertiesAccordion.setDisable(true);
		ObservableList<Category> items = FXCollections.observableArrayList(
				DatabaseController.getCategories());
		this.categoriesList.setItems(items);
		if (items != null && items.size() > 0) {
			this.categoriesList.getSelectionModel().select(
					!items.get(0).equals(Category.NONE)
						? items.get(0)
						: items.size() > 1
							? items.get(1)
							: null);
		}
	}

	private MenuBar menuBar() {
		MenuItem deleteMenuItem = new MenuItem("manageCategories.menu.edit.delete", e -> {
				Category item = this.categoriesList.getSelectionModel().getSelectedItem();
				if (item != null) {
					this.removeCategory(item);
				}
			}, "Ctrl+D");
		deleteMenuItem.disableProperty().bind(
				this.categoriesList.getSelectionModel().selectedItemProperty().isEqualTo(
						Category.NONE));
		return new MenuBar(
				new Menu("manageCategories.menu.edit.name",
					new MenuItem("manageCategories.menu.edit.add", e -> {
						WindowController.showWindow(CreateCategoryWindow.class, this.stage);
						this.propertiesAccordion.setDisable(true);
						this.categoriesList.setItems(FXCollections.observableArrayList(
								DatabaseController.getCategories()));
						ObservableList<Category> items = this.categoriesList.getItems();
						if (items != null && items.size() > 0) {
							this.categoriesList.getSelectionModel().select(
									items.get(items.size() - 1));
						}
					}, "Ctrl+N"),
					deleteMenuItem));
	}

	private void removeCategory(Category category) {
		if (OptionsDialog.getBooleanNamespace(
				"manageCategories.dialogs.delete")) {
			this.categoriesList.getItems().remove(category);
			DatabaseController.removeCategory(category);
		}
	}

	private void applyCategoryChanges() {
		DatabaseController.addCategories(this.changedCategories);
		this.changedCategories.clear();
	}

	@Override
	public void initStage(Stage stage) {
		stage.setOnCloseRequest(e -> {
			this.applyCategoryChanges();
		});
	}

	@Override
	public void initSplitPane(SplitPane splitPane) {
		VBox.setVgrow(splitPane, Priority.ALWAYS);
	}

	@Override
	public void initAccordion(Accordion accordion) {
		accordion.expandedPaneProperty().addListener((v, o, n) -> {
			Category selectedCategory = this.categoriesList.getSelectionModel().getSelectedItem();
			if (this.appointmentsPane.equals(n) && selectedCategory != null) {
				this.appointmentList.setItems(FXCollections.observableArrayList(
						DatabaseController.getCategoryAppointments(selectedCategory)));
			}
		});
	}

	@Override
	public void initTextField(TextField textField) {
		textField.textProperty().addListener(
				new CategoryChangeListener(Category::getName, Category::setName));
	}

	@Override
	public void initTextArea(TextArea textArea) {
		textArea.textProperty().addListener(
				new CategoryChangeListener(Category::getDescription, Category::setDescription));
	}
}
