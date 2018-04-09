package MenuBar;

import javafx.scene.control.MenuItem;

@SuppressWarnings("restriction")
public class Menu extends javafx.scene.control.Menu {
	
	public Menu(String name, MenuItem...menuItems) {
		this.setText(name);
		this.getItems().addAll(menuItems);
	}
}