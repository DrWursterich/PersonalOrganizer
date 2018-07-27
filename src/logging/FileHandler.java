package logging;

import java.io.OutputStream;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * A Handler for File Logs.
 * @author Mario Schäper
 * @see java.util.logging.StreamHandler
 */
public class FileHandler extends StreamHandler {
	public FileHandler(OutputStream out) throws SecurityException {
		this.setOutputStream(out);
	}

	@Override
	public void publish(LogRecord record) {
		super.publish(record);
	}

	@Override
	public void flush() {
		super.flush();
	}

	@Override
	public void close() throws SecurityException {
		super.close();
	}

	@Override
	protected void finalize() throws Throwable {
		this.flush();
		super.finalize();
	}
}
