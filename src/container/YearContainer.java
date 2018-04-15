package container;

/**
 * A {@link SubContainer SubContainer} holding
 * {@link MonthContainer MonthContainer}-instances.
 * This class is used purely for structural purposes.
 * @author Mario Schäper
 */
public class YearContainer extends SubContainer<MonthContainer> {
	public YearContainer(short value) {
		super(value);
	}
}
