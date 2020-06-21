package database.period;

import database.DatabaseItem;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;

public class Period extends DatabaseItem {
	private final ReadOnlyIntegerWrapper minutesProperty;
	private final ReadOnlyIntegerWrapper daysProperty;
	private final ReadOnlyIntegerWrapper monthsProperty;

	public static Period createInstance(
			final int minutes,
			final int days,
			final int months) {
		return new Period(minutes, days, months);
	}

	public static Period createInstance(
			final int id,
			final int minutes,
			final int days,
			final int months) {
		final Period instance = Period.createInstance(minutes, days, months);
		instance.initializeId(id);
		return instance;
	}

	private Period(final int minutes, final int days, final int months) {
		this.minutesProperty = new ReadOnlyIntegerWrapper(minutes);
		this.daysProperty = new ReadOnlyIntegerWrapper(days);
		this.monthsProperty = new ReadOnlyIntegerWrapper(months);
	}

	public int getMinutes() {
		return this.minutesProperty.get();
	}

	public ReadOnlyIntegerProperty minutesProperty() {
		return this.minutesProperty.getReadOnlyProperty();
	}

	public int getDays() {
		return this.daysProperty.get();
	}

	public ReadOnlyIntegerProperty daysProperty() {
		return this.daysProperty.getReadOnlyProperty();
	}

	public int getMonths() {
		return this.monthsProperty.get();
	}

	public ReadOnlyIntegerProperty monthsProperty() {
		return this.monthsProperty.getReadOnlyProperty();
	}

	@Override
	public String toString() {
		return String.join(
				"&",
				super.toString(),
				"months=" + this.getMonths(),
				"days=" + this.getDays(),
				"minutes=" + this.getMinutes());
	}
}

