package views;

import database.DatabaseController;
import javafx.scene.layout.Pane;

public abstract class View extends Pane {
	protected DatabaseController database;
	
	public View(DatabaseController database) {
		super();
		this.database = database;
		this.setHeight(200);
		this.setWidth(200);
		this.parentProperty().addListener((observable, oldValue, newValue) -> {
			this.update();
		});
		this.heightProperty().addListener((observable, oldValue, newValue) -> {
			this.update();
		});
		this.widthProperty().addListener((observable, oldValue, newValue) -> {
			this.update();
		});
	}
	
	public abstract void update();
}
