package settings;

import static javafx.scene.text.FontWeight.BOLD;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import logging.LoggingController;
import menus.OptionsDialog;
import util.Translator;

/**
 * Static Class that holds Information for all SettingController.
 * @author Mario Schäper
 */
public abstract class SettingController {
	private static final String PATH = "config/cnfg";
	private static final File FILE = new File(PATH);
	private static SettingsContainer SETTINGS;

	static {
		try {
			SETTINGS = new SettingsContainer();
		} catch (Exception e) {
			LoggingController.log(Level.SEVERE,
					"Unable to initialize SettingsContainer: " + e.getMessage());
			OptionsDialog.showMessage(
					Translator.translate("dialogs", "unexpectedError", "title"),
					Translator.translate("dialogs", "unexpectedError", "message"));
			System.exit(0);
		}
		if (FILE.isDirectory()) {
			LoggingController.log(Level.SEVERE, "Config-File is a Directory.");
			ButtonType deleteButton = new ButtonType(Translator.translate(
					"settings", "dialogs", "fileIsDir", "buttons", "delete"),
					ButtonData.YES);
			ButtonType quitButton = new ButtonType(Translator.translate(
					"settings", "dialogs", "fileIsDir", "buttons", "quit"),
					ButtonData.CANCEL_CLOSE);
			if (!deleteButton.equals(OptionsDialog.getOption(
					Translator.translate("settings", "dialogs", "fileIsDir", "title"),
					Translator.translate("settings", "dialogs", "fileIsDir", "message"),
					deleteButton, quitButton))) {
				System.exit(0);
			}
			try {
				FILE.delete();
			} catch (Exception e) {
				LoggingController.log(Level.SEVERE, "Unable to delete the Folder blocking"
						+ " the Config-File from Creation: " + e.getMessage());
				OptionsDialog.showMessage(
						Translator.translate("settings", "dialogs", "dirNotDeletable", "title"),
						Translator.translate("settings", "dialogs", "dirNotDeletable", "message"));
				System.exit(0);
			}
		}
		if (FILE.exists()) {
			SettingController.load();
		} else {
			SettingController.save();
		}
	}

	/**
	 * Container Class to hold Data.
	 * @author Mario Schäper
	 */
	public final static class SettingsContainer implements Serializable {
		private static final long serialVersionUID = -2795405964359378914L;
		public final Setting<String> LANGUAGE = new LanguageSetting(Translator.DEFAULT_LANGUAGE);
		public final Setting<Color> DAYVIEW_TOPBAR_BACKGROUND_COLOR =
				new ColorSetting(Color.GAINSBORO);
		public final Setting<Color> DAYVIEW_TOPBAR_STROKE_COLOR =
				new ColorSetting(Color.SLATEGREY);
		public final Setting<Color> DAYVIEW_BACKGROUND_LEFT_COLOR =
				new ColorSetting(Color.BURLYWOOD);
		public final Setting<Color> DAYVIEW_BACKGROUND_RIGHT_COLOR =
				new ColorSetting(Color.AQUAMARINE);
		public final Setting<Font>  DAYVIEW_TIMESTAMP_FONT =
				new FontSetting(Font.font("Courier New"));
		public final Setting<Color> DAYVIEW_APPOINTMENT_BACKGROUND_COLOR =
				new ColorSetting(Color.KHAKI);
		public final Setting<Color> DAYVIEW_APPOINTMENT_STROKE_COLOR =
				new ColorSetting(Color.DARKKHAKI);
		public final Setting<Font>  DAYVIEW_APPOINTMENT_SUBJECT_FONT =
				new FontSetting(Font.font("Verdana", BOLD, 14));
		public final Setting<Font>  DAYVIEW_APPOINTMENT_DESCRIPTION_FONT =
				new FontSetting(Font.font("Verdana", 12));

		public SettingsContainer() throws IllegalArgumentException {}

		public void applyValues(SettingsContainer sc) {
			Field[] fields = SettingsContainer.class.getDeclaredFields();
			for (int i=fields.length-1;i>=0;i--) {
				try {
					if (Modifier.isPublic(fields[i].getModifiers())) {
						Object fieldValue = fields[i].get(this);
						Object scValue = fields[i].get(sc);
						if (fieldValue != null && scValue != null &&
								fieldValue instanceof Setting<?> && scValue instanceof Setting<?>) {
							((Setting<?>)fieldValue).setObject(((Setting<?>)scValue).getValue());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Restores the default settings for all propertys.
	 */
	public static void setDefaults() {
		Field[] fields = SettingsContainer.class.getDeclaredFields();
		for (int i=fields.length-1;i>=0;i--) {
			try {
				if (Modifier.isPublic(fields[i].getModifiers())) {
					Object fieldValue = fields[i].get(SETTINGS);
					if (fieldValue instanceof Setting<?>) {
						try {
							((Setting<?>)fieldValue).restoreDefault();
						} catch (NullPointerException e) {
							throw new Exception("Setting has no default Value");
						}
					}
				}
			} catch (Exception e) {
				LoggingController.log(Level.WARNING, "Unable to restore Default for Setting " +
						fields[i].getName() + ": " + e.getMessage());
			}
		}
	}

	public static void load() {
		boolean deleteFile = false;
		FileInputStream in = null;
		ObjectInputStream ois = null;
		try {
			in = new FileInputStream(FILE);
			ois = new ObjectInputStream(in);
			SettingsContainer sc = (SettingsContainer)ois.readObject();
			SETTINGS.applyValues(sc);
			LoggingController.log(Level.FINE, "Config loaded");
		} catch (ClassNotFoundException | IOException e) {
			LoggingController.log(Level.WARNING, "Unable to load Config: " + e.getMessage());
			deleteFile = !(e instanceof FileNotFoundException);
		} finally {
			try {
				ois.close();
				in.close();
			} catch (NullPointerException | IOException e) {} finally {
				if (deleteFile) {
					try {
						FILE.delete();
					} catch (Exception e) {
						LoggingController.log(Level.WARNING, "Unable to delete corrupted config");
					}
				}
			}
		}
	}

	public static void save() {
		FileOutputStream out = null;
		ObjectOutputStream oos = null;
		try {
			out = new FileOutputStream(FILE);
			oos = new ObjectOutputStream(out);
			oos.writeObject(SETTINGS);
			LoggingController.log(Level.INFO, "Config saved.");
		} catch (Exception e) {
			LoggingController.log(Level.WARNING, "Unable to save Config: " + e.getMessage());
		} finally {
			try {
				oos.close();
				out.close();
			} catch (NullPointerException | IOException e) {}
		}
	}

	public static SettingsContainer get() {
		return SETTINGS;
	}
}
