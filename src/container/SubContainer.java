package container;

import java.util.ArrayList;

import containerItem.ContainerItem;

/**
 * Class representing a {@link Container Container} that
 * can be a {@link ContainerItem ContainerItem}. 
 * @author Mario Schäper
 * @param <T> the ContainerItem class the container can contain
 */
public class SubContainer<T extends ContainerItem> extends Container<T> implements ContainerItem {
	protected short value;
	protected ArrayList<Container<?>> parents;

	SubContainer(short value) {
		super();
		this.value = value;
		this.parents = new ArrayList<Container<?>>();
	}

	/**
	 * @return the value of the {@link SubContainer SubContainer}
	 */
	public long getValue() {
		return this.value;
	}

	/**
	 * {@inheritDoc}
	 * <br/>If the container already holds an item of this item's value,
	 * a {@link DuplicateItemException DuplicateItemException} is thrown. 
	 */
	@Override
	public void add(T item) {
		try {
			for (int i=0;i<=this.items.size();i++) {
				if (i < this.items.size() && this.items.get(i).getValue() == item.getValue()) {
					throw new DuplicateItemException("SubContainer cannot contain "
							+ "multiple ContainerItems of the same value.", item.getValue());
				}
				if (i == this.items.size() || this.items.get(i).getValue() > item.getValue()) {
					this.items.add(i, item);
					item.getParents().add(this);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return all {@link Container Containers} this
	 * 		{@link SubContainer SubContainer} is registered in
	 */
	public ArrayList<Container<?>> getParents() {
		return this.parents;
	}
}
