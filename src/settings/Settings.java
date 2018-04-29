package settings;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Static Class that holds information for all settings.
 * @author Mario Schäper
 */
public abstract class Settings {
	public final static Color DEFAULT_DAYVIEW_TOPBAR_BACKGROUND_COLOR = Color.GAINSBORO;
	public final static Color DEFAULT_DAYVIEW_TOPBAR_STROKE_COLOR = Color.SLATEGREY;
	public final static Color DEFAULT_DAYVIEW_BACKGROUND_LEFT_COLOR = Color.BURLYWOOD;
	public final static Color DEFAULT_DAYVIEW_BACKGROUND_RIGHT_COLOR = Color.AQUAMARINE;
	public final static Font  DEFAULT_DAYVIEW_TIMESTAMP_FONT = Font.font("Courier New");
	public final static Color DEFAULT_DAYVIEW_APPOINTMENT_BACKGROUND_COLOR = Color.KHAKI;
	public final static Color DEFAULT_DAYVIEW_APPOINTMENT_STROKE_COLOR = Color.DARKKHAKI;
	public final static Font  DEFAULT_DAYVIEW_APPOINTMENT_SUBJECT_FONT = Font.font("Verdana", FontWeight.BOLD, 14);
	public final static Font  DEFAULT_DAYVIEW_APPOINTMENT_DESCRIPTION_FONT = Font.font("Verdana", 12);
	
	public static final ObjectProperty<Color> DAYVIEW_TOPBAR_BACKGROUND_COLOR =
			new SimpleObjectProperty<Color>(DEFAULT_DAYVIEW_TOPBAR_BACKGROUND_COLOR);
	public static final ObjectProperty<Color> DAYVIEW_TOPBAR_STROKE_COLOR =
			new SimpleObjectProperty<Color>(DEFAULT_DAYVIEW_TOPBAR_STROKE_COLOR);
	public static final ObjectProperty<Color> DAYVIEW_BACKGROUND_LEFT_COLOR =
			new SimpleObjectProperty<Color>(DEFAULT_DAYVIEW_BACKGROUND_LEFT_COLOR);
	public static final ObjectProperty<Color> DAYVIEW_BACKGROUND_RIGHT_COLOR =
			new SimpleObjectProperty<Color>(DEFAULT_DAYVIEW_BACKGROUND_RIGHT_COLOR);
	public static final ObjectProperty<Font>  DAYVIEW_TIMESTAMP_FONT =
			new SimpleObjectProperty<Font>(DEFAULT_DAYVIEW_TIMESTAMP_FONT);
	public static final ObjectProperty<Color> DAYVIEW_APPOINTMENT_BACKGROUND_COLOR =
			new SimpleObjectProperty<Color>(DEFAULT_DAYVIEW_APPOINTMENT_BACKGROUND_COLOR);
	public static final ObjectProperty<Color> DAYVIEW_APPOINTMENT_STROKE_COLOR =
			new SimpleObjectProperty<Color>(DEFAULT_DAYVIEW_APPOINTMENT_STROKE_COLOR);
	public static final ObjectProperty<Font>  DAYVIEW_APPOINTMENT_SUBJECT_FONT =
			new SimpleObjectProperty<Font>(DEFAULT_DAYVIEW_APPOINTMENT_SUBJECT_FONT);
	public static final ObjectProperty<Font>  DAYVIEW_APPOINTMENT_DESCRIPTION_FONT =
			new SimpleObjectProperty<Font>(DEFAULT_DAYVIEW_APPOINTMENT_DESCRIPTION_FONT);
	
	/**
	 * Restores the default settings for all propertys.
	 */
	public static void setDefaults() {
		DAYVIEW_TOPBAR_BACKGROUND_COLOR.setValue(DEFAULT_DAYVIEW_TOPBAR_BACKGROUND_COLOR);
		DAYVIEW_TOPBAR_STROKE_COLOR.setValue(DEFAULT_DAYVIEW_TOPBAR_STROKE_COLOR);
		DAYVIEW_BACKGROUND_LEFT_COLOR.setValue(DEFAULT_DAYVIEW_BACKGROUND_LEFT_COLOR);
		DAYVIEW_BACKGROUND_RIGHT_COLOR.setValue(DEFAULT_DAYVIEW_BACKGROUND_RIGHT_COLOR);
		DAYVIEW_TIMESTAMP_FONT.setValue(DEFAULT_DAYVIEW_TIMESTAMP_FONT);
		DAYVIEW_APPOINTMENT_BACKGROUND_COLOR.setValue(DEFAULT_DAYVIEW_APPOINTMENT_BACKGROUND_COLOR);
		DAYVIEW_APPOINTMENT_STROKE_COLOR.setValue(DEFAULT_DAYVIEW_APPOINTMENT_STROKE_COLOR);
		DAYVIEW_APPOINTMENT_SUBJECT_FONT.setValue(DEFAULT_DAYVIEW_APPOINTMENT_SUBJECT_FONT);
		DAYVIEW_APPOINTMENT_DESCRIPTION_FONT.setValue(DEFAULT_DAYVIEW_APPOINTMENT_DESCRIPTION_FONT);
	}
}
