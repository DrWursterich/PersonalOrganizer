package settings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.scene.paint.Color;

/**
 * Setting for a {@link javafx.scene.paint.Color Color}.
 * @author Mario Schäper
 */
public class ColorSetting extends Setting<Color> {
	private static final long serialVersionUID = -7167640376750733505L;

	public ColorSetting(Color defaultValue) {
		super(defaultValue);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		Color val = (this.getValue() != null ? this.getValue() : this.defaultValue);
		out.writeDouble(val.getRed());
		out.writeDouble(val.getGreen());
		out.writeDouble(val.getBlue());
		out.writeDouble(val.getOpacity());
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.setValue(Color.color(in.readDouble(), in.readDouble(),
				in.readDouble(), in.readDouble()));
	}
}
