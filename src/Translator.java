import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Translator {
	private String currentLanguage;
	private JSONObject json;

	public Translator(String language) throws IllegalArgumentException {
		this.setLanguage(language);
	}

	public String getLanguage() {
		return this.currentLanguage;
	}

	public class UnsetLanguageException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public UnsetLanguageException(String message) {
			super(message);
		}
	}

	public boolean setLanguage(String language) {
		if (language == null) {
			throw new IllegalArgumentException("Language cannot be Null");
		}
		File file = new File(".\\config\\language\\" + language + ".json");
		if (file.exists()) {
			try {
				this.json = new JSONObject(new String(Files.readAllBytes(file.toPath())));
			} catch (JSONException | IOException e) {
				System.out.println("Language Configuration Could Not Be Loaded");
				e.printStackTrace();
				return false;
			}
		} else {
			System.out.println("Language Configuration Not Found");
			return false;
		}
		this.currentLanguage = language;
		return true;
	}

	public String translate(String...keys) {
		if (this.json == null) {
			throw new UnsetLanguageException(this.currentLanguage != null ?
					(this.currentLanguage+".json does not exist") : "No Laguage is defined");
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
