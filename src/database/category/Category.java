package database;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import util.Translator;

public class Category extends DatabaseItem {
	public static final Category NONE = DatabaseController.getCategoryById(1);
	private StringProperty nameProperty;
	private StringProperty descriptionProperty;

	static {
		Category.NONE.nameProperty = Translator.translationProperty("ressources.categories.none");
	}

	public static Category createInstance(String name, String description) {
		return new Category(name, description);
	}

	public static Category createInstance(int id, String name, String description) {
		if (Category.NONE != null && Category.NONE.getId() == id) {
			return Category.NONE;
		}
		Category instance = Category.createInstance(name, description);
		instance.initializeId(id);
		return instance;
	}

	private Category(String name, String description) {
		this.nameProperty = new SimpleStringProperty(name);
		this.descriptionProperty = new SimpleStringProperty(description);
	}

	public StringProperty nameProperty() {
		return this.nameProperty;
	}

	public String getName() {
		return this.nameProperty.getValue();
	}

	public void setName(String name) {
		this.nameProperty.setValue(name);
	}

	public StringProperty descriptionProperty() {
		return this.descriptionProperty;
	}

	public String getDescription() {
		return this.descriptionProperty.getValue();
	}

	public void setDescription(String description) {
		this.descriptionProperty.setValue(description);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && other instanceof Category;
	}
}
