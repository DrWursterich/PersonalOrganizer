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
import javafx.scene.control.Label;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import settings.Settings;
import database.DatabaseController;
import containerItem.Appointment;
import util.ResizableRectangle;
import util.Time;

/**
 * Class that allows a visualisation of {@link Appointment Appointments}
 * in a {@link DatabaseController Database} by displaying them for
 * individual days in a format similar to a table.
 * @author Mario Schäper
 */
public class DayView extends DateView {
	private Rectangle topBar;
	private DayScale dayScale;

	/**
	 * Represents the scale of a day, in which {@link Appointment Appointments} can be displayed
	 * @author Mario Schäper
	 */
	@SuppressWarnings("unused")
	private class DayScale extends Pane {
		private Time start;
		private Time end;

		private DayScale(Time start, Time end) {
			this.start = start;
			this.end = end;
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
			backgroundLeft.fillProperty().bind(Settings.DAYVIEW_BACKGROUND_LEFT_COLOR);
			Rectangle backgroundRight = new ResizableRectangle();
			backgroundRight.widthProperty().bind(this.widthProperty().subtract(backgroundLeft.widthProperty()));
			backgroundRight.xProperty().bind(backgroundLeft.xProperty().add(backgroundLeft.widthProperty()));
			backgroundRight.heightProperty().bind(this.heightProperty());
			backgroundRight.fillProperty().bind(Settings.DAYVIEW_BACKGROUND_RIGHT_COLOR);
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
				label.fontProperty().bind(Settings.DAYVIEW_TIMESTAMP_FONT);
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
			ArrayList<Appointment> appointments = DatabaseController.getDayAppointments(date);
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
					appRec.fillProperty().bind(Settings.DAYVIEW_APPOINTMENT_BACKGROUND_COLOR);
					appRec.strokeProperty().bind(Settings.DAYVIEW_APPOINTMENT_STROKE_COLOR);
					Label subjectLabel = new Label();
					subjectLabel.textProperty().bind(a.subjectProperty());
					subjectLabel.fontProperty().bind(Settings.DAYVIEW_APPOINTMENT_SUBJECT_FONT);
					subjectLabel.translateXProperty().bind(appRec.xProperty().add(5));
					subjectLabel.translateYProperty().bind(appRec.yProperty().add(5));
					Label descriptionLabel = new Label();
					descriptionLabel.textProperty().bind(a.descriptionProperty());
					descriptionLabel.fontProperty().bind(Settings.DAYVIEW_APPOINTMENT_DESCRIPTION_FONT);
					descriptionLabel.translateXProperty().bind(appRec.xProperty().add(5));
					descriptionLabel.translateYProperty().bind(appRec.yProperty().add(subjectLabel.heightProperty()).add(10));
					this.getChildren().addAll(appRec, subjectLabel, descriptionLabel);
				}
			}
		}
	}

	public DayView(GregorianCalendar date) {
		super(date);

		this.topBar = new ResizableRectangle(10, 40);
		this.topBar.fillProperty().bind(Settings.DAYVIEW_TOPBAR_BACKGROUND_COLOR);
		this.topBar.strokeProperty().bind(Settings.DAYVIEW_TOPBAR_STROKE_COLOR);
		this.topBar.setStrokeWidth(3);
		this.topBar.widthProperty().bind(this.widthProperty().subtract(this.topBar.strokeWidthProperty()));
		this.topBar.yProperty().bind(this.topBar.strokeWidthProperty().divide(2));

		this.dayScale = new DayScale(new Time(0, 0), new Time(24, 0));
		ScrollPane sp = new ScrollPane(this.dayScale);
		VBox.setVgrow(sp, Priority.ALWAYS);
		sp.setFitToWidth(true);
		sp.setFitToHeight(true);
		this.getChildren().addAll(this.topBar, sp);
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
