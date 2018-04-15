package container;

/**
 * A {@link SubContainer SubContainer} holding
 * {@link DayContainer DayContainer}-instances.
 * This class is used purely for structural purposes.
 * @author Mario Schäper
 */
public class MonthContainer extends SubContainer<DayContainer> {
	public MonthContainer(short value) {
		super(value);
	}
}
