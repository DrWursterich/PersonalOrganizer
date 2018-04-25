package container;

import java.util.ArrayList;

import containerItem.ContainerItem;
import database.DatabaseController;


/**
 * Class representing a container, able to hold
 * {@link ContainerItems ContainerItems}.<br/>
 * @author Mario Schäper
 * @param <T> class of the ContainerItems
 */
public class Container<T extends ContainerItem> {
	protected ArrayList<T> items;

	public Container() {
		this.items = new ArrayList<T>();
	}

	/**
	 * Adds an {@link ContainerItem ContainerItem} to the
	 * {@link Container Container}.<br/>The ContainerItems will
	 * stay sorted ascending by their values.
	 * @param item the item to add
	 */
	public void add(T item) {
		if (!this.items.contains(item)) {
			for (int i=0;i<=this.items.size();i++) {
				if (i == this.items.size() || this.items.get(i).getValue() > item.getValue()) {
					this.items.add(i, item);
					item.getParents().add(this);
					break;
				}
			}
		}
	}

	/**
	 * Returns whether the {@link Container Container} contains an item.<br/>
	 * The comparison is specified by the coresponding item-values.
	 * @param item the item to check for
	 * @return <b>true</b> if the item is contained, <b>false</b> if not
	 */
	@SuppressWarnings("unused")
	public boolean contains(T item) {
		return DatabaseController.search(this.items, item.getValue()) == null ? false : true;
	}

	/**
	 * @return the {@link ContainerItem ContainerItems}
	 */
	public ArrayList<T> getItems() {
		return this.items;
	}
}
