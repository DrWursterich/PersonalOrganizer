package logging;

import java.util.logging.LogRecord;

/**
 * Filter for Logs.<br/>
 * Filters out Logs, that are not called by the {@link LoggingController LoggingController}.
 * @author Mario Schäper
 * @see java.util.logging.Filter
 */
public class Filter implements java.util.logging.Filter {
	private boolean isDisabled = false;

	/**
	 * @return <b>true</b> if the Filter completely supressing all Logs, <b>false</b> if not.
	 */
	public boolean isDisabled() {
		return this.isDisabled;
	}

	/**
	 * If set to <b>true</b> the Filter supresses all Logs entirely.
	 * @param isDisabled new isDisabled value
	 */
	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	@Override
	public boolean isLoggable(LogRecord record) {
		return true;/*!this.isDisabled &&
				record.getSourceClassName().equals(LoggingController.class.getName());
				*/
	}
}
