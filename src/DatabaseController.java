import java.util.ArrayList;
import java.util.GregorianCalendar;
//import java.sql.DriverManager;
//import java.sql.SQLException;

/*
 * #####################################################################################
 * TODO: THIS CLASS USES A TEMPORARY SUBSTITUTE STORAGE-STRUCTURE INSTEAD OF A DATABASE!
 * #####################################################################################
 */

/**
 * This class controlls the access to a database containing appointments.
 * @author Mario Schäper
 */
public class DatabaseController {
	private Container<YearContainer> container;
//	private String jdbcDriver = "com.mysql.jdbc.Driver";
//	private String dbName = "Appointments";

	/**
	 * Classes implementig this interface can be used as
	 * {@link ContainerItem Containeritems} for {@link Container Containers}.
	 * <br/>ContainerItems have to define a item-value, which is used as
	 * index for their Container.
	 * @author Mario Schäper
	 */
	private interface ContainerItem {
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

	/**
	 * Class representing a container, able to hold
	 * {@link ContainerItems ContainerItems}.<br/>
	 * @author Mario Schäper
	 * @param <T> class of the ContainerItems
	 */
	private class Container<T extends ContainerItem> {
		protected ArrayList<T> items;

		Container() {
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
			return search(this.items, item.getValue()) == null ? false : true;
		}

		/**
		 * @return the {@link ContainerItem ContainerItems}
		 */
		public ArrayList<T> getItems() {
			return this.items;
		}
	}

	/**
	 * Class representing a {@link Container Container} that
	 * can be a {@link ContainerItem ContainerItem}. 
	 * @author Mario Schäper
	 * @param <T> the ContainerItem class the container can contain
	 */
	private class SubContainer<T extends ContainerItem> extends Container<T> implements ContainerItem {
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

	/**
	 * A {@link SubContainer SubContainer} holding
	 * {@link MonthContainer MonthContainer}-instances.
	 * This class is used purely for structural purposes.
	 * @author Mario Schäper
	 */
	private class YearContainer extends SubContainer<MonthContainer> {
		YearContainer(short value) {
			super(value);
		}
	}

	/**
	 * A {@link SubContainer SubContainer} holding
	 * {@link DayContainer DayContainer}-instances.
	 * This class is used purely for structural purposes.
	 * @author Mario Schäper
	 */
	private class MonthContainer extends SubContainer<DayContainer> {
		MonthContainer(short value) {
			super(value);
		}
	}

	/**
	 * A {@link SubContainer SubContainer} holding
	 * {@link Appointment Appointment}-instances.
	 * This class is used purely for structural purposes.
	 * @author Mario Schäper
	 */
	private class DayContainer extends SubContainer<Appointment> {
		DayContainer(short value) {
			super(value);
		}
	}

	/**
	 * Class representing an appointment.
	 * @author Mario Schäper
	 */
	public class Appointment implements ContainerItem {
		private ArrayList<Container<?>> parents;
		private GregorianCalendar startDate;
		private GregorianCalendar endDate;
		private String subject;
		private String description;

		/**
		 * Invokes an instance of the class {@link Appointment Appointment}.
		 * @param subject the subject
		 * @param description a description
		 * @param startDate the date, at which the appointment beginns
		 * @param endDate the date, at which the appointment ends
		 */
		private Appointment(String subject, String description,
				GregorianCalendar startDate, GregorianCalendar endDate) {
			this.parents = new ArrayList<Container<?>>();
			this.subject = subject;
			this.description = description;
			this.startDate = startDate;
			this.endDate = endDate;
		}

		/**
		 * @param startDate the new start date
		 */
		public void setStartDate(GregorianCalendar startDate) {
			this.startDate = startDate;
		}

		/**
		 * @param endDate the new end date
		 */
		public void setEndDate(GregorianCalendar endDate) {
			this.endDate = endDate;
		}

		/**
		 * @param subject the new subject
		 */
		public void setSubject(String subject) {
			this.subject = subject;
		}

		/**
		 * @param description the new description
		 */
		public void setDescription(String description) {
			this.description = description;
		}

		/**
		 * @return the number of milliseconds since January 1,
		 * 1970, 00:00:00 GMT represented by the start date.
		 */
		public long getValue() {
			return this.startDate.getTime().getTime();
		}

		/**
		 * @return all {@link DayContainer DayContainers} this
		 * 		{@link Appointment Appointment} is registered in
		 */
		public ArrayList<Container<?>> getParents() {
			return this.parents;
		}

		/**
		 * @return the subject of the {@link Appointment Appointment}
		 */
		public String getSubject() {
			return this.subject;
		}

		/**
		 * @return the description of the {@link Appointment Appointment}
		 */
		public String getDescription() {
			return this.description;
		}

		/**
		 * @return the year of the date, at which the
		 * {@link Appointment Appointments} beginns.
		 */
		public short getStartYear() {
			return (short)this.startDate.get(GregorianCalendar.YEAR);
		}

		/**
		 * @return the month of the date, at which the
		 * {@link Appointment Appointments} beginns.
		 */
		public short getStartMonth() {
			return (short)this.startDate.get(GregorianCalendar.MONTH);
		}

		/**
		 * @return the day of the date, at which the
		 * {@link Appointment Appointments} beginns.
		 */
		public short getStartDay() {
			return (short)this.startDate.get(GregorianCalendar.DAY_OF_MONTH);
		}

