package menus;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;

/**
 * Wrapper Class for {@link javafx.scene.control.Menu Menu}
 * allowing for invocation with {@link javafx.scene.control.MenuItem MenuItems}.
 * @author Mario Schäper
 */
public class Menu extends javafx.scene.control.Menu {
	public Menu(String name, MenuItem...menuItems) {
		Label label = new Label(name);
		label.setTextFill(Color.BLACK);
		this.setGraphic(label);
		this.getItems().addAll(menuItems);
	}

	public Menu(StringProperty name, MenuItem...menuItems) {
		Label label = new Label();
		label.textProperty().bind(name);
		label.setTextFill(Color.BLACK);
		this.setGraphic(label);
		this.getItems().addAll(menuItems);
	}

	public Menu(String name, EventHandler<ActionEvent> onAction, MenuItem...menuItems) {
		this(name, menuItems);
		this.setOnAction(onAction);
	}

	public Menu(StringProperty name, EventHandler<ActionEvent> onAction, MenuItem...menuItems) {
		this(name, menuItems);
		this.setOnAction(onAction);
	}
}
