package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * A class that translates keywords into a specific language using .json-files.<br/>
 * A language consists of a String of two chars, as an abbreviation
 * for the language it represents. A .json-file with the same name
 * has to be located in the ../config/language folder.
 * @author Mario Schäper
 */
public abstract class Translator {
	private static StringProperty currentLanguage = new SimpleStringProperty("en");
	private static JSONObject json;

	/**
	 * Returns the language.<br/>
	 * A language consists of a String of two chars, as an abbreviation
	 * for the language it represents. A .json-file with the same name
	 * has to be located in the ../config/language folder.
	 * @return
	 */
	public static String getLanguage() {
		return currentLanguage.getValue();
	}

	/**
	 * Sets the language.<br/>
	 * A language consists of a String of two chars, as an abbreviation
	 * for the language it represents. A .json-file with the same name
	 * has to be located in the ../config/language folder.
	 * @param language the new language
	 * @throws IOException if the .json-file could not be loaded
	 */
	public static void setLanguage(String language) throws IOException {
		if (language == null) {
			throw new IllegalArgumentException("Language cannot be Null");
		}
		File file = new File(".\\config\\language\\" + language + ".json");
		if (file.exists()) {
			try {
				json = new JSONObject(new String(Files.readAllBytes(file.toPath())));
			} catch (JSONException e) {
				e.printStackTrace();
				throw new IOException("Language Configuration for \"" + language + "\" Could Not Be Loaded");
			}
		} else {
			throw new IOException("Language Configuration for \"" + language + "\" Does Not Exist");
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
	 * would have the the method call <pre>.translate("window", "title");</pre><br/>
	 * To translate the "window" keyword use <pre>.translate("window", "name");</pre>
	 * @param keys sequence of keywords representing the .json-structure
	 * @return
	 */
	public static String translate(String...keys) {
		if (json == null) {
			throw new UnsetLanguageException(currentLanguage != null || currentLanguage.getValue() != null ?
					(getLanguage()+".json does not exist") : "No Laguage is defined", getLanguage());
		}
		JSONObject newJson = json;
		String ret = keys[keys.length-1];
		for (String key : keys) {
			if (newJson.has(key)) {
				try {
					Object temp = newJson.get(key);
					if (temp instanceof JSONArray) {
						String[] strNames = new String[((JSONArray)temp).length()];
						for (int i=strNames.length-1;i>=0;i--) {
							strNames[i] = "" + i;
						}
						newJson = ((JSONArray)temp).toJSONObject(new JSONArray(strNames));
					}
					if (temp instanceof JSONObject) {
						newJson = (JSONObject)temp;
					}
					if (temp instanceof String) {
						ret = (String)temp;
						break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("key \"" + key + "\" not found");
				break;
			}
		}
		return ret;
	}

	/**
	 * Creates and returns a {@link javafx.beans.property.SimpleStringProperty SimpleStringProperty},
	 * is bind to the translation of {@link #translate(String...) translate} invoked with the given keys.
	 * @param keys sequence of keywords representing the .json-structure
	 * @return translated property
	 */
	public static SimpleStringProperty translationProperty(String...keys) {
		SimpleStringProperty strP = new SimpleStringProperty(translate(keys));
		currentLanguage.addListener((v, o, n) -> {
			strP.setValue(translate(keys));
		});
		return strP;
	}
}