		/**
		 * @return the hour of the date, at which the
		 * {@link Appointment Appointments} beginns.
		 */
		public short getStartHour() {
			return (short)this.startDate.get(GregorianCalendar.HOUR);
		}

		/**
		 * @return the minute of the date, at which the
		 * {@link Appointment Appointments} beginns.
		 */
		public short getStartMinute() {
			return (short)this.startDate.get(GregorianCalendar.MINUTE);
		}

		/**
		 * @return the year of the date, at which the
		 * {@link Appointment Appointments} ends.
		 */
		public short getEndYear() {
			return (short)this.endDate.get(GregorianCalendar.YEAR);
		}

		/**
		 * @return the month of the date, at which the
		 * {@link Appointment Appointments} ends.
		 */
		public short getEndMonth() {
			return (short)this.endDate.get(GregorianCalendar.MONTH);
		}

		/**
		 * @return the day of the date, at which the
		 * {@link Appointment Appointments} ends.
		 */
		public short getEndDay() {
			return (short)this.endDate.get(GregorianCalendar.DAY_OF_MONTH);
		}

		/**
		 * @return the hour of the date, at which the
		 * {@link Appointment Appointments} ends.
		 */
		public short getEndHour() {
			return (short)this.endDate.get(GregorianCalendar.HOUR);
		}

		/**
		 * @return the minute of the date, at which the
		 * {@link Appointment Appointments} ends.
		 */
		public short getEndMinute() {
			return (short)this.endDate.get(GregorianCalendar.MINUTE);
		}
	}

	/**
	 * Adds an {@link Appointment Appointments} to the Database.
	 * @param subject the subject
	 * @param description a description
	 * @param startDate the date, at which the appointment beginns
	 * @param endDate the date, at which the appointment ends
	 */
	public void addAppointment(String subject, String description,
			GregorianCalendar startDate, GregorianCalendar endDate) {
		Appointment appointment = new Appointment(subject, description, startDate, endDate);
		startDate.set(GregorianCalendar.HOUR, 1);
		endDate.set(GregorianCalendar.HOUR, 2);
		while (startDate.before(endDate)) {
			YearContainer year = this.search(this.container.getItems(), startDate.get(GregorianCalendar.YEAR));
			if (year == null) {
				year = new YearContainer((short)startDate.get(GregorianCalendar.YEAR));
				this.container.add(year);
			}
			MonthContainer month = this.search(year.getItems(), startDate.get(GregorianCalendar.MONTH));
			if (month == null) {
				month = new MonthContainer((short)startDate.get(GregorianCalendar.MONTH));
				year.add(month);
			}
			DayContainer day = this.search(month.getItems(), startDate.get(GregorianCalendar.DAY_OF_MONTH));
			if (day == null) {
				day = new DayContainer((short)startDate.get(GregorianCalendar.DAY_OF_MONTH));
				month.add(day);
			}
			day.add(appointment);
			startDate.add(GregorianCalendar.DAY_OF_MONTH, 1);
		}
	}

	/**
	 * Returns an {@link java.util.ArrayList ArrayList} of all
	 * {@link Appointment Appointments} for the given day.
	 * @param date specifies the day
	 * @return list of all appointments sorted by starttime
	 */
	public ArrayList<Appointment> getDayAppointments(GregorianCalendar date) {
		YearContainer year = this.search(this.container.getItems(), date.get(GregorianCalendar.YEAR));
		if (year != null) {
			MonthContainer month = this.search(year.getItems(), date.get(GregorianCalendar.MONTH));
			if (month != null) {
				DayContainer day = this.search(month.getItems(), date.get(GregorianCalendar.DAY_OF_MONTH));
				if (day != null) {
					return day.getItems();
				}
			}
		}
		return null;
	}

	/**
	 * Returns the {@link ContainerItem ContainerItem} with the given
	 * value from an {@link java.util.ArrayList ArrayList}, found via
	 * binary search.<br/>
	 * If it does not contain a suitable item <b>null</b> is returned.
	 * @param list the list to search in
	 * @param value the item-value to look for
	 * @return item with specified value or null
	 */
	private <T extends ContainerItem> T search(ArrayList<T> list, long value) {
		if (list.size() > 0) {
			int startIndex = 0;
			int endIndex = list.size()-1;
			if (list.get(startIndex).getValue() == value) {
				return list.get(startIndex);
			}
			if (list.get(endIndex).getValue() == value) {
				return list.get(endIndex);
			}
			if (value > list.get(startIndex).getValue() && value < list.get(endIndex).getValue()) {
				while (endIndex-startIndex > 1) {
					int midIndex = startIndex + (endIndex-startIndex)/2;
					if (list.get(midIndex).getValue() == value) {
						return list.get(midIndex);
					} else {
						if (list.get(midIndex).getValue() > value) {
							endIndex = midIndex;
						} else {
							startIndex = midIndex;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Invokes a {@link DatabaseController DatabaseController} instance.
	 */
	public DatabaseController() {
		this.initiateDatabase();
	}

	/**
	 * Initiates the database.
	 */
	public void initiateDatabase() {
		this.container = new Container<YearContainer>();
//		try {
//			Class.forName(this.jdbcDriver);
//			DriverManager.getConnection("jdbc:mySql://localhost/?user=root&password=")
//					.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
//		} catch (ClassNotFoundException | SQLException e) {
//			e.printStackTrace();
//		}
	}
}
