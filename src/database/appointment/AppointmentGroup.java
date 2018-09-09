package database;

import java.util.ArrayList;

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
		this.category = category;
		this.priority = priority;
		this.appointmentItems = appointmentItems;
	}

	public String getSubject() {
		return this.subject;
	}

	public String getDescription() {
		return this.description;
	}

	public Category getCategory() {
		return this.category != null ? this.category : Category.NONE;
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
