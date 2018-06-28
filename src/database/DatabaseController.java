package database;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import appointments.Category;
import appointments.Priority;
import containerItem.Appointment;
import util.Duration;

/**
 * This class controlls the access to a database containing appointments.
 * @author Mario Schäper
 */
public abstract class DatabaseController {
	private static final String INSERT_APPOINTMENTGROUP = "INSERT INTO APPOINTMENT_GROUP "
			+ "(NAME, DESCRIPTION, PRIORITY_FK, CATEGORY_FK) VALUES (?, ?, ?, ?);";
	private static final String INSERT_APPOINTMENT = "INSERT INTO APPOINTMENT "
			+ "(START_DATE, END_DATE, CIRCLE_FK) VALUES (?, ?, ?);";
	private static final String INSERT_APPOINTMENT_APPOINTMENT_GROUP = "INSERT INTO "
			+ "APPOINTMENT_APPOINTMENT_GROUP (APPOINTMENT_FK, APPOINTMENT_GROUP_FK) VALUES (?, ?);";
	private static ArrayList<ResultSet> results = new ArrayList<>();
	private static Connection connection;

	static {
		try {
			DatabaseController.connection =
					DriverManager.getConnection("jdbc:sqlite:appointments.db");
			DatabaseController.runScript("CREATE_TABLE");
			DatabaseController.runScript("INSERT_INTO");
			DatabaseController.runScript("CREATE_TRIGGER");
			DatabaseController.runScript("CREATE_VIEW");
		} catch (SQLException e) {
			System.out.println("Connection to database could not be established");
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * A class to hold data.
	 * Serves the purposes of readability and structure.
	 * @author Mario Schäper
	 */
	public static class AppointmentContainer {
		private String subject;
		private String description;
		private Category category;
		private Priority priority;
		private ArrayList<AppointmentItem> appointmentItems;

		public AppointmentContainer(String subject, String description, Category category,
				Priority priority, ArrayList<AppointmentItem> appointmentItems) {
			this.subject = subject;
			this.description = description;
			this.category = category;
			this.priority = priority;
			this.appointmentItems = appointmentItems;
		}

		public String getSubject() {
			return subject;
		}

		public String getDescription() {
			return description;
		}

		public Category getCategory() {
			return category;
		}

		public Priority getPriority() {
			return priority;
		}

		public ArrayList<AppointmentItem> getAppointmentItems() {
			return appointmentItems;
		}
	}

	/**
	 * A class to hold data.
	 * Serves the purposes of readability and structure.
	 * @author Mario Schäper
	 */
	public static class AppointmentItem {
		public static final Duration NO_REPETITION = new Duration(0, 0, 0, 0);
		private GregorianCalendar startDate;
		private GregorianCalendar endDate;
		private Duration repetition;
		private GregorianCalendar repetitionEnd;

		public AppointmentItem(GregorianCalendar startDate, GregorianCalendar endDate,
				Duration repetition, GregorianCalendar repetitionEnd) {
			this.startDate = startDate;
			this.endDate = endDate;
			this.repetition = repetition;
			this.repetitionEnd = repetitionEnd;
		}

		public AppointmentItem(GregorianCalendar startDate, GregorianCalendar endDate) {
			this(startDate, endDate, AppointmentItem.NO_REPETITION, null);
		}

		public GregorianCalendar getStartDate() {
			return startDate;
		}

		public GregorianCalendar getEndDate() {
			return endDate;
		}

		public Duration getRepetition() {
			return repetition;
		}

		public GregorianCalendar getRepetitionEnd() {
			return repetitionEnd;
		}
	}

	/**
	 * Adds an {@link Appointment Appointments} to the Database.
	 * @param subject the subject
	 * @param description a description
	 * @param startDate the date, at which the appointment beginns
	 * @param endDate the date, at which the appointment ends
	 */
	public static void addAppointment(AppointmentContainer appointment) {
		PreparedStatement statement = null;
		Savepoint savepoint = null;
		try {
			savepoint = DatabaseController.connection.setSavepoint();
			DatabaseController.connection.setAutoCommit(false);
			statement = DatabaseController.connection
					.prepareStatement(DatabaseController.INSERT_APPOINTMENTGROUP);
			statement.setString(1, appointment.getSubject());
			statement.setString(2,appointment.getDescription());
			statement.setInt(3, DatabaseController.getPriorityId(appointment.getPriority()));
			statement.setInt(4, DatabaseController.getCategoryId(appointment.getCategory()));
			statement.executeUpdate();
			statement.close();
			int appointmentGroupId = DatabaseController.getMaxId("APPOINTMENT_GROUP");
			for (AppointmentItem app : appointment.getAppointmentItems()) {
				statement = DatabaseController.connection
						.prepareStatement(DatabaseController.INSERT_APPOINTMENT);
				statement.setDate(1, new Date(app.getStartDate().getTimeInMillis()));
				statement.setDate(2, new Date(app.getEndDate().getTimeInMillis()));
				statement.setInt(3, DatabaseController.getDurationId(app.getRepetition()));
				statement.executeUpdate();
				statement.close();
				statement = DatabaseController.connection
						.prepareStatement(DatabaseController.INSERT_APPOINTMENT_APPOINTMENT_GROUP);
				statement.setInt(1, DatabaseController.getMaxId("APPOINTMENT"));
				statement.setInt(2, appointmentGroupId);
				statement.executeUpdate();
				statement.close();
			}
			DatabaseController.connection.commit();
		} catch (SQLException e) {
			System.out.println("unable to add appointment: " + e.getMessage());
			if (savepoint != null) {
				try {
					DatabaseController.connection.rollback(savepoint);
				} catch (SQLException ex) {}
			}
		} finally {
			try {
				DatabaseController.connection.setAutoCommit(true);
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {}
		}
	}

	/**
	 * Returns an {@link java.util.ArrayList ArrayList} of all
	 * {@link Appointment Appointments} for the given day.
	 * @param date specifies the day
	 * @return list of all appointments sorted by starttime
	 */
	public static ArrayList<Appointment> getDayAppointments(GregorianCalendar date) {
		ArrayList<Appointment> appointments = new ArrayList<>();
		String dateStr = "'" + new SimpleDateFormat("yyyy-mm-dd").format(date.getTime()) + "'";
		try {
			Statement statement = DatabaseController.connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM APPOINTMENTS_VIEW WHERE START_DATE <= DATE("
					+ dateStr + ") AND DATE(" + dateStr + ") < DATE(REPETITION_END, +END_DATE, -START_DATE);");
			while (result.next()) {
				GregorianCalendar start = new GregorianCalendar();
				GregorianCalendar end = new GregorianCalendar();
				start.setTime(result.getDate("START_DATE"));
				end.setTime(result.getDate("END_DATE"));
				appointments.add(new Appointment(result.getString("NAME"),
						result.getString("DESCRIPTION"), start, end));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
		return appointments;
	}

	/**
	 * Executes a script with the given name, that is located in the SQL-folder
	 * and has the extension ".sql". The name may contain subfolders but not its
	 * extionsion.
	 * @param script the name of the script to execute
	 */
	public static void runScript(String script) {
		ArrayList<ResultSet> currentResults = new ArrayList<>();
		Statement statement = null;
		Savepoint savepoint = null;
		try {
			savepoint = DatabaseController.connection.setSavepoint();
			DatabaseController.connection.setAutoCommit(false);
			File file = new File("SQL/" + script + ".sql");
			if (!file.exists()) {
				throw new IOException(file.getAbsolutePath() + " does not exist");
			}
			LineNumberReader reader = new LineNumberReader(Files.newBufferedReader(file.toPath()));
			StringBuffer command = new StringBuffer();
			String line = null;
			while((line = reader.readLine()) != null) {
				line = line.replace('\n', ' ').replace('\t', ' ').replace('\r', ' ').trim();
				if (!line.startsWith("--") && line.length() >= 1) {
					command.append(line);
					if (line.endsWith(";")) {
						statement = DatabaseController.connection.createStatement();
						statement.execute(command.toString());
						ResultSet resultSet = statement.getResultSet();
						if (resultSet != null) {
							currentResults.add(resultSet);
						}
						command = new StringBuffer();
					} else {
						command.append(' ');
					}
				}
			}
			DatabaseController.connection.commit();
			DatabaseController.results = currentResults;
		} catch (IOException | SQLException e) {
			System.out.println("unable to execute script " + script + ": " + e.getMessage());
			if (savepoint != null) {
				try {
					DatabaseController.connection.rollback(savepoint);
				} catch (SQLException ex) {}
			}
		} finally {
			try {
				DatabaseController.connection.setAutoCommit(true);
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {}
		}
	}

	/**
	 * Returns the ID of the given {@link Priority Priority} from the database.
	 * If there is none, it will be inserted first.
	 * @param priority the priority to get the ID from
	 * @return the ID of the priority
	 */
	private static int getPriorityId(Priority priority) {
		return 0;
	}

	/**
	 * Returns the ID of the given {@link Category Category} from the database.
	 * If there is none, it will be inserted first.
	 * @param category the category to get the ID from
	 * @return the ID of the category
	 */
	private static int getCategoryId(Category category) {
		return 0;
	}

	/**
	 * Returns the ID of the given {@link Duration Duration} from the database.
	 * If there is none, it will be inserted first.
	 * @param duration the duration to get the ID from
	 * @return the ID of the duration
	 */
	private static int getDurationId(Duration duration) {
		return 0;
	}

	/**
	 * Returns the highest ID in the given table of the database.
	 * @param table the table
	 * @return the highest ID in the given table
	 * @throws SQLException
	 */
	private static int getMaxId(String table) throws SQLException {
		ResultSet result = DatabaseController.connection.createStatement()
				.executeQuery("SELECT MAX(ID) AS MAX_ID FROM " + table + ";");
		return result.getInt("MAX_ID");
	}
}
