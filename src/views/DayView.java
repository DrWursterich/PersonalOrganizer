package views;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Pane;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import util.ResizableRectangle;
import util.Time;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import database.DatabaseController;
import containerItem.Appointment;

/**
 * Class that allows a visualisation of {@link Appointment Appointments}
 * in a {@link DatabaseController Database} by displaying them for
 * individual days in a format similar to a table.
 * @author Mario Schäper
 */
public class DayView extends DateView {
	private Rectangle topBar;
	private DayScale dayScale;
	private ObjectProperty<Font> subjectFont;
	private ObjectProperty<Font> descriptionFont;
	private ObjectProperty<Color> topBarBackgroundColor;
	private ObjectProperty<Color> topBarStrokeColor;
	private ObjectProperty<Color> backgroundLeftColor;
	private ObjectProperty<Color> backgroundRightColor;
	private ObjectProperty<Color> appointmentBackgroundColor;
	private ObjectProperty<Color> appointmentStrokeColor;

	/**
	 * Represents the scale of a day, in which {@link Appointment Appointments} can be displayed
	 * @author Mario Schäper
	 */
	private class DayScale extends Pane {
		private DatabaseController database;
		private ObjectProperty<Font> font;
		private Time start;
		private Time end;

		private DayScale(DatabaseController database, Time start, Time end) {
			this.database = database;
			this.start = start;
			this.end = end;
			this.font = new SimpleObjectProperty<Font>(Font.font("Courier New"));
			this.update();
		}

		/**
		 * @return the Time property of the start of the scale
		 */
		private Time startProperty() {
			return this.start;
		}

		/**
		 * @return the Time property of the end of the scale
		 */
		private Time endProperty() {
			return this.end;
		}

		/**
		 * Refreshes the visuals for the instance
		 */
		private void update() {
			this.getChildren().clear();
			Rectangle backgroundLeft = new ResizableRectangle();
			backgroundLeft.widthProperty().bind(this.widthProperty().multiply(0.25));
			backgroundLeft.heightProperty().bind(this.heightProperty());
			backgroundLeft.fillProperty().bind(backgroundLeftColor);
			Rectangle backgroundRight = new ResizableRectangle();
			backgroundRight.widthProperty().bind(this.widthProperty().subtract(backgroundLeft.widthProperty()));
			backgroundRight.xProperty().bind(backgroundLeft.xProperty().add(backgroundLeft.widthProperty()));
			backgroundRight.heightProperty().bind(this.heightProperty());
			backgroundRight.fillProperty().bind(backgroundRightColor);
			this.getChildren().addAll(backgroundLeft, backgroundRight);
			DoubleProperty heightPerMinute = new SimpleDoubleProperty();
			heightPerMinute.bind(this.heightProperty().divide(this.end.subtract(this.start)));
			for (byte i=(byte)Math.ceil((double)this.start.doubleValue()/60);i<=this.end.getHour();i++) {
				DoubleProperty height = new SimpleDoubleProperty();
				height.bind(heightPerMinute.multiply(this.start.multiply(-1).add(i*60)));
				Line line = new Line();
				line.startYProperty().bind(height);
				line.endYProperty().bind(height);
				line.startXProperty().bind(backgroundLeft.xProperty().add(line.strokeWidthProperty().divide(2)));
				line.endXProperty().bind(backgroundLeft.xProperty().add(backgroundLeft.widthProperty())
						.subtract(line.strokeWidthProperty().divide(2)));
				Label label = new Label(String.format("%2d:00", Math.abs(i%24)));
				label.fontProperty().bind(this.font);
				label.setPrefWidth(40);
				label.layoutYProperty().bind(height.add(1));
				Line line2 = new Line();
				line2.startYProperty().bind(height);
				line2.endYProperty().bind(height);
				line2.startXProperty().bind(backgroundRight.xProperty().add(line2.strokeWidthProperty().divide(2)));
				line2.endXProperty().bind(backgroundRight.xProperty().add(backgroundRight.widthProperty())
						.subtract(line2.strokeWidthProperty().divide(2)));
				this.getChildren().addAll(line, label, line2);
			}
			ArrayList<Appointment> appointments = this.database.getDayAppointments(date);
			if (appointments != null) {
				for (Appointment a : appointments) {
					Rectangle appRec = new Rectangle() {
						@Override
						public double minWidth(double height) {
							return 0;
						}
					};
					appRec.xProperty().bind(backgroundRight.xProperty().add(10));
					appRec.widthProperty().bind(backgroundRight.widthProperty().subtract(20));
					appRec.yProperty().bind(backgroundRight.yProperty().add(
							heightPerMinute.multiply((a.getStartDay() == date.get(GregorianCalendar.DAY_OF_MONTH) ?
								a.getStartHour()*60+a.getStartMinute() : 0)-this.start.getValue())));
					appRec.heightProperty().bind(backgroundRight.yProperty().subtract(appRec.yProperty()).add(
							heightPerMinute.multiply((a.getEndDay() == date.get(GregorianCalendar.DAY_OF_MONTH) ?
								a.getEndHour()*60+a.getEndMinute() : 1440)-this.start.getValue())));
					appRec.fillProperty().bind(appointmentBackgroundColor);
					appRec.strokeProperty().bind(appointmentStrokeColor);
					Label subjectLabel = new Label();
					subjectLabel.textProperty().bind(a.subjectProperty());
					subjectLabel.fontProperty().bind(subjectFontProperty());
					subjectLabel.translateXProperty().bind(appRec.xProperty().add(5));
					subjectLabel.translateYProperty().bind(appRec.yProperty().add(5));
					Label descriptionLabel = new Label();
					descriptionLabel.textProperty().bind(a.descriptionProperty());
					descriptionLabel.fontProperty().bind(descriptionFontProperty());
					descriptionLabel.translateXProperty().bind(appRec.xProperty().add(5));
					descriptionLabel.translateYProperty().bind(appRec.yProperty().add(subjectLabel.heightProperty()).add(10));
					this.getChildren().addAll(appRec, subjectLabel, descriptionLabel);
				}
			}
		}
	}

