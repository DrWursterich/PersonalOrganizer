package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class that translates keywords into a specific language using .json-files.<br/>
 * A language consists of a String of two chars, as an abbreviation
 * for the language it represents. A .json-file with the same name
 * has to be located in the ../config/language folder.
 * @author Mario Schäper
 */
public class Translator {
	private String currentLanguage;
	private JSONObject json;

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

	public Translator(String language) throws IllegalArgumentException, IOException {
		this.setLanguage(language);
	}

	/**
	 * Returns the language.<br/>
	 * A language consists of a String of two chars, as an abbreviation
	 * for the language it represents. A .json-file with the same name
	 * has to be located in the ../config/language folder.
	 * @return
	 */
	public String getLanguage() {
		return this.currentLanguage;
	}

	/**
	 * Sets the language.<br/>
	 * A language consists of a String of two chars, as an abbreviation
	 * for the language it represents. A .json-file with the same name
	 * has to be located in the ../config/language folder.
	 * @param language the new language
	 * @throws IOException if the .json-file could not be loaded
	 */
	public void setLanguage(String language) throws IOException {
		if (language == null) {
			throw new IllegalArgumentException("Language cannot be Null");
		}
		File file = new File(".\\config\\language\\" + language + ".json");
		if (file.exists()) {
			try {
				this.json = new JSONObject(new String(Files.readAllBytes(file.toPath())));
			} catch (JSONException e) {
				throw new IOException("Language Configuration for \"" + language + "\" Could Not Be Loaded");
			}
		} else {
			throw new IOException("Language Configuration for \"" + language + "\" Does Not Exist");
		}
		this.currentLanguage = language;
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
	public String translate(String...keys) {
		if (this.json == null) {
			throw new UnsetLanguageException(this.currentLanguage != null ?
					(this.currentLanguage+".json does not exist") : "No Laguage is defined", this.currentLanguage);
		}
		JSONObject json = this.json;
		String ret = keys[keys.length-1];
		for (String key : keys) {
			if (json.has(key)) {
				try {
					Object temp = json.get(key);
					if (temp instanceof JSONArray) {
						String[] strNames = new String[((JSONArray)temp).length()];
						for (int i=strNames.length-1;i>=0;i--) {
							strNames[i] = "" + i;
						}
						json = ((JSONArray)temp).toJSONObject(new JSONArray(strNames));
					}
					if (temp instanceof JSONObject) {
						json = (JSONObject)temp;
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
}
