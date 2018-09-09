package database.appointment;

import java.util.Calendar;
import java.util.GregorianCalendar;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Class representing an appointment.
 * @author Mario Schäper
 */
public class Appointment {
	private GregorianCalendar startDate;
	private GregorianCalendar endDate;
	private StringProperty subject;
	private StringProperty description;

	/**
	 * Invokes an instance of the class {@link Appointment Appointment}.
	 * @param subject the subject
	 * @param description a description
	 * @param startDate the date, at which the appointment beginns
	 * @param endDate the date, at which the appointment ends
	 */
	public Appointment(String subject, String description,
			GregorianCalendar startDate, GregorianCalendar endDate) {
		this.subject = new SimpleStringProperty(subject);
		this.description = new SimpleStringProperty(description);
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
		this.subject.setValue(subject);
	}

	/**
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description.setValue(description);
	}

	/**
	 * @return the subject property of the {@link Appointment Appointment}
	 */
	public StringProperty subjectProperty() {
		return this.subject;
	}

	/**
	 * @return the description property of the {@link Appointment Appointment}
	 */
	public StringProperty descriptionProperty() {
		return this.description;
	}

	/**
	 * @return the subject of the {@link Appointment Appointment}
	 */
	public String getSubject() {
		return this.subject.getValue();
	}

	/**
	 * @return the description of the {@link Appointment Appointment}
	 */
	public String getDescription() {
		return this.description.getValue();
	}

	/**
	 * @return the year of the date, at which the
	 * {@link Appointment Appointments} beginns.
	 */
	public short getStartYear() {
		return (short)this.startDate.get(Calendar.YEAR);
	}

	/**
	 * @return the month of the date, at which the
	 * {@link Appointment Appointments} beginns.
	 */
	public short getStartMonth() {
		return (short)this.startDate.get(Calendar.MONTH);
	}

	/**
	 * @return the day of the date, at which the
	 * {@link Appointment Appointments} beginns.
	 */
	public short getStartDay() {
		return (short)this.startDate.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * @return the hour of the date, at which the
	 * {@link Appointment Appointments} beginns.
	 */
	public short getStartHour() {
		return (short)this.startDate.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * @return the minute of the date, at which the
	 * {@link Appointment Appointments} beginns.
	 */
	public short getStartMinute() {
		return (short)this.startDate.get(Calendar.MINUTE);
	}

	/**
	 * @return the year of the date, at which the
	 * {@link Appointment Appointments} ends.
	 */
	public short getEndYear() {
		return (short)this.endDate.get(Calendar.YEAR);
	}

	/**
	 * @return the month of the date, at which the
	 * {@link Appointment Appointments} ends.
	 */
	public short getEndMonth() {
		return (short)this.endDate.get(Calendar.MONTH);
	}

	/**
	 * @return the day of the date, at which the
	 * {@link Appointment Appointments} ends.
	 */
	public short getEndDay() {
		return (short)this.endDate.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * @return the hour of the date, at which the
	 * {@link Appointment Appointments} ends.
	 */
	public short getEndHour() {
		return (short)this.endDate.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * @return the minute of the date, at which the
	 * {@link Appointment Appointments} ends.
	 */
	public short getEndMinute() {
		return (short)this.endDate.get(Calendar.MINUTE);
	}
}
