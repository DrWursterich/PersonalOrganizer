package menuBar;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Wrapper Class for {@link javafx.scene.control.MenuItem MenuItem}
 * allowing for invocation with the onAction Event.
 * @author Mario Schäper
 */
public class MenuItem extends javafx.scene.control.MenuItem {
	public MenuItem(String name, EventHandler<ActionEvent> event) {
		this.setText(name);
		this.setOnAction(event);
	}
}
