package logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Formatter for Console Logs.
 * @author Mario Schäper
 * @see java.util.logging.Formatter
 */
public class ConsoleFormatter extends Formatter {
	private static final String SEPARATION =
			"-------------------------------------------------------------------------\n";
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	@Override
	public String format(LogRecord record) {
		return String.format("%s [%s] %s\n",
				new SimpleDateFormat("HH:mm:ss:SSS").format(new Date(record.getMillis())),
				record.getLevel().getName(),
				record.getMessage());
	}

	@Override
	public String getHead(Handler handler) {
		return "Personal Organizer™ Console-Log\n" + new Date() + "\n" + SEPARATION;
	}

	@Override
	public String getTail(Handler handler) {
		return SEPARATION + "\n";
	}
}
