package container;

import containerItem.ContainerItem;

/**
 * An <em>unchecked Exception</em>, to be thrown when a
 * {@link ContainerItem ContainerItem} is beeing added to a
 * {@link SubContainer SubContainer}, that already holds an item
 * with the same value.<br/>Preventing this is neccessary,
 * since the item-values are used as indices.
 * @author Mario Schäper
 */
public class DuplicateItemException extends RuntimeException {
	private static final long serialVersionUID = -6854571989359725908L;
	private long itemValue;

	/**
	 * Invokes a instance of a
	 * {@link DuplicateItemException DuplicateItemException}.
	 * @param message the message of the exception
	 * @param itemValue the douplicate item-value
	 */
	public DuplicateItemException(String message, long itemValue) {
		super(message);
		this.itemValue = itemValue;
	}

	/**
	 * @return the duplicate item-value
	 */
	public long getItemValue() {
		return this.itemValue;
	}
}
