package util;

import java.util.Locale;
import java.util.logging.Level;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javafx.beans.property.StringProperty;
import logging.LoggingController;
import windows.OptionsDialog;
import javafx.beans.property.SimpleStringProperty;

/**
 * A class that translates keywords into a specific language using .json-files.<br/>
 * A language consists of a String of two chars, as an abbreviation
 * for the language it represents (schould be according to ISO 639). A .json-file with
 * the same name has to be located in the ../config/language folder.
 * @author Mario Schäper
 */
public abstract class Translator {
	private static StringProperty currentLanguage = new SimpleStringProperty();
	private static JSONObject json;
	private static final HashMap<String, StringProperty> TRANSLATION_PROPERTIES = new HashMap<>();
	/**
	 * The default Language as determined by
	 * {@link java.util.Locale#getDefault() Locale.getDefault()}.
	 */
	public static final String DEFAULT_LANGUAGE;

	static {
		String systemLocale = Locale.getDefault().getLanguage();
		try {
			Translator.setLanguage(systemLocale);
		} catch (Exception e) {
			LoggingController.log(Level.INFO, "Unable to load default (" +
					systemLocale + ") Language Settings: " + e.getMessage());
			systemLocale = "en";
			try {
				Translator.setLanguage(systemLocale);
			} catch (Exception ex) {
				LoggingController.log(Level.SEVERE,
						"Unable to load English Language Settings: " + ex.getMessage());
				OptionsDialog.showMessage("Error",
						"Unable to load neither default nor english Language Settings");
				System.exit(0);
			}
		}
		DEFAULT_LANGUAGE = systemLocale;
		LoggingController.log(Level.FINE, "Language Settings loaded for Language: " + systemLocale);
	}

	/**
	 * Returns the language.<br/>
	 * A language consists of a String of two chars, as an abbreviation
	 * for the language it represents (schould be according to ISO 639). A .json-file with
	 * the same name has to be located in the ../config/language folder.
	 * @return the current language
	 */
	public static String getLanguage() {
		return currentLanguage.getValue();
	}

	/**
	 * Sets the language.<br/>
	 * A language consists of a String of two chars, as an abbreviation
	 * for the language it represents (schould be according to ISO 639). A .json-file with
	 * the same name has to be located in the ../config/language folder.
	 * @param language the new language
	 * @throws IOException if the .json-file could not be loaded
	 */
	public static void setLanguage(String language) throws Exception {
		if (language == null) {
			throw new IllegalArgumentException("Language cannot be Null");
		}
		if (language.equals(currentLanguage.getValue())) {
			return;
		}
		File file = new File("config/language/" + language + ".json");
		if (file.exists()) {
			try {
				json = new JSONObject(new String(Files.readAllBytes(file.toPath())));
			} catch (JSONException e) {
				e.printStackTrace();
				throw new IOException("Language Configuration for \"" +
						language + "\" could not be loaded");
			}
		} else {
			throw new IOException("Language Configuration for \"" + language + "\" does not exist");
		}
		currentLanguage.setValue(language);
	}

	/**
	 * Translates a keyword using a .json-config for the specified language.<br/>
	 * If the keyword does not exist in the file the keyword itself is returned.<br/>
	 * <b>Example:</b> Translating the keyword "title" of the .json<pre>
	 * {
	 *    "window": {
	 *        "name": "Window",
	 *        "title": "Personal Organizer"
	 *    }
	 * }</pre>
	 * would have the the method call <pre>.translate("window.title");</pre><br/>
	 * To translate the "window" keyword use <pre>.translate("window.name");</pre>
	 * This also works for the following .json-structure:<pre>
	 * {
	 *    "window.title": "Personal Organizer"
	 * }</pre>
	 * @param key dot-separated keywords representing the .json-structure
	 * @return the translated String
	 * @throws UnsetLanguageException if the language of this class is not set
	 */
	public static String translate(String key) throws UnsetLanguageException {
		if (json == null) {
			throw new UnsetLanguageException(
					currentLanguage != null || currentLanguage.getValue() != null
							? (Translator.getLanguage() + ".json does not exist")
							: "No Laguage is defined",
					Translator.getLanguage());
		}
		if (key.startsWith(".") || key.endsWith(".")) {
			throw new IllegalArgumentException("Keys cannot start or end with a \".\"");
		}
		JSONObject newJson = json;
		String toSearch = key.contains(".") ? key.substring(key.indexOf('.') + 1) : "";
		String searchString = key.contains(".") ? key.substring(0, key.indexOf('.')) : key;
		while (!searchString.equals("")) {
			boolean found = false;
			while (!found) {
				if (newJson.has(searchString)) {
					try {
						Object temp = newJson.get(searchString);
						found = true;
						if (temp instanceof JSONArray) {
							String[] strNames = new String[((JSONArray)temp).length()];
							for (int j=strNames.length-1;j>=0;j--) {
								strNames[j] = ((JSONArray)temp).get(j).toString();
							}
							newJson = ((JSONArray)temp).toJSONObject(new JSONArray(strNames));
						}
						if (temp instanceof JSONObject) {
							newJson = (JSONObject)temp;
						}
						if (temp instanceof String) {
							if (toSearch.equals("")) {
								return (String)temp;
							} else {
								LoggingController.log(Level.WARNING, "Key \"" + key +
										"\" for Language \"" + Translator.getLanguage() +
										"\" contains a unexpected String for Subkey \"" +
										searchString + "\"");
								return key;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (!toSearch.equals("")) {
					searchString += "." + (toSearch.contains(".") ?
							toSearch.substring(0, toSearch.indexOf('.')) : toSearch);
					toSearch = toSearch.contains(".") ?
							toSearch.substring(toSearch.indexOf('.') + 1) : "";
				} else {
					break;
				}
			}
			searchString = toSearch.contains(".") ?
					toSearch.substring(0, toSearch.indexOf('.')) : toSearch;
			toSearch = toSearch.contains(".") ?
					toSearch.substring(toSearch.indexOf('.') + 1) : "";
		}
		LoggingController.log(Level.WARNING,
				"Key \"" + key + "\" not found for Language " + Translator.getLanguage());
		return key;
	}

	/**
	 * Returns a {@link javafx.beans.property.SimpleStringProperty SimpleStringProperty},
	 * which is bound to the translation of {@link #translate(String) translate}
	 * invoked with the given keys.
	 * @param key dot-separated keywords representing the .json-structure
	 * @return the translated property
	 * @throws UnsetLanguageException if the language of this class is not set
	 * @see Translator#translate(String)
	 */
	public static StringProperty translationProperty(String key)
			throws UnsetLanguageException {
		StringProperty ret = TRANSLATION_PROPERTIES.get(key);
		if (ret != null) {
			return ret;
		}
		SimpleStringProperty strP = new SimpleStringProperty(Translator.translate(key));
		currentLanguage.addListener((v, o, n) -> {
			strP.setValue(Translator.translate(key));
		});
		TRANSLATION_PROPERTIES.put(key, strP);
		return strP;
	}
}
