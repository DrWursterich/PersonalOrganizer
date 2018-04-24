package views;

import database.DatabaseController;
import javafx.scene.layout.VBox;

@SuppressWarnings("restriction")
public abstract class View extends VBox {
	protected DatabaseController database;

	public View(DatabaseController database) {
		super();
		this.database = database;
		this.setWidth(200);
		this.setHeight(200);
		this.parentProperty().addListener((observable, oldValue, newValue) -> {
			this.update();
		});
	}

	public abstract void update();
}
