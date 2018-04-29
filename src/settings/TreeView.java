package settings;

import javafx.collections.ObservableList;

/**
 * Wrapper Class for {@link javafx.scene.control.TreeView TreeView}, that allows for
 * simpler handling of its childs.
 * @author Mario Schäper
 */
public class TreeView extends javafx.scene.control.TreeView<String> {
	public TreeView() {
		super(new javafx.scene.control.TreeItem<String>());
		this.setShowRoot(false);
	}
	
	/**
	 * @return the children of the root item
	 */
	public ObservableList<javafx.scene.control.TreeItem<String>> getItems() {
		return this.getRoot().getChildren();
	}
	
	public void apply() {
		this.applyRecursive(this.getRoot());
	}

	private void applyRecursive(javafx.scene.control.TreeItem<String> item) {
		if (item.getChildren() == null || item.getChildren().size() == 0) {
			if (item instanceof TreeItem) {
				((TreeItem)item).apply();
				return;
			}
		}
		for (javafx.scene.control.TreeItem<String> subItem : item.getChildren()) {
			this.applyRecursive(subItem);
		}
	}
}
