package util;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Represents a Time in minutes as property.<br/>
 * Also contains seperate fields for minutes and hours.
 * @author Mario Schäper
 */
public class Time extends SimpleIntegerProperty {
	private IntegerProperty minutes = new SimpleIntegerProperty(0);
	private IntegerProperty hours = new SimpleIntegerProperty(0);

	public Time(int hour, int minute) {
		this.hours.bind(this.divide(60));
		this.minutes.bind(this.subtract(hours));
		this.setValue(hour*60 + minute);
	}

	public Time(int hour) {
		this(hour, 0);
	}

	public Time() {
		this(0, 0);
	}

	/**
	 * @return the value of the hour field
	 */
	public int getHour() {
		return this.hours.getValue();
	}

	/**
	 * @return the value of the minute field
	 */
	public int getMinute() {
		return this.minutes.getValue();
	}

	/**
	 * @return the property of the hour field
	 */
	public IntegerProperty hourProperty() {
		return this.hours;
	}

	/**
	 * @return the property of the minute field
	 */
	public IntegerProperty minuteProperty() {
		return this.minutes;
	}

	/**
	 * @param hour the new value for the hour field
	 */
	public void setHour(int hour) {
		this.setValue(hour << 8 + this.getMinute());
	}

	/**
	 * @param minute the new value for the minute field
	 */
	public void setMinute(int minute) {
		this.setValue(this.getHour() << 8 + minute);
	}
}
