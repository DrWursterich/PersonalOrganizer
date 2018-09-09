package database;

/**
 * Abstract Class, that represents a Object holding Data of an Entity from the connected Database.
 * @author Mario Schäper
 */
public abstract class DatabaseItem {
	protected final static int UNASSIGNED_ID = -1;
	private int id = DatabaseItem.UNASSIGNED_ID;

	public final int getId() {
		return this.id;
	}

	public final boolean hasId() {
		return this.id != DatabaseItem.UNASSIGNED_ID;
	}

	protected final void initializeId(int id) {
		if (!this.hasId()) {
			this.id = id;
		}
	}

	/**
	 * Applies the Data of this Instance to the Database Entity of the Objects ID.
	 */
//	abstract void save();

	/**
	 * Applies the Database Data of the Entity with the Objects ID to this Instance.
	 */
//	abstract void load();

	@Override
	public boolean equals(Object that) {
		return that != null && that instanceof DatabaseItem && this.id == ((DatabaseItem)that).id;
	}

	@Override
	public int hashCode() {
		return this.id != DatabaseItem.UNASSIGNED_ID ? this.id : super.hashCode();
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "&id=" + this.id;
	}
}
