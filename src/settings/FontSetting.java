package settings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * Setting for a {@link javafx.scene.text.Font Font}.
 * @author Mario Schäper
 */
public class FontSetting extends Setting<Font> {
	private static final long serialVersionUID = 5673127869832467521L;

	public FontSetting(Font defaultValue) {
		super(defaultValue);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		Font val = (this.getValue() != null ? this.getValue() : this.defaultValue);
		String weightString = "NORMAL";
		String postureString = "REGULAR";
		String sub = "";
		if (val.toString().contains("style=")) {
			sub = val.toString().substring(val.toString().indexOf("style=") + 6);
			sub = sub.substring(0, sub.indexOf(",")).toUpperCase();
		}
		if (sub.contains("ITALIC")) {
			postureString = "ITALIC";
			sub = sub.replace("ITALIC", "").trim();
		}
		if (!sub.equals("REGULAR") && !sub.equals("")) {
			weightString = sub;
		}
		out.writeUTF(val.getFamily());
		out.writeDouble(val.getSize());
		out.writeUTF(weightString);
		out.writeUTF(postureString);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		String family = in.readUTF();
		double size = in.readDouble();
		String weightString = in.readUTF();
		String postureString = in.readUTF();
		FontWeight weight = FontWeight.NORMAL;
		FontPosture posture = FontPosture.REGULAR;
		try {
			weight = FontWeight.valueOf(weightString);
			posture = FontPosture.valueOf(postureString);
		} catch (Exception e) {e.printStackTrace();}
		this.setValue(Font.font(family, weight, posture, size));
	}
}
