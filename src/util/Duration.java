package util;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Immutable class to represent a timeperiod defined by months and minutes.
 * @author Mario Schäper
 */
public class Duration implements Comparable<Duration> {
	public final static Duration NONE = new Duration(0, 0, 0, 0);
	private final static int MIN_MINUTES_PER_MONTH = 27 * 24 * 60;
	private final static int MAX_MINUTES_PER_MONTH = 31 * 24 * 60;

	private final short months;
	private final long minutes;

	public Duration(int months, int days, long hours, long minutes) {
		this.months = (short)months;
		this.minutes = minutes + 60 * (hours + 24 * days);
	}

	public Duration(GregorianCalendar from, GregorianCalendar to, boolean monthsAsDays) {
		this.months = (short)(monthsAsDays ? 0 : this.monthsBetween(from, to));
		GregorianCalendar tmp = (GregorianCalendar)from.clone();
		tmp.add(Calendar.MONTH, this.months);
		this.minutes = (to.getTimeInMillis() - tmp.getTimeInMillis()) / 60000;
	}

	public Duration(GregorianCalendar from, GregorianCalendar to) {
		this(from, to, false);
	}

	/**
	 * Returns the amount of months.
	 * @return months
	 */
	public int getMonths() {
		return this.months;
	}

	/**
	 * Returns the amount of days.
	 * @return days
	 */
	public int getDays() {
		return (int)(this.minutes / 60 / 24);
	}

	/**
	 * Returns the amount of hours.
	 * @return hours
	 */
	public int getHours() {
		return (int)(this.minutes / 60 % 24);
	}

	/**
	 * Returns the amount of minutes.
	 * @return minuts
	 */
	public int getMinutes() {
		return (int)(this.minutes % 60);
	}

	/**
	 * Returns the minutes, hours and days in minutes.
	 * @return minutes
	 */
	public long getRawMinutes() {
		return this.minutes;
	}

	/**
	 * Returns a {@link java.util.GregorianCalendar GregorianCalendar} instance
	 * with the summed values of both parameters.
	 * @param augend the augend
	 * @param addend the addend
	 * @return resulting {@link java.util.GregorianCalendar GregorianCalendar} instance
	 */
	public static GregorianCalendar add(GregorianCalendar augend, Duration addend) {
		GregorianCalendar ret = (GregorianCalendar)augend.clone();
		ret.add(Calendar.MONTH, addend.months);
		ret.add(Calendar.DAY_OF_MONTH, addend.getDays());
		ret.add(Calendar.HOUR_OF_DAY, addend.getHours());
		ret.add(Calendar.MINUTE, addend.getMinutes());
		return ret;
	}

	/**
	 * Returns a {@link Duration Duration} instance with the summed values of both parameters.
	 * @param augend the augend
	 * @param addend the addend
	 * @return resulting {@link Duration Duration} instance
	 */
	public static Duration add(Duration augend, Duration addend) {
		return new Duration(augend.months + addend.months, 0, 0, augend.minutes + addend.minutes);
	}

	/**
	 * Returns a {@link java.util.GregorianCalendar GregorianCalendar} instance
	 * with the subtracted values of the subtrahend from the minuend.
	 * @param minuend the minuend
	 * @param subtrahend the subtrahend
	 * @return resulting {@link java.util.GregorianCalendar GregorianCalendar} instance
	 */
	public static GregorianCalendar subtract(GregorianCalendar minuend, Duration subtrahend) {
		GregorianCalendar ret = (GregorianCalendar)minuend.clone();
		ret.add(Calendar.MINUTE, -subtrahend.getMinutes() * -1);
		ret.add(Calendar.HOUR_OF_DAY, -subtrahend.getHours());
		ret.add(Calendar.DAY_OF_MONTH, -subtrahend.getDays());
		ret.add(Calendar.MONTH, -subtrahend.months);
		return ret;
	}

	/**
	 * Returns a {@link Duration Duration} instance with the subtracted values
	 * form the subtrahend of the minuend.
	 * @param minuend the minuend
	 * @param subtrahend the subtrahend
	 * @return resulting {@link Duration Duration} instance
	 */
	public static Duration subtract(Duration minuend, Duration subtrahend) {
		return new Duration(minuend.months - subtrahend.months, 0, 0,
				minuend.minutes - subtrahend.minutes);
	}

	/**
	 * Returns a {@link Duration Duration} instance with the values from the multiplicand
	 * multiplied by the multiplier.
	 * @param multiplicand the multiplicand
	 * @param multiplier the multiplier
	 * @returnresulting {@link Duration Duration} instance
	 */
	public static Duration multiply(Duration multiplicand, int multiplier) {
		return new Duration(multiplicand.months * multiplier, 0, 0,
				multiplicand.minutes * multiplier);
	}

	/**
	 * Returns a {@link Duration Duration} instance with the values of the quotient of the
	 * dividend and divisor. Months and minutes are divided seperatly.
	 * Floatingpoint months are replaced by the minimum of days of a month as
	 * minutes. Resulting values are rounded down.
	 * @param dividend the dividend
	 * @param divisor the divisor
	 * @return resulting {@link Duration Duration} instance
	 */
	public static Duration divide(Duration dividend, int divisor) {
		return new Duration(dividend.months / divisor, 0, 0, dividend.minutes / divisor
				+ (dividend.months % divisor) * MIN_MINUTES_PER_MONTH / divisor);
	}

	/**
	 * Returns the quotient of the dividend and devisor.
	 * Months are replaced by the minimum of days of a month as minutes.
	 * @param dividend the dividend
	 * @param divisor the divisor
	 * @return the quotient
	 */
	public static double divide(Duration dividend, Duration divisor) {
		return (dividend.months * MAX_MINUTES_PER_MONTH + dividend.minutes) /
				(divisor.months * MIN_MINUTES_PER_MONTH + divisor.minutes);
	}

	/**
	 * Returns the amount of months between two
	 * {@link java.util.GregorianCalendar GregorianCalendar} instances.
	 * @param from first date
	 * @param to second date
	 * @return months
	 */
	private int monthsBetween(GregorianCalendar from, GregorianCalendar to) {
		int months = 0;
		GregorianCalendar tmp = (GregorianCalendar)from.clone();
		tmp.add(Calendar.MONTH, 1);
		while (tmp.before(to)) {
			months++;
			tmp.add(Calendar.MONTH, 1);
		}
		return months;
	}

	@Override
	public String toString() {
		return String.format("%2d:%2d %2d.%2d",
				this.getHours(), this.getMinutes(), this.getDays(), this.getMonths());
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(this.months) + Long.hashCode(this.minutes);
	}

	@Override
	public boolean equals(Object other) {
		return (other == null || !(other instanceof Duration))
				? false
				: this.months == ((Duration)other).months
						&& this.minutes == ((Duration)other).minutes;
	}

	@Override
	public int compareTo(Duration other) {
		return Long.compare(
				this.months * MIN_MINUTES_PER_MONTH + this.minutes,
				other.months * MIN_MINUTES_PER_MONTH + other.minutes);
	}
}
