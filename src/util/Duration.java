package util;

import java.util.GregorianCalendar;

public class Duration {
	private short months;
	private long minutes;

	public Duration(int months, int days, long hours, long minutes) {
		this.months = (short)months;
		this.minutes = minutes + 60*(hours + 24*days);
	}

	public Duration(GregorianCalendar from, GregorianCalendar to, boolean monthsAsDays) {
		this.months = (short)(monthsAsDays ? 0 : this.monthsBetween(from, to));
		GregorianCalendar tmp = (GregorianCalendar)from.clone();
		tmp.add(GregorianCalendar.MONTH, this.months);
		this.minutes = (to.getTimeInMillis() - tmp.getTimeInMillis()) / 60000;
	}

	public int getMonths() {
		return this.months;
	}

	public int getDays() {
		return (int)(this.minutes / 60 / 24);
	}

	public int getHours() {
		return (int)(this.minutes / 60 % 24);
	}

	public int getMinutes() {
		return (int)(this.minutes % 60);
	}

	public long getRawMinutes() {
		return this.minutes;
	}

	private int monthsBetween(GregorianCalendar from, GregorianCalendar to) {
		int months = 0;
		GregorianCalendar tmp = (GregorianCalendar)from.clone();
		tmp.add(GregorianCalendar.MONTH, 1);
		while (tmp.before(to)) {
			months++;
			tmp.add(GregorianCalendar.MONTH, 1);
		}
		return months;
	}
}
