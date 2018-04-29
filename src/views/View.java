package views;

import javafx.scene.layout.VBox;

/**
 * Superclass for views
 * @author Mario Schäper
 */
public abstract class View extends VBox {
	public View() {
		super();
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
