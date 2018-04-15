package containerItem;

import java.util.ArrayList;

import container.Container;

/**
 * Classes implementig this interface can be used as
 * {@link ContainerItem Containeritems} for {@link Container Containers}.
 * <br/>ContainerItems have to define a item-value, which is used as
 * index for their Container.
 * @author Mario Schäper
 */
public interface ContainerItem {
	/**
	 * @return the item-value
	 */
	public abstract long getValue();

	/**
	 * @return all {@link Container Containers}, the
	 * 		{@link ContainerItem ContainerItem} is registered in
	 */
	public abstract ArrayList<Container<?>> getParents();
}
