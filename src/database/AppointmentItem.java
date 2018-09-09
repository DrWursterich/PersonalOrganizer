package database;

import java.util.GregorianCalendar;
import util.Duration;

/**
 * A class to hold data.
 * Serves the purposes of readability and structure.
 * @author Mario Schäper
 */
public class AppointmentItem {
	private GregorianCalendar startDate;
	private GregorianCalendar endDate;
	private Duration repetition;
	private GregorianCalendar repetitionEnd;

	public AppointmentItem(GregorianCalendar startDate, GregorianCalendar endDate,
			Duration repetition, GregorianCalendar repetitionEnd) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.repetition = repetition;
		this.repetitionEnd = repetitionEnd;
	}

	public AppointmentItem(GregorianCalendar startDate, GregorianCalendar endDate) {
		this(startDate, endDate, Duration.NONE, null);
	}

	public GregorianCalendar getStartDate() {
		return this.startDate;
	}

	public GregorianCalendar getEndDate() {
		return this.endDate;
	}

	public Duration getRepetition() {
		return this.repetition;
	}

	public GregorianCalendar getRepetitionEnd() {
		return this.repetitionEnd;
	}
}