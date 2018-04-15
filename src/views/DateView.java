package views;

import java.util.GregorianCalendar;
import database.DatabaseController;

public abstract class DateView extends View {
	protected GregorianCalendar date;

	public DateView(DatabaseController database, GregorianCalendar date) {
		super(database);
		this.loadDate(date);
	}

	public void loadDate(GregorianCalendar date) {
		this.setDate(date);
		this.update();
	}

	public GregorianCalendar getDate() {
		return date;
	}

	public void setDate(GregorianCalendar date) {
		this.date = date;
	}

}
