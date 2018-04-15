package container;

import containerItem.Appointment;

/**
 * A {@link SubContainer SubContainer} holding
 * {@link Appointment Appointment}-instances.
 * This class is used purely for structural purposes.
 * @author Mario Schäper
 */
public class DayContainer extends SubContainer<Appointment> {
	public DayContainer(short value) {
		super(value);
	}
}
