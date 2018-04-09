package MenuBar;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

@SuppressWarnings("restriction")
public class MenuItem extends javafx.scene.control.MenuItem {
	public MenuItem(String name, EventHandler<ActionEvent> event) {
		this.setText(name);
		this.setOnAction(event);
	}
}