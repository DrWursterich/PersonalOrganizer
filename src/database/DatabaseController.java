package database;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import appointments.Appointment;
import appointments.Category;
import appointments.Priority;
import logging.LoggingController;
import util.Duration;

import static java.util.GregorianCalendar.DAY_OF_MONTH;
import static java.util.GregorianCalendar.MONTH;
import static java.util.GregorianCalendar.YEAR;

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
	private static Connection connection;
	@SuppressWarnings("unused")
	private static ArrayList<ResultSet> results = new ArrayList<>();

	static {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:appointments.db");
			LoggingController.log(Level.FINE, "Database connection established.");
		} catch (SQLException e) {
			LoggingController.log(Level.SEVERE,
					"Connection to database could not be established: " + e.getMessage());
			System.exit(0);
		}
		DatabaseController.runScript("CREATE_TABLE");
		DatabaseController.runScript("INSERT_INTO");
		DatabaseController.runScript("CREATE_TRIGGER");
		DatabaseController.runScript("CREATE_VIEW");
		LoggingController.log(Level.FINE, "Database initialized.");
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
			return this.startDate;
		}

		public GregorianCalendar getEndDate() {
			return this.endDate;
		}

		public Duration getRepetition() {
			return this.repetition;
		}

		public GregorianCalendar getRepetitionEnd() {
			return this.repetitionEnd;
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
			savepoint = connection.setSavepoint();
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(INSERT_APPOINTMENTGROUP);
			statement.setString(1, appointment.getSubject());
			statement.setString(2,appointment.getDescription());
			statement.setInt(3, DatabaseController.getPriorityId(appointment.getPriority()));
			statement.setInt(4, DatabaseController.getCategoryId(appointment.getCategory()));
			statement.executeUpdate();
			statement.close();
			int appointmentGroupId = DatabaseController.getMaxId("APPOINTMENT_GROUP");
			for (AppointmentItem app : appointment.getAppointmentItems()) {
				statement = connection.prepareStatement(INSERT_APPOINTMENT);
				statement.setLong(1, app.getStartDate().getTimeInMillis());
				statement.setLong(2, app.getEndDate().getTimeInMillis());
				statement.setInt(3, DatabaseController.getCircleId(
						DatabaseController.getDurationId(app.getRepetition()),
						app.getRepetitionEnd()));
				statement.executeUpdate();
				statement.close();
				statement = connection.prepareStatement(INSERT_APPOINTMENT_APPOINTMENT_GROUP);
				statement.setInt(1, DatabaseController.getMaxId("APPOINTMENT"));
				statement.setInt(2, appointmentGroupId);
				statement.executeUpdate();
				statement.close();
			}
			connection.commit();
			LoggingController.log(Level.FINE, "Added Appointment to Database.");
		} catch (SQLException e) {
			LoggingController.log(Level.WARNING,
					"Failed to add Appointment to Database: " + e.getMessage());
			if (savepoint != null) {
				try {
					connection.rollback(savepoint);
				} catch (SQLException ex) {
					LoggingController.log(Level.SEVERE, "Rollback failed: " + ex.getMessage());
				}
			}
		} finally {
			try {
				connection.setAutoCommit(true);
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
		GregorianCalendar dateStart = new GregorianCalendar(date.get(YEAR),
				date.get(MONTH), date.get(DAY_OF_MONTH));
		GregorianCalendar dateEnd = (GregorianCalendar)dateStart.clone();
		dateEnd.add(DAY_OF_MONTH, 1);
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM APPOINTMENTS_VIEW " +
					"WHERE START_DATE < " + dateEnd.getTimeInMillis() + " AND " +
					"(REPETITION_END + (END_DATE - START_DATE)) >= " + dateStart.getTimeInMillis());
			while (result.next()) {
				GregorianCalendar start = new GregorianCalendar();
				GregorianCalendar end = new GregorianCalendar();
				start.setTime(result.getDate("START_DATE"));
				end.setTime(result.getDate("END_DATE"));
				Duration appDuration = new Duration(start, end);
				Duration repetition = new Duration(result.getInt("REPETITION_MONTHS"),
						0, 0, result.getInt("REPETITION_MINUTES"));
				int div = (int)Math.ceil(Duration.divide(new Duration(start,
						Duration.subtract(dateStart, appDuration)), repetition));
				while (!Duration.add(end, Duration.multiply(repetition, ++div)).after(dateStart)) {
					;
				}
				GregorianCalendar current =
						Duration.add(start, Duration.multiply(repetition, --div));
				do {
					appointments.add(new Appointment(result.getString("NAME"),
							result.getString("DESCRIPTION"), current,
							Duration.add(current, appDuration)));
				} while ((current = Duration.add(start, Duration.multiply(repetition, ++div)))
						.before(dateEnd));
			}
		} catch (SQLException e) {
			LoggingController.log(Level.WARNING, "Unable to get Appointments for " +
					(new SimpleDateFormat("YYYY-MM-dd")).format(date.getTime()) +
					": " + e.getMessage());
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
			savepoint = connection.setSavepoint();
			connection.setAutoCommit(false);
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
						statement = connection.createStatement();
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
			connection.commit();
			results = currentResults;
			LoggingController.log(Level.FINE, "Executed Script " + script + ".");
		} catch (IOException | SQLException e) {
			LoggingController.log(Level.WARNING,
					"Executing Script " + script + " failed: " + e.getMessage());
			if (savepoint != null) {
				try {
					connection.rollback(savepoint);
				} catch (SQLException ex) {
					LoggingController.log(Level.SEVERE, "Rollback failed: " + e.getMessage());
				}
			}
		} finally {
			try {
				connection.setAutoCommit(true);
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
	 * @throws SQLException
	 */
	private static int getPriorityId(Priority priority) throws SQLException {
		//TODO
		return 0;
	}


	/**
	 * Returns the ID of the given {@link Category Category} from the database.
	 * If there is none, it will be inserted first.
	 * @param category the category to get the ID from
	 * @return the ID of the category
	 * @throws SQLException
	 */
	private static int getCategoryId(Category category) throws SQLException {
		//TODO
		return 0;
	}


	/**
	 * Returns the ID of the given {@link Duration Duration} from the database.
	 * If there is none, it will be inserted first.
	 * @param duration the duration to get the ID from
	 * @return the ID of the duration
	 * @throws SQLException
	 */
	private static int getDurationId(Duration duration) throws SQLException {
		int durationMinutes = (duration.getMinutes() + duration.getHours() * 60);
		int id = 0;
		ResultSet result = connection.createStatement().executeQuery(
				"SELECT ID FROM PERIOD " +
				"WHERE MINUTES = " + durationMinutes + " AND " +
				"DAYS = " + duration.getDays() + " AND " +
				"MONTHS = " + duration.getMonths() + ";");
		if (result.isClosed() || result.getInt("ID") == 0) {
			connection.createStatement().executeUpdate(
					"INSERT INTO PERIOD (MINUTES, DAYS, MONTHS) VALUES (" +
					durationMinutes + ", " +
					duration.getDays() + ", " +
					duration.getMonths() + ");");
			id = DatabaseController.getMaxId("PERIOD");
		} else {
			id = result.getInt("ID");
		}
		result.close();
		return id;
	}


	/**
	 * Returns the ID of the circle-entity with the given parameters from the database.
	 * If there is none, it will be inserted first.
	 * @param durationId the foreign key of a {@link Duration Duration}
	 * @param repetitionEnd the date at witch the circle terminates
	 * @return the ID of the circle-entity
	 * @throws SQLException
	 */
	private static int getCircleId(int durationId, GregorianCalendar repetitionEnd)
			throws SQLException {
		int id = 0;
		ResultSet result = connection.createStatement().executeQuery(
				"SELECT ID FROM CIRCLE " +
				"WHERE PERIOD_FK = " + durationId + " AND " +
				"END_DATE = " + repetitionEnd.getTimeInMillis());
		if (result.isClosed() || result.getInt("ID") == 0) {
			connection.createStatement().executeUpdate(
					"INSERT INTO CIRCLE (END_DATE, PERIOD_FK) VALUES (" +
					repetitionEnd.getTimeInMillis() + ", " +
					durationId + ");");
			id = DatabaseController.getMaxId("CIRCLE");
		} else {
			id = result.getInt("ID");
		}
		result.close();
		return id;
	}


	/**
	 * Returns the highest ID in the given table of the database.
	 * @param table the table
	 * @return the highest ID in the given table
	 * @throws SQLException
	 */
	private static int getMaxId(String table) throws SQLException {
		ResultSet result = connection.createStatement().executeQuery(
				"SELECT MAX(ID) AS MAX_ID " +
				"FROM " + table + ";");
		int id = 0;
		if (!result.isClosed()) {
			id = result.getInt("MAX_ID");
			result.close();
		}
		return id;
	}
}
