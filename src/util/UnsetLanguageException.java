package util;

/**
 * Exception signalizing, that the language used is invalid.<br/>
 * A language consists of a String of two chars, as an abbreviation
 * for the language it represents. A .json-file with the same name
 * has to be located in the ../config/language folder.
 * @author Mario Schäper
 */
public class UnsetLanguageException extends RuntimeException {
	private static final long serialVersionUID = 7696918332978896439L;
	private final String language;

	public UnsetLanguageException(String message, String language) {
		super(message);
		this.language = language;
	}
	
	public String getLanguage() {
		return this.language;
	}
}
