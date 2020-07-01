package logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Handler for Console Logs.
 * @author Mario Schäper
 * @see java.util.logging.ConsoleHandler
 */
public class ConsoleHandler extends java.util.logging.ConsoleHandler {
	private boolean firstLog = true;
	@Override
	public void publish(LogRecord record) {
		if (this.firstLog) {
			System.out.print(this.getFormatter().getHead(this));
			this.firstLog = false;
		}
		if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
			System.err.print(this.getFormatter().format(record));
		} else {
			System.out.print(this.getFormatter().format(record));
		}
	}

	@Override
	public void flush() {
		super.flush();
	}

	@Override
	public void close() throws SecurityException {
		if (!this.firstLog) {
			System.out.println(this.getFormatter().getTail(this));
		}
		this.flush();
		super.close();
	}
}
