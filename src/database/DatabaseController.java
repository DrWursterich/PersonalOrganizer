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
import database.period.Period;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import database.appointment.Appointment;
import database.appointment.AppointmentGroup;
import database.appointment.AppointmentItem;
import database.category.Category;
import database.priority.Priority;
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
	private static final String INSERT_CATEGORY = "INSERT INTO "
			+ "CATEGORY (NAME, DESCRIPTION) VALUES (?, ?);";
	private static final String UPDATE_CATEGORY = "UPDATE CATEGORY "
			+ "SET NAME = ?, DESCRIPTION = ? WHERE ID = ?";
	private static final String INSERT_PRIORITY =
			"INSERT INTO PRIORITY (NAME) VALUES (?);";
	private static final String UPDATE_PRIORITY =
			"UPDATE PRIORITY SET NAME = ? WHERE ID = ?;";
	private static final String INSERT_PERIOD =
			"INSERT INTO PERIOD (MINUTES; DAYS; MONTHS) VALUES (?, ?, ?);";
	private static final String DELETE_PRIORITY =
			"DELETE FROM PRIORITY WHERE ID = ?;";
	private static final String DELETE_PRIORITY_PERIODS =
			"DELETE FROM PRIORITY_PERIOD WHERE PRIORITY_FK = ?;";
	private static final String DELETE_CATEGORY = "DELETE FROM CATEGORY WHERE ID = ?;";
	private static Connection connection;
	@SuppressWarnings("unused")
	private static ArrayList<ResultSet> results = new ArrayList<>();

	//TODO: Datenbankzugriffe sollten zusammengefasst werden, indem der eigentliche zugriff in
	//		einer eigenen Methode statt findet. eventuell ist der rollback ect. auch extrahierbar.

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
	 * Adds an {@link AppointmentGroup Appointment} to the Database.
	 * @param appointment the Appointment
	 */
	public static void addAppointment(final AppointmentGroup appointment) {
		PreparedStatement statement = null;
		Savepoint savepoint = null;
		final Priority priority = appointment.getPriority() != null
				? appointment.getPriority()
				: Priority.NONE;
		final Category category = appointment.getCategory() != null
				? appointment.getCategory()
				: Category.NONE;
		if (!priority.hasId())  {
			DatabaseController.addPriority(priority);
		}
		if (!category.hasId()) {
			DatabaseController.addCategory(category);
		}
		try {
			savepoint = connection.setSavepoint();
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(INSERT_APPOINTMENTGROUP);
			statement.setString(1, appointment.getSubject());
			statement.setString(2, appointment.getDescription());
			statement.setInt(3, priority.getId());
			statement.setInt(4, category.getId());
			statement.executeUpdate();
			statement.close();
			final int appointmentGroupId = DatabaseController.getMaxId("APPOINTMENT_GROUP");
			for (final AppointmentItem app : appointment.getAppointmentItems()) {
				statement = connection.prepareStatement(INSERT_APPOINTMENT);
				statement.setLong(1, app.getStartDate().getTimeInMillis());
				statement.setLong(2, app.getEndDate().getTimeInMillis());
				statement.setInt(3,
						DatabaseController.getCircleId(
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
		} catch (final SQLException e) {
			LoggingController.log(Level.WARNING,
					"Failed to add Appointment to Database: " + e.getMessage());
			if (savepoint != null) {
				try {
					connection.rollback(savepoint);
				} catch (final SQLException ex) {
					LoggingController.log(Level.SEVERE, "Rollback failed: " + ex.getMessage());
				}
			}
		} finally {
			try {
				connection.setAutoCommit(true);
				if (statement != null) {
					statement.close();
				}
			} catch (final SQLException e) {}
		}
	}

	public static ArrayList<AppointmentGroup> getPriorityAppointments(
			final Priority priority) {
		final ArrayList<AppointmentGroup> ret = new ArrayList<>();
		try {
			if (!priority.hasId()) {
				throw new SQLException(
						"Category does not exist in the Database");
			}
			final Statement statement = connection.createStatement();
			final ResultSet result = statement.executeQuery(
					"SELECT ID, NAME, DESCRIPTION, CATEGORY_FK "
					+ "FROM APPOINTMENT_GROUP "
					+ "WHERE PRIORITY_FK = " + priority.getId());
			while (result.next()) {
				ret.add(new AppointmentGroup(
						result.getInt("ID"),
						result.getString("NAME"),
				result.getString("DESCRIPTION"),
				DatabaseController.getCategoryById(
						result.getInt("CATEGORY_FK")),
				priority,
				null));
			}
		} catch (final SQLException e) {
			LoggingController.log(Level.WARNING,
					"Unable to get Appointments for Priority(id="
					+ priority.getId() + " name=\""
					+ priority.getName() + "\"):" + e.getMessage());
			return null;
		}
		return ret;
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
				if (!repetition.equals(Duration.NONE)) {
					int div = (int)Math.ceil(Duration.divide(
							new Duration(start, Duration.subtract(dateStart, appDuration)),
							repetition));
					while (!Duration.add(end, Duration.multiply(repetition, ++div)).after(dateStart)) {
					}
					GregorianCalendar current =
							Duration.add(start, Duration.multiply(repetition, --div));
					do {
						appointments.add(new Appointment(result.getString("NAME"),
								result.getString("DESCRIPTION"), current,
								Duration.add(current, appDuration)));
					} while ((current = Duration.add(start, Duration.multiply(repetition, ++div)))
							.before(dateEnd));
				} else {
					appointments.add(new Appointment(result.getString("NAME"),
							result.getString("DESCRIPTION"), start, end));
				}
			}
		} catch (SQLException e) {
			LoggingController.log(Level.WARNING, "Unable to get Appointments for " +
					(new SimpleDateFormat("YYYY-MM-dd")).format(date.getTime()) +
					": " + e.getMessage());
			return null;
		}
		return appointments;
	}

	public static ArrayList<AppointmentGroup> getCategoryAppointments(Category category) {
		ArrayList<AppointmentGroup> ret = new ArrayList<>();
		try {
			if (!category.hasId()) {
				throw new SQLException("Category does not exist in the Database");
			}
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT ID, NAME, DESCRIPTION " +
					"FROM APPOINTMENT_GROUP " +
					"WHERE CATEGORY_FK = " + category.getId());
			while (result.next()) {
				ret.add(new AppointmentGroup(result.getInt("ID"), result.getString("NAME"),
						result.getString("DESCRIPTION"), category, null, null));
			}
		} catch (SQLException e) {
			LoggingController.log(Level.WARNING, "Unable to get Appointments for Category(id=" +
					category.getId() + " name=\"" + category.getName() + "\"):" + e.getMessage());
			return null;
		}
		return ret;
	}

	public static Category getCategoryById(int id) {
		String name = "NONE";
		String description = "";
		try {
			ResultSet result = connection.createStatement().executeQuery(
					"SELECT NAME, DESCRIPTION FROM CATEGORY WHERE ID = " + id + ";");
			if (!result.isClosed()) {
				name = result.getString("NAME");
				description = result.getString("DESCRIPTION");
			} else {
				throw new SQLException("Category does not exist");
			}
		} catch (SQLException e) {
			LoggingController.log(Level.INFO,
					"Unable to return Category for id " + id + ": " + e.getMessage());
			id = DatabaseItem.UNASSIGNED_ID;
		}
		return Category.createInstance(id, name, description);
	}

	public static ArrayList<Category> getCategories() {
		ArrayList<Category> ret = new ArrayList<>();
		try {
			ResultSet result = connection.createStatement().executeQuery("SELECT * FROM CATEGORY;");
			if (!result.isClosed()) {
				while (result.next()) {
					ret.add(Category.createInstance(
							result.getInt("ID"),
							result.getString("NAME"),
							result.getString("DESCRIPTION")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static Priority getPriorityById(int id) {
		String name= "NONE";
		final List<Period> alarmList = new ArrayList<>();
		try {
			ResultSet result = connection.createStatement().executeQuery(
					"SELECT NAME FROM PRIORITY WHERE ID = " + id + ";");
			if (result != null && !result.isClosed()) {
				name = result.getString("NAME");
				result.close();
			} else {
				throw new SQLException("Priority does not exist");
			}
			result = connection.createStatement().executeQuery(
					"SELECT P.ID, P.MINUTES, P.DAYS, P.MONTHS "
					+ "FROM PRIORITY_PERIOD AS PP "
					+ "JOIN PERIOD AS P ON PP.PERIOD_FK = P.ID "
					+ "WHERE PP.PRIORITY_FK = " + id + ";");
			while (result != null && !result.isClosed() && result.next()) {
				alarmList.add(Period.createInstance(
						result.getInt("ID"),
						result.getInt("MINUTES"),
						result.getInt("DAYS"),
						result.getInt("MONTHS")));
			}
			result.close();
		} catch (final SQLException e) {
			LoggingController.log(Level.INFO,
						"Unable to return Priority for id " + id
						+ ": " + e.getMessage());
			id = DatabaseItem.UNASSIGNED_ID;
		}
		return Priority.createInstance(id, name, alarmList);
	}

	public static ArrayList<Priority> getPriorities() {
		final ArrayList<Priority> ret = new ArrayList<>();
		try {
			final ResultSet priorities = connection.createStatement().executeQuery(
					"SELECT * FROM PRIORITY;");
			while (!priorities.isClosed() && priorities.next()) {
				final int id = priorities.getInt("ID");
				final ArrayList<Period> alarmList = new ArrayList<>();
				final ResultSet alarms = connection.createStatement().executeQuery(
						"SELECT P.ID, P.MINUTES, P.DAYS, P.MONTHS "
						+ "FROM PRIORITY_PERIOD AS PP "
						+ "JOIN PERIOD AS P ON PP.PERIOD_FK = P.ID "
						+ "WHERE PP.PRIORITY_FK = " + id + ";");
				while (alarms != null && !alarms.isClosed() && alarms.next()) {
					alarmList.add(Period.createInstance(
							alarms.getInt("ID"),
							alarms.getInt("MINUTES"),
							alarms.getInt("DAYS"),
							alarms.getInt("MONTHS")));
				}
				ret.add(Priority.createInstance(
						id,
						priorities.getString("NAME"),
						alarmList));
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Adds a {@link Priority Priority} to the Database.
	 * @param priority the Priority
	 */
	public static void addPriority(final Priority priority) {
		PreparedStatement statement = null;
		Savepoint savepoint = null;
		boolean success = true;
		try {
			savepoint = connection.setSavepoint();
			connection.setAutoCommit(false);
			if (priority.hasId()) {
				statement = connection.prepareStatement(UPDATE_PRIORITY);
				statement.setInt(2, priority.getId());
			} else {
				statement = connection.prepareStatement(INSERT_PRIORITY);
			}
			statement.setString(1, priority.getName());
			statement.executeUpdate();
			connection.commit();
			if (!priority.hasId()) {
				priority.initializeId(DatabaseController.getMaxId("PRIORITY"));
			}
			LoggingController.log(Level.FINE, "Added Priority to Database.");
		} catch (final SQLException e) {
			LoggingController.log(Level.WARNING,
					"Failed to add Priority to Database: " + e.getMessage());
			success = false;
			e.printStackTrace(System.err);
			if (savepoint != null) {
				try {
					connection.rollback(savepoint);
				} catch (final SQLException ex) {
					LoggingController.log(Level.SEVERE,
							"Rollback failed: " + ex.getMessage());
				}
			}
		} finally {
			try {
				connection.setAutoCommit(true);
				if (statement != null) {
					statement.close();
				}
			} catch (final SQLException e) {
				System.out.println("recovering failed");
				e.printStackTrace(System.err);
			}
		}
		if (success) {
			priority.getAlarmList().stream().forEach(DatabaseController::addPeriod);
			try {
				savepoint = connection.setSavepoint();
				connection.setAutoCommit(false);
				final int deleted = connection.createStatement().executeUpdate(
						"DELETE FROM PRIORITY_PERIOD WHERE PRIORITY_FK = "
							+ priority.getId()+ ";");
				LoggingController.log(Level.FINE,
						"Removed " + deleted
							+ " Priority-Period-Links of \""
							+ priority.getName() + "\".");
				final Optional<String> values = priority.getAlarmList().stream()
						.map(e -> "(" + e.getId() + ", " + e.getId() + ")") // FIXME: Vermutlich so nicht richtig!
						.reduce((r, e) -> r += ", " + e);
				if (values.isPresent()) {
					statement = connection.prepareStatement(
							"INSERT INTO PRIORITY_PERIOD "
								+ "(PRIORITY_FK, PERIOD_FK) VALUES "
								+ values.get() + ";");
					statement.executeUpdate();
					LoggingController.log(Level.FINE,
							"Added Priority-Period-Links to Database.");
				}
				connection.commit();
			} catch (final SQLException e) {
				LoggingController.log(Level.WARNING,
						"Failed to add Priority-Period-Links to Database: "
							+ e.getMessage());
				e.printStackTrace(System.err);
				if (savepoint != null) {
					try {
						connection.rollback(savepoint);
					} catch (final SQLException ex) {
						LoggingController.log(Level.SEVERE,
								"Rollback failed: " + ex.getMessage());
					}
				}
			} finally {
				try {
					connection.setAutoCommit(true);
					if (statement != null) {
						statement.close();
					}
				} catch (final SQLException e) {
					System.out.println("recovering failed");
					e.printStackTrace(System.err);
				}
			}
		}
	}

	/**
	 * Adds a {@link Period Period} to the Database.<br/>
	 * If the Period is already present in the Database,
	 * this method does nothing.
	 * @param period the Period
	 */
	public static void addPeriod(final Period period) {
		if (period.hasId()) {
			return;
		}
		PreparedStatement statement = null;
		Savepoint savepoint = null;
		try {
			savepoint = connection.setSavepoint();
			connection.setAutoCommit(false);
			final ResultSet periods = connection.createStatement().executeQuery(
					"SELECT * FROM PERIOD WHERE MONTHS = " + period.getMonths()
						+ " AND DAYS = " + period.getDays()
						+ " AND MINUTES = " + period.getMinutes());
			final List<Integer> duplicatePeriods = new ArrayList<>();
			while (!periods.isClosed() && periods.next()) {
				final int id = periods.getInt("ID");
				if (periods.isFirst()) {
					LoggingController.log(Level.FINE,
							"Period already exists in the Database.");
					period.initializeId(id);
				}
				duplicatePeriods.add(id);
			}
			if (duplicatePeriods.size() > 1) {
				LoggingController.log(Level.WARNING,
						"The Periods with the IDs "
							+ String.join(
								", ",
								duplicatePeriods.toArray(new String[duplicatePeriods.size()])
							+ " are identical."));
			}
			if (duplicatePeriods.size() == 0) {
				statement = connection.prepareStatement(INSERT_PERIOD);
				statement.setInt(1, period.getMinutes());
				statement.setInt(2, period.getDays());
				statement.setInt(3, period.getMonths());
				statement.executeUpdate();
				statement.close();
				connection.commit();
				period.initializeId(DatabaseController.getMaxId("PERIOD"));
				LoggingController.log(Level.FINE, "Added Period to Database.");
			}
		} catch (final SQLException e) {
			LoggingController.log(Level.WARNING,
					"Failed to add Period to Database: " + e.getMessage());
			if (savepoint != null) {
				try {
					connection.rollback(savepoint);
				} catch (final SQLException ex) {
					LoggingController.log(Level.SEVERE,
							"Rollback failed: " + ex.getMessage());
				}
			}
		} finally {
			try {
				connection.setAutoCommit(true);
				if (statement != null) {
					statement.close();
				}
			} catch (final SQLException e) {
				System.out.println("recovering failed");
				e.printStackTrace(System.err);
			}
		}
	}

	public static void addPriorities(final List<Priority> priorities) {
		priorities.stream().forEach(e -> DatabaseController.addPriority(e));
	}

	public static void removePriority(final Priority priority) {
		PreparedStatement statement = null;
		Savepoint savepoint = null;
		try {
			if (!priority.hasId()) {
					throw new SQLException("Priority does not exist in the Database");
			}
			savepoint = connection.setSavepoint();
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(DELETE_PRIORITY_PERIODS);
			statement.setInt(1, priority.getId());
			statement.executeUpdate();
			statement.close();
			statement = connection.prepareStatement(DELETE_PRIORITY);
			statement.setInt(1, priority.getId());
			final int amountRemoved = statement.executeUpdate();
			statement.close();
			connection.commit();
			if (amountRemoved == 1) {
				LoggingController.log(Level.FINE,
						"Removed 1 Priority from Database.");
			} else {
				LoggingController.log(Level.WARNING,
						"Removed " + amountRemoved + " Priorities from Database.");
			}
		} catch (final SQLException e) {
			LoggingController.log(Level.WARNING,
					"Failed to remove Priority from Database: " + e.getMessage());
			if (savepoint != null) {
				try {
					connection.rollback(savepoint);
				} catch (final SQLException ex) {
					LoggingController.log(Level.SEVERE,
							"Rollback failed: " + ex.getMessage());
				}
			}
		} finally {
			try {
				connection.setAutoCommit(true);
				if (statement != null) {
					statement.close();
				}
			} catch (final SQLException e) {
				System.out.println("recovering failed");
				e.printStackTrace(System.err);
			}
		}
	}

	/**
	 * Adds a {@link Category Category} to the Database.
	 * @param category the Category
	 */
	public static void addCategory(Category category) {
		PreparedStatement statement = null;
		Savepoint savepoint = null;
		try {
			savepoint = connection.setSavepoint();
			connection.setAutoCommit(false);
			if (category.hasId()) {
				statement = connection.prepareStatement(UPDATE_CATEGORY);
				statement.setInt(3, category.getId());
			} else {
				statement = connection.prepareStatement(INSERT_CATEGORY);
			}
			statement.setString(1, category.getName());
			statement.setString(2,category.getDescription());
			statement.executeUpdate();
			statement.close();
			connection.commit();
			category.initializeId(DatabaseController.getMaxId("CATEGORY"));
			LoggingController.log(Level.FINE, "Added Category to Database.");
		} catch (SQLException e) {
			LoggingController.log(Level.WARNING,
					"Failed to add Category to Database: " + e.getMessage());
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

	public static void addCategories(List<Category> categories) {
		for (Category category : categories) {
			DatabaseController.addCategory(category);
		}
	}

	public static void removeCategory(Category category) {
		PreparedStatement statement = null;
		Savepoint savepoint = null;
		try {
			if (!category.hasId()) {
				throw new SQLException("Category does not exist in the Database");
			}
			savepoint = connection.setSavepoint();
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(DELETE_CATEGORY);
			statement.setInt(1, category.getId());
			int amountRemoved = statement.executeUpdate();
			statement.close();
			connection.commit();
			if (amountRemoved == 0) {
				throw new SQLException("Category does not exist in the Database");
			}
			LoggingController.log(Level.FINE, "Removed Category from Database.");
		} catch (SQLException e) {
			LoggingController.log(Level.WARNING,
					"Failed to remove Category from Database: " + e.getMessage());
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
