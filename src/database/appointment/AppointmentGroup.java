package database.appointment;

import java.util.ArrayList;
import database.DatabaseItem;
import database.category.Category;
import database.priority.Priority;

/**
 * A class to hold data.
 * @author Mario Schäper
 */
public class AppointmentGroup extends DatabaseItem {
	private String subject;
	private String description;
	private Category category;
	private Priority priority;
	private ArrayList<AppointmentItem> appointmentItems;

	public AppointmentGroup(int id, String subject, String description,
			Category category, Priority priority, ArrayList<AppointmentItem> appointmentItems) {
		this(subject, description, category, priority, appointmentItems);
		this.initializeId(id);
	}

	public AppointmentGroup(String subject, String description, Category category,
			Priority priority, ArrayList<AppointmentItem> appointmentItems) {
		this.subject = subject;
		this.description = description;
		this.category = category != null ? category : Category.NONE;
		this.priority = priority != null ? priority : Priority.NONE;
		this.appointmentItems = appointmentItems;
	}

	public String getSubject() {
		return this.subject;
	}

	public String getDescription() {
		return this.description;
	}

	public Category getCategory() {
		return this.category;
	}

	public Priority getPriority() {
		return this.priority;
	}

	public ArrayList<AppointmentItem> getAppointmentItems() {
		return this.appointmentItems;
	}

	@Override
	public String toString() {
		return this.subject;
	}
}