	public DayView(DatabaseController database, GregorianCalendar date) {
		super(database, date);
		this.subjectFont = new SimpleObjectProperty<Font>(Font.font("Verdana", FontWeight.BOLD, 14));
		this.descriptionFont = new SimpleObjectProperty<Font>(Font.font("Verdana", 12));
		this.backgroundLeftColor = new SimpleObjectProperty<Color>(Color.BURLYWOOD);
		this.backgroundRightColor = new SimpleObjectProperty<Color>(Color.AQUAMARINE);
		this.appointmentBackgroundColor = new SimpleObjectProperty<Color>(Color.KHAKI);
		this.appointmentStrokeColor = new SimpleObjectProperty<Color>(Color.DARKKHAKI);
		this.topBarBackgroundColor = new SimpleObjectProperty<Color>(Color.GAINSBORO);
		this.topBarStrokeColor = new SimpleObjectProperty<Color>(Color.SLATEGREY);

		this.topBar = new ResizableRectangle(10, 40);
		this.topBar.fillProperty().bind(this.topBarBackgroundColor);
		this.topBar.strokeProperty().bind(this.topBarStrokeColor);
		this.topBar.setStrokeWidth(3);
		this.topBar.widthProperty().bind(this.widthProperty().subtract(this.topBar.strokeWidthProperty()));
		this.topBar.yProperty().bind(this.topBar.strokeWidthProperty().divide(2));

		this.dayScale = new DayScale(database, new Time(0, 0), new Time(24, 0));
		ScrollPane sp = new ScrollPane(this.dayScale);
		VBox.setVgrow(sp, Priority.ALWAYS);
		sp.setFitToWidth(true);
		sp.setFitToHeight(true);
		this.getChildren().addAll(this.topBar, sp);
	}

	/**
	 * @param font the new Font for the subjects of {@link Appointment Appointments}
	 */
	public void setSubjectFont(Font font) {
		this.subjectFont.set(font);
	}

	/**
	 * @param font the new Font for the descriptions of {@link Appointment Appointments}
	 */
	public void setDescriptionFont(Font font) {
		this.descriptionFont.set(font);
	}

	/**
	 * @param color the new Color for the left background
	 */
	public void setBackgroundLeftColor(Color color) {
		this.backgroundLeftColor.setValue(color);
	}

	/**
	 * @param color the new Color for the right background
	 */
	public void setBackgroundRightColor(Color color) {
		this.backgroundRightColor.setValue(color);
	}

	/**
	 * @param color the new Color for the background of all {@link Appointment Appointments}
	 */
	public void setAppointmentBackgroundColor(Color color) {
		this.appointmentBackgroundColor.setValue(color);
	}

	/**
	 * @param color the new Color for the stroke/border of all {@link Appointment Appointments}
	 */
	public void setAppointmentStrokeColor(Color color) {
		this.appointmentStrokeColor.setValue(color);
	}
	
	/**
	 * @return the Font for the subjects of {@link Appointment Appointments}
	 */
	public Font getSubjectFont() {
		return this.subjectFont.getValue();
	}

	/**
	 * @return the Font for the descriptions of {@link Appointment Appointments}
	 */
	public Font getDescriptionFont() {
		return this.descriptionFont.getValue();
	}

	/**
	 * @return the Color for the left background
	 */
	public Color getBackgroundLeftColor() {
		return this.backgroundLeftColor.getValue();
	}

	/**
	 * @return the Color for the right background
	 */
	public Color getBackgroundRightColor() {
		return this.backgroundRightColor.getValue();
	}

	/**
	 * @return the Color for the background of all {@link Appointment Appointments}
	 */
	public Color getAppointmentBackgroundColor() {
		return this.appointmentBackgroundColor.getValue();
	}

	/**
	 * @return the Color for the stroke/border of all {@link Appointment Appointments}
	 */
	public Color getAppointmentStrokeColor() {
		return this.appointmentStrokeColor.getValue();
	}
	
	/**
	 * @return the Font property for the subjects of {@link Appointment Appointments}
	 */
	public ObjectProperty<Font> subjectFontProperty() {
		return this.subjectFont;
	}

	/**
	 * @return the Font property for the descriptions of {@link Appointment Appointments}
	 */
	public ObjectProperty<Font> descriptionFontProperty() {
		return this.descriptionFont;
	}

	/**
	 * @return the Color property for the left background
	 */
	public ObjectProperty<Color> backgroundLeftProperty() {
		return this.backgroundLeftColor;
	}

	/**
	 * @return the Color property for the right background
	 */
	public ObjectProperty<Color> backgroundRightProperty() {
		return this.backgroundRightColor;
	}

	/**
	 * @return the Color property for the background of all {@link Appointment Appointments}
	 */
	public ObjectProperty<Color> appointmentBackgroundColorProperty() {
		return this.appointmentBackgroundColor;
	}

	/**
	 * @return the Color property for the stroke/border of all {@link Appointment Appointments}
	 */
	public ObjectProperty<Color> appointmentStrokeColorProperty() {
		return this.appointmentStrokeColor;
	}

	/**
	 * Refreshes all visuals of the instance
	 */
	public void update() {
		DayView me = this;
		(new Task<Void>() {
			protected Void call() throws Exception {
				Platform.runLater(new Runnable() {
					public void run() {
						me.dayScale.update();
					}
				});
				return null;
			}
		}).run();
	}
}
