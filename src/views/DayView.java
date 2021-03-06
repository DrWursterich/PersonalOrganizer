package views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.layout.Pane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import settings.SettingController;
import database.DatabaseController;
import database.appointment.Appointment;
import de.schaeper.fx.bindings.ConverterBinding;
import de.schaeper.fx.scene.text.font.Font;
import util.ResizableRectangle;
import util.Time;

/**
 * Class that allows a visualisation of {@link Appointment Appointments} in a
 * {@link DatabaseController Database} by displaying them for individual days in
 * a format similar to a table.
 *
 * @author Mario Schäper
 */
public class DayView extends DateView {
	private ResizableRectangle topBar;
	private DayScale dayScale;

	/**
	 * Represents the scale of a day, in which {@link Appointment Appointments}
	 * can be displayed
	 *
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
			backgroundLeft.widthProperty().set(60);
			backgroundLeft.heightProperty().bind(this.heightProperty());
			backgroundLeft.fillProperty().bind(SettingController.get().DAYVIEW_BACKGROUND_LEFT_COLOR);
			Rectangle backgroundRight = new ResizableRectangle();
			backgroundRight.widthProperty()
					.bind(this.widthProperty().subtract(backgroundLeft.widthProperty()));
			backgroundRight.xProperty()
					.bind(backgroundLeft.xProperty().add(backgroundLeft.widthProperty()));
			backgroundRight.heightProperty().bind(this.heightProperty());
			backgroundRight.fillProperty().bind(SettingController.get().DAYVIEW_BACKGROUND_RIGHT_COLOR);
			this.getChildren().addAll(backgroundLeft, backgroundRight);
			DoubleProperty heightPerMinute = new SimpleDoubleProperty();
			heightPerMinute.bind(this.heightProperty().divide(this.end.subtract(this.start)));
			for (byte i = (byte)Math.ceil(this.start.doubleValue() / 60); i <= this.end
					.getHour(); i++) {
				DoubleProperty height = new SimpleDoubleProperty();
				height.bind(heightPerMinute.multiply(this.start.multiply(-1).add(i * 60)));
				Line line = new Line();
				line.startYProperty().bind(height);
				line.endYProperty().bind(height);
				line.startXProperty()
						.bind(backgroundLeft.xProperty().add(line.strokeWidthProperty().divide(2)));
				line.endXProperty()
						.bind(backgroundLeft.xProperty().add(backgroundLeft.widthProperty())
								.subtract(line.strokeWidthProperty().divide(2)));
				Label label = new Label(String.format("%2d:00", Math.abs(i % 24)));
				ConverterBinding.bind(
						SettingController.get().DAYVIEW_TIMESTAMP_FONT,
						label.fontProperty(),
						Font::getFxFont);
				label.setPrefWidth(40);
				label.layoutYProperty().bind(height.add(1));
				Line line2 = new Line();
				line2.startYProperty().bind(height);
				line2.endYProperty().bind(height);
				line2.startXProperty().bind(
						backgroundRight.xProperty().add(line2.strokeWidthProperty().divide(2)));
				line2.endXProperty()
						.bind(backgroundRight.xProperty().add(backgroundRight.widthProperty())
								.subtract(line2.strokeWidthProperty().divide(2)));
				this.getChildren().addAll(line, label, line2);
			}
			ArrayList<Appointment> appointments = DatabaseController
					.getDayAppointments(DayView.this.date);
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
					appRec.yProperty()
							.bind(backgroundRight.yProperty().add(heightPerMinute.multiply(
									(a.getStartDay() == DayView.this.date.get(Calendar.DAY_OF_MONTH)
											? a.getStartHour() * 60 + a.getStartMinute()
											: 0) - this.start.getValue())));
					appRec.heightProperty().bind(backgroundRight.yProperty()
							.subtract(appRec.yProperty())
							.add(heightPerMinute.multiply(
									(a.getEndDay() == DayView.this.date.get(Calendar.DAY_OF_MONTH)
											? a.getEndHour() * 60 + a.getEndMinute()
											: 1440) - this.start.getValue())));
					appRec.fillProperty().bind(SettingController.get().DAYVIEW_APPOINTMENT_BACKGROUND_COLOR);
					appRec.strokeProperty().bind(SettingController.get().DAYVIEW_APPOINTMENT_STROKE_COLOR);
					Label subjectLabel = new Label();
					subjectLabel.textProperty().bind(a.subjectProperty());
					ConverterBinding.bind(
							SettingController.get().DAYVIEW_APPOINTMENT_SUBJECT_FONT,
							subjectLabel.fontProperty(),
							Font::getFxFont);
					subjectLabel.translateXProperty().bind(appRec.xProperty().add(5));
					subjectLabel.translateYProperty().bind(appRec.yProperty().add(5));
					Label descriptionLabel = new Label();
					descriptionLabel.textProperty().bind(a.descriptionProperty());
					ConverterBinding.bind(
							SettingController.get().DAYVIEW_APPOINTMENT_DESCRIPTION_FONT,
							descriptionLabel.fontProperty(),
							Font::getFxFont);
					descriptionLabel.translateXProperty().bind(appRec.xProperty().add(5));
					descriptionLabel.translateYProperty()
							.bind(appRec.yProperty().add(subjectLabel.heightProperty()).add(10));
					this.getChildren().addAll(appRec, subjectLabel, descriptionLabel);
				}
			}
		}

		@Override
		public void setHeight(double height) {
			super.setHeight(700);
		}
	}

	public DayView(GregorianCalendar date) {
		super(date);

		this.topBar = new ResizableRectangle(0, 40);
		this.topBar.setResizeHeight(false);
		this.topBar.fillProperty().bind(SettingController.get().DAYVIEW_TOPBAR_BACKGROUND_COLOR);
		this.topBar.strokeProperty().bind(SettingController.get().DAYVIEW_TOPBAR_STROKE_COLOR);
		this.topBar.setStrokeWidth(3);
		this.topBar.widthProperty()
				.bind(this.widthProperty().subtract(this.topBar.strokeWidthProperty()));
		this.topBar.yProperty().bind(this.topBar.strokeWidthProperty().divide(2));

		// this.dayScale = new DayScale(new Time(0, 0), new Time(24, 0));
		// ScrollPane sp = new ScrollPane(this.dayScale);
		// VBox.setVgrow(sp, Priority.ALWAYS);
		// sp.setFitToWidth(true);
		// sp.setFitToHeight(true);
		// this.getChildren().addAll(this.topBar, sp);

		this.dayScale = new DayScale(new Time(0, 0), new Time(24, 0));
		ScrollPane sp = new ScrollPane(this.dayScale);
		// VBox.setVgrow(sp, Priority.ALWAYS);
		sp.setFitToWidth(true);
		// sp.setFitToHeight(true);
		sp.setHbarPolicy(ScrollBarPolicy.NEVER);
		sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		this.getChildren().addAll(this.topBar, sp);
	}

	/**
	 * Refreshes all visuals of the instance
	 */
	@Override
	public void update() {
		(new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Platform.runLater(DayView.this.dayScale::update);
				return null;
			}
		}).run();
	}
}
