package views;

import java.util.GregorianCalendar;
import database.DatabaseController;

/**
 * Superclass for all Views related to specific dates
 * @author Mario Schäper
 */
public abstract class DateView extends View {
	protected GregorianCalendar date;

	public DateView(DatabaseController database, GregorianCalendar date) {
		super(database);
		this.loadDate(date);
	}

	/**
	 * changes the views date and updates the view accordingly
	 * @param date the new date
	 */
	public void loadDate(GregorianCalendar date) {
		this.setDate(date);
		this.update();
	}

	/**
	 * @return the date of the view
	 */
	public GregorianCalendar getDate() {
		return date;
	}

	/**
	 * @param date the new date of the view
	 */
	public void setDate(GregorianCalendar date) {
		this.date = date;
	}

}
