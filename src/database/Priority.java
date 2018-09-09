package database;

import java.time.Period;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

public class Priority extends DatabaseItem {
	private StringProperty name = new SimpleStringProperty();
	private ObservableList<Period> alarms = new SortedList<>(null);

	public Priority() {
	}
}
