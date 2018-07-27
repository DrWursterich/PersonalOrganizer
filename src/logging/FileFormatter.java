package logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Formatter for File Logs.
 * @author Mario Schäper
 * @see java.util.logging.Formatter
 */
public class FileFormatter extends Formatter {
	private static final String SEPARATION =
			"-------------------------------------------------------------------------\n";

	@Override
	public String format(LogRecord record) {
		return String.format("%s [%s] %s\n",
				new SimpleDateFormat("HH:mm:ss:SSS").format(new Date(record.getMillis())),
				record.getLevel().getName(), record.getMessage());
	}

	@Override
	public String getHead(Handler handler) {
		return "Personal Organizer™ Log\n" + new Date() + "\n" + SEPARATION;
	}

	@Override
	public String getTail(Handler handler) {
		return SEPARATION + "\n";
	}
}
