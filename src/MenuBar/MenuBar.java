package MenuBar;

import javafx.scene.control.Menu;

@SuppressWarnings("restriction")
public class MenuBar extends javafx.scene.control.MenuBar {
	
	public MenuBar(Menu...menus) {
		this.getMenus().addAll(menus);
		
	}
}