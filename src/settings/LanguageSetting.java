package settings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import logging.LoggingController;
import util.Translator;

/**
 * Setting for a Language for the {@link util.Translator Translator}.
 * @see util.Translator#setLanguage(String)
 * @author Mario Schäper
 */
public class LanguageSetting extends Setting<String> {
	private static final long serialVersionUID = 5927316865135806684L;

	public LanguageSetting(String defaultValue) {
		super(defaultValue);
		this.addListener((v, o, n) -> {
			try {
				Translator.setLanguage(n);
				LoggingController.log(Level.INFO, "Language changed to " + n);
			} catch (Exception e) {
				this.setValue(o);
			}
		});
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeUTF(this.getValue());
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.setValue(in.readUTF());
	}
}
