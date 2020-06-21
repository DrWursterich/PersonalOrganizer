package database.priority;

import database.period.Period;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseController;
import database.DatabaseItem;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import util.Translator;

public class Priority extends DatabaseItem {

	public static final Priority NONE = DatabaseController.getPriorityById(1);

	private StringProperty nameProperty;
	private ObservableList<Period> alarmListProperty;

	static {
		Priority.NONE.nameProperty = Translator.translationProperty("ressources.priorities.none");
	}

	public static Priority createInstance(
			final String name,
			final List<Period> alarmList) {
		return new Priority(name, alarmList);
	}

	public static Priority createInstance(
			final int id,
			final String name,
			final List<Period> alarmList) {
		if (Priority.NONE != null && Priority.NONE.getId() == id) {
			return Priority.NONE;
		}
		final Priority priority = new Priority(name, alarmList);
		priority.initializeId(id);
		return priority;
	}

	public Priority(final String name, final List<Period> alarmList) {
		this.nameProperty = new SimpleStringProperty(name);
		this.alarmListProperty = new SortedList<>(new SimpleListProperty<>(
				FXCollections.observableArrayList(
						alarmList != null
							? alarmList
							: new ArrayList<>())));
	}

	public StringProperty nameProperty() {
		return this.nameProperty;
	}

	public String getName() {
		return this.nameProperty.get();
	}

	public void setName(final String name) {
		this.nameProperty.set(name);
	}

	public ObservableList<Period> alarmListProperty() {
		return this.alarmListProperty;
	}

	public List<Period> getAlarmList() {
		return this.alarmListProperty;
	}

	public void setAlarmList(final List<Period> alarmList) {
		this.alarmListProperty.setAll(alarmList);
	}

	@Override
	public String toString() {
		final StringBuilder alarms = new StringBuilder("[");
		this.alarmListProperty.stream().forEachOrdered(alarms::append);
		alarms.append("]");
		return String.join(
				"&",
				super.toString(),
				"name=" + this.getName(),
				"alarmList=" + alarms);
	}
}
