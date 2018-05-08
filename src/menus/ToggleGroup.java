package menus;

/**
 * Wrapper Class for {@link javafx.scene.control.ToggleGroup ToggleGroup},
 * allowing for inline-invocation.
 * @author Mario Schäper
 */
public class ToggleGroup extends javafx.scene.control.ToggleGroup {
	public static RadioMenuItem[] wrap(RadioMenuItem...menuItems) {
		return ToggleGroup.wrap(0, menuItems);
	}

	public static RadioMenuItem[] wrap(int toggled, RadioMenuItem...menuItems) {
		ToggleGroup toggleGroup = new ToggleGroup();
		for (int i=menuItems.length-1;i>=0;i--) {
			menuItems[i].setToggleGroup(toggleGroup);
			if (i == toggled) {
				menuItems[i].setSelected(true);
			}
		}
		return menuItems;
	}
}
