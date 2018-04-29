package database;
import java.util.ArrayList;
import java.util.GregorianCalendar;
//import java.sql.DriverManager;
//import java.sql.SQLException;

import container.*;
import containerItem.Appointment;
import containerItem.ContainerItem;

/*
 * #####################################################################################
 * TODO: THIS CLASS USES A TEMPORARY SUBSTITUTE STORAGE-STRUCTURE INSTEAD OF A DATABASE!
 * #####################################################################################
 */

/**
 * This class controlls the access to a database containing appointments.
 * @author Mario Schäper
 */
public abstract class DatabaseController {
	private static Container<YearContainer> container = new Container<YearContainer>();
//	private String jdbcDriver = "com.mysql.jdbc.Driver";
//	private String dbName = "Appointments";

	/**
	 * Invokes a {@link DatabaseController DatabaseController} instance.
	 */
//	public DatabaseController() {
//		this.initiateDatabase();
//	}

	/**
	 * Initiates the database.
	 */
//	public void initiateDatabase() {
//		try {
//			Class.forName(this.jdbcDriver);
//			DriverManager.getConnection("jdbc:mySql://localhost/?user=root&password=")
//					.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
//		} catch (ClassNotFoundException | SQLException e) {
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * Adds an {@link Appointment Appointments} to the Database.
	 * @param subject the subject
	 * @param description a description
	 * @param startDate the date, at which the appointment beginns
	 * @param endDate the date, at which the appointment ends
	 */
	public static void addAppointment(String subject, String description,
			GregorianCalendar startDate, GregorianCalendar endDate) {
		Appointment appointment = new Appointment(subject, description, startDate, endDate);
		startDate = (GregorianCalendar)startDate.clone();
		endDate = (GregorianCalendar)endDate.clone();
		startDate.set(GregorianCalendar.HOUR, 1);
		endDate.set(GregorianCalendar.HOUR, 2);
		while (startDate.before(endDate)) {
			YearContainer year = search(container.getItems(), startDate.get(GregorianCalendar.YEAR));
			if (year == null) {
				year = new YearContainer((short)startDate.get(GregorianCalendar.YEAR));
				container.add(year);
			}
			MonthContainer month = search(year.getItems(), startDate.get(GregorianCalendar.MONTH));
			if (month == null) {
				month = new MonthContainer((short)startDate.get(GregorianCalendar.MONTH));
				year.add(month);
			}
			DayContainer day = search(month.getItems(), startDate.get(GregorianCalendar.DAY_OF_MONTH));
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
	public static ArrayList<Appointment> getDayAppointments(GregorianCalendar date) {
		YearContainer year = search(container.getItems(), date.get(GregorianCalendar.YEAR));
		if (year != null) {
			MonthContainer month = search(year.getItems(), date.get(GregorianCalendar.MONTH));
			if (month != null) {
				DayContainer day = search(month.getItems(), date.get(GregorianCalendar.DAY_OF_MONTH));
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
	public static <T extends ContainerItem> T search(ArrayList<T> list, long value) {
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
}
