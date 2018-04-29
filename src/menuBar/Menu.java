package menuBar;

import javafx.scene.control.MenuItem;
import javafx.beans.property.StringProperty;

/**
 * Wrapper Class for {@link javafx.scene.control.Menu Menu}
 * allowing for invocation with {@link javafx.scene.control.MenuItem MenuItems}.
 * @author Mario Schäper
 */
public class Menu extends javafx.scene.control.Menu {
	public Menu(String name, MenuItem...menuItems) {
		this.setText(name);
		this.getItems().addAll(menuItems);
	}
	
	public Menu(StringProperty name, MenuItem...menuItems) {
		this.textProperty().bind(name);;
		this.getItems().addAll(menuItems);
	}
}
