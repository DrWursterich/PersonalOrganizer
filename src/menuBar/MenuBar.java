package menuBar;

import javafx.scene.control.Menu;

/**
 * Wrapper Class for {@link javafx.scene.control.MenuBar MenuBar}
 * allowing for invocation with {@link javafx.scene.control.Menu Menus}.
 * @author Mario Schäper
 */
public class MenuBar extends javafx.scene.control.MenuBar {
	public MenuBar(Menu...menus) {
		this.getMenus().addAll(menus);
	}
}
