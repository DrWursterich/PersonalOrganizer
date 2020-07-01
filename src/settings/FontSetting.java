package settings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.schaeper.fx.scene.text.font.Font;
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

	private void writeObject(final ObjectOutputStream out) throws IOException {
		final Font font = this.getValue() != null
				? this.getValue()
				: this.defaultValue;
		out.writeUTF(font.getFamily());
		out.writeDouble(font.getSize());
		out.writeUTF(font.getFontWeight().toString());
		out.writeUTF(font.getFontPosture().toString());
	}

	private void readObject(final ObjectInputStream in)
			throws IOException, ClassNotFoundException {
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
		this.setValue(new Font(family, weight, posture, size));
	}
}
