package menus;

import javafx.scene.control.MenuItem;

/**
 * Wrapper Class for {@link javafx.scene.control.ContextMenu ContextMenu}
 * allowing for invocation with {@link javafx.scene.control.MenuItem MenuItems}.
 * @author Mario Schäper
 */
public class ContextMenu extends javafx.scene.control.ContextMenu {
	public ContextMenu(MenuItem...menuItems) {
		this.getItems().addAll(menuItems);
	}
}
