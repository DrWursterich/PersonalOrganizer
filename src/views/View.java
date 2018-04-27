package views;

import database.DatabaseController;
import javafx.scene.layout.VBox;

/**
 * Superclass for views
 * @author Mario Schäper
 */
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

	/**
	 * Refreshes all visuals
	 */
	public abstract void update();
}
