package menus;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import org.schaeper.fxiterator.item.Item;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import util.NumberField;
import windows.NodeInitializer;

public class AlarmIteratorItem extends GridPane
			implements Item, NodeInitializer {
	private final IntegerProperty monthsProperty;
	private final IntegerProperty daysProperty;
	private final IntegerProperty hoursProperty;
	private final IntegerProperty minutesProperty;
	private final Label monthsLabel;
	private final Label daysLabel;
	private final Label hoursLabel;
	private final Label minutesLabel;
	private final NumberField monthsField;
	private final NumberField daysField;
	private final NumberField hoursField;
	private final NumberField minutesField;

	private static final Format intFormat = new Format() {
		private static final long serialVersionUID = 46471849318247424L;

		@Override
		public StringBuffer format(
				final Object obj,
				final StringBuffer buffer,
				final FieldPosition position) {
			return buffer.append(obj.toString());
		}

		@Override
		public Object parseObject(
				final String string,
				final ParsePosition position) {
			int ret;
			try {
				ret = Integer.parseInt(string);
			} catch (final NumberFormatException e) {
				ret = 0;
			}
			position.setIndex(ret);
			return ret;
		}
	};

	public AlarmIteratorItem() {
		this.monthsProperty = new SimpleIntegerProperty();
		this.daysProperty = new SimpleIntegerProperty();
		this.hoursProperty = new SimpleIntegerProperty();
		this.minutesProperty = new SimpleIntegerProperty();
		this.monthsLabel = this.labelTranslatable(
				"alarmIterator.alarm.months.label");
		this.daysLabel = this.labelTranslatable(
				"alarmIterator.alarm.days.label");
		this.hoursLabel = this.labelTranslatable(
				"alarmIterator.alarm.hours.label");
		this.minutesLabel = this.labelTranslatable(
				"alarmIterator.alarm.minutes.label");
		this.monthsField = this.numberFieldTranslatable(
				"alarmIterator.alarm.months.prompt",
				0,
				Integer.MAX_VALUE);
		this.daysField = this.numberFieldTranslatable(
				"alarmIterator.alarm.days.prompt",
				0,
				30);
		this.hoursField = this.numberFieldTranslatable(
				"alarmIterator.alarm.hours.prompt",
				0,
				23);
		this.minutesField = this.numberFieldTranslatable(
				"alarmIterator.alarm.minutes.prompt",
				0,
				59);
		this.monthsField.textProperty().bindBidirectional(
				this.monthsProperty,
				intFormat);
		this.daysField.textProperty().bindBidirectional(
				this.daysProperty,
				intFormat);
		this.hoursField.textProperty().bindBidirectional(
				this.hoursProperty,
				intFormat);
		this.minutesField.textProperty().bindBidirectional(
				this.minutesProperty,
				intFormat);
		this.setPadding(new Insets(1, 10, 0, 10));
		this.setHgap(10);
		this.setAlignment(Pos.CENTER);
		this.add(this.monthsLabel, 0, 0);
		this.add(this.daysLabel, 1, 0);
		this.add(this.monthsField, 0, 1);
		this.add(this.daysField, 1, 1);
		this.add(this.hoursLabel, 0, 2);
		this.add(this.minutesLabel, 1, 2);
		this.add(this.hoursField, 0, 3);
		this.add(this.minutesField, 1, 3);
	}

	public int getMonths() {
		return this.monthsProperty.get();
	}

	public void setMonths(final int months) {
		this.monthsProperty.set(months);
	}

	public IntegerProperty monthsProperty() {
		return this.monthsProperty;
	}

	public int getDays() {
		return this.daysProperty.get();
	}

	public void setDays(final int days) {
		this.daysProperty.set(days);
	}

	public IntegerProperty daysProperty() {
		return this.daysProperty;
	}

	public int getHours() {
		return this.hoursProperty.get();
	}

	public void setHours(final int hours) {
		this.hoursProperty.set(hours);
	}

	public IntegerProperty hoursProperty() {
		return this.hoursProperty;
	}

	public int getMinutes() {
		return this.minutesProperty.get();
	}

	public void setMinutes(final int minutes) {
		this.minutesProperty.set(minutes);
	}

	public IntegerProperty minutesProperty() {
		return this.minutesProperty;
	}

	@Override
	public void initNumberField(final NumberField numberField) {
		numberField.setDefaultValue("0");
	}
}

