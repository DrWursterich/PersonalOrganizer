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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Callback;
import menus.ContextMenu;
import menus.Menu;
import menus.MenuBar;
import menus.MenuItem;
import util.Translator;

public class ManageCategoriesWindow extends Window {
	private ListView<Category> categoriesList = new ListView<>();
	private Label nameLabel = this.label("manageCategories.properties.title.label");
	private TextField nameField = this.textField("manageCategories.properties.title.prompt");
	private VBox nameVBox = new VBox(this.nameLabel, this.nameField);
	private Label descriptionLabel = this.label("manageCategories.properties.description.label");
	private TextArea descriptionField = this.textArea(
			"manageCategories.properties.description.prompt");
	private VBox descriptionVBox = new VBox(this.descriptionLabel, this.descriptionField);
	private VBox propertiesVBox = new VBox(this.nameVBox, this.descriptionVBox);
	private TitledPane propertiesPane = this.titledPane(
			"manageCategories.properties.name", this.propertiesVBox);
	private ListView<AppointmentGroup> appointmentList = new ListView<>();
	private TitledPane appointmentsPane = this.titledPane(
			"manageCategories.appointments.name", this.appointmentList);
	private Accordion propertiesAccordion = new Accordion(
			this.propertiesPane, this.appointmentsPane);
	private SplitPane contentPane = new SplitPane(this.categoriesList, this.propertiesAccordion);
	private VBox root = new VBox(this.menuBar(), this.contentPane);
	private Scene scene = new Scene(this.root);
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
		this.stage.setMinHeight(320);
		this.stage.setMinWidth(380);
		this.stage.titleProperty().bind(
				Translator.translationProperty("manageCategories.title"));
		this.stage.setScene(this.scene);

		VBox.setVgrow(this.contentPane, Priority.ALWAYS);

		this.categoriesList.setCellFactory(param -> {
			CategoryListCell cell = new CategoryListCell() {
				@Override
				protected void installContextMenu() {
					MenuItem deleteMenuItem = new MenuItem("manageCategories.menu.edit.delete", e -> {
							boolean consent = OptionsDialog.getBoolean(
									Translator.translate(
											"manageCategories.dialogs.delete.title"),
									Translator.translate(
											"manageCategories.dialogs.delete.message"));
							if (consent) {
								this.getListView().getItems().remove(this.getItem());
								DatabaseController.removeCategory(this.getItem());
							}
						});
					deleteMenuItem.setDisable(Category.NONE.equals(this.getItem()));
					this.setContextMenu(new ContextMenu(deleteMenuItem));
				}
			};
			return cell;
		});

		this.nameField.textProperty().addListener(
				new CategoryChangeListener(Category::getName, Category::setName));

		this.descriptionField.textProperty().addListener(
				new CategoryChangeListener(Category::getDescription, Category::setDescription));

		this.propertiesAccordion.expandedPaneProperty().addListener((v, o, n) -> {
			Category selectedCategory = this.categoriesList.getSelectionModel().getSelectedItem();
			if (this.appointmentsPane.equals(n) && selectedCategory != null) {
				this.appointmentList.setItems(FXCollections.observableArrayList(
						DatabaseController.getCategoryAppointments(selectedCategory)));
			}
		});

		this.propertiesVBox.setSpacing(25);

		this.categoriesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
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

		this.stage.setOnCloseRequest(e -> {
			this.applyCategoryChanges();
		});
	}

	@Override
	public void show() {
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
		this.stage.showAndWait();
	}

	private MenuBar menuBar() {
		MenuItem deleteMenuItem = new MenuItem("manageCategories.menu.edit.delete", e -> {
				Category item = this.categoriesList.getSelectionModel().getSelectedItem();
				if (item != null) {
					boolean consent = OptionsDialog.getBoolean(
							Translator.translate(
									"manageCategories.dialogs.delete.title"),
							Translator.translate(
									"manageCategories.dialogs.delete.message"));
					if (consent) {
						this.categoriesList.getItems().remove(item);
						DatabaseController.removeCategory(item);
					}
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

	private Label label(String translationPropertyText) {
		Label ret = new Label();
		ret.textProperty().bind(Translator.translationProperty(translationPropertyText));
		return ret;
	}

//	private <E extends Labeled> E bind(E node, String titleTranslationText) {
//		node.textProperty().bind(Translator.translationProperty(titleTranslationText));
//		return node;
//	}
//
//	private <E extends TextInputControl> E bind(E node, String titleTranslationText) {
//		node.textProperty().bind(Translator.translationProperty(titleTranslationText));
//		return node;
//	}

	private TextField textField(String textTranslationText) {
		TextField ret = new TextField();
		ret.promptTextProperty().bind(Translator.translationProperty(textTranslationText));
		return ret;
	}

	private TextArea textArea(String textTranslationText) {
		TextArea ret = new TextArea();
		ret.promptTextProperty().bind(Translator.translationProperty(textTranslationText));
		return ret;
	}

	private TitledPane titledPane(String titleTranslationPropertyText, Node node) {
		TitledPane ret = new TitledPane();
		ret.textProperty().bind(Translator.translationProperty(titleTranslationPropertyText));
		ret.setContent(node);
		return ret;
	}

	private void applyCategoryChanges() {
		DatabaseController.addCategories(this.changedCategories);
		this.changedCategories.clear();
	}

	@Override
	protected void finalize() {
		this.applyCategoryChanges();
	}
}
