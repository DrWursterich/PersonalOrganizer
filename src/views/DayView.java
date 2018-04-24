package views;

import database.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import containerItem.Appointment;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

@SuppressWarnings("restriction")
public class DayView extends DateView {
	@SuppressWarnings("unused")
	private class Time extends SimpleIntegerProperty {
		private IntegerProperty time = new SimpleIntegerProperty(this, "time", 0);
		private IntegerProperty minutes = new SimpleIntegerProperty(0);
		private IntegerProperty hours = new SimpleIntegerProperty(0);

		private Time(int hour, int minute) {
			this.hours.bind(time.divide(60));
			this.minutes.bind(time.subtract(hours));
			this.time.setValue(hour*60 + minute);
		}

		private Time(int hour) {
			this(hour, 0);
		}

		private int getTime() {
			return this.time.getValue();
		}

		private int getHour() {
			return this.hours.getValue();
		}

		private int getMinute() {
			return this.minutes.getValue();
		}

		private IntegerProperty timeProperty() {
			return this.time;
		}

		private IntegerProperty hourProperty() {
			return this.hours;
		}

		private IntegerProperty minuteProperty() {
			return this.minutes;
		}

		private void setHour(int hour) {
			this.time.setValue((short)(hour << 8 + this.getMinute()));
		}

		private void setMinute(int minute) {
			this.time.setValue((short)(this.getHour() << 8 + minute));
		}
	}

	@SuppressWarnings("unused")
	private class DayScale extends Region {
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

		private Time getStart() {
			return this.start;
		}

		private Time getEnd() {
			return this.end;
		}

		private void update() {
			this.getChildren().clear();
			Rectangle backgroundLeft = new Rectangle() {
				@Override
				public double minWidth(double height) {
					return 0;
				}
			};
			backgroundLeft.widthProperty().bind(this.widthProperty().multiply(0.25));
			backgroundLeft.heightProperty().bind(this.heightProperty());
			backgroundLeft.setFill(Color.BURLYWOOD);
			Rectangle backgroundRight = new Rectangle() {
				@Override
				public double minWidth(double height) {
					return 0;
				}
			};
			backgroundRight.widthProperty().bind(this.widthProperty().subtract(backgroundLeft.widthProperty()));
			backgroundRight.xProperty().bind(backgroundLeft.xProperty().add(backgroundLeft.widthProperty()));
			backgroundRight.heightProperty().bind(this.heightProperty());
			backgroundRight.setFill(Color.AQUAMARINE);
			this.getChildren().addAll(backgroundLeft, backgroundRight);
			DoubleProperty heightPerMinute = new SimpleDoubleProperty();
			heightPerMinute.bind(this.heightProperty().divide(this.end.timeProperty().subtract(this.start.timeProperty())));
			for (byte i=(byte)Math.ceil((double)this.start.timeProperty().doubleValue()/60);i<=this.end.getHour();i++) {
				DoubleProperty height = new SimpleDoubleProperty();
				height.bind(heightPerMinute.multiply(this.start.timeProperty().multiply(-1).add(i*60)));
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
								a.getStartHour()*60+a.getStartMinute() : 0)-this.start.getTime())));
					appRec.heightProperty().bind(backgroundRight.yProperty().subtract(appRec.yProperty()).add(
							heightPerMinute.multiply((a.getEndDay() == date.get(GregorianCalendar.DAY_OF_MONTH) ?
								a.getEndHour()*60+a.getEndMinute() : 1440)-this.start.getTime())));
					appRec.setFill(Color.KHAKI);
					appRec.setStroke(Color.DARKKHAKI);
					Label subjectLabel = new Label(a.getSubject());
					subjectLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
					subjectLabel.translateXProperty().bind(appRec.xProperty().add(5));
					subjectLabel.translateYProperty().bind(appRec.yProperty().add(5));
					Label descriptionLabel = new Label(a.getDescription());
					descriptionLabel.setFont(Font.font("Verdana", 12));
					descriptionLabel.translateXProperty().bind(appRec.xProperty().add(5));
					descriptionLabel.translateYProperty().bind(appRec.yProperty().add(subjectLabel.heightProperty()).add(10));
					this.getChildren().addAll(appRec, subjectLabel, descriptionLabel);
				}
			}
		}
	}

	public DayView(DatabaseController database, GregorianCalendar date) {
		super(database, date);
		Rectangle topBar = new Rectangle(10, 40) {
			@Override
			public double minWidth(double height) {
				return 0;
			}
		};
		topBar.setFill(Color.GAINSBORO);
		topBar.setStroke(Color.SLATEGREY);
		topBar.setStrokeWidth(3);
		topBar.widthProperty().bind(this.widthProperty());
		topBar.yProperty().bind(topBar.strokeWidthProperty().divide(2));

		DayScale dayScale = new DayScale(database, new Time(7, 0), new Time(22, 0));
		VBox.setVgrow(dayScale, Priority.ALWAYS);

		this.getChildren().addAll(topBar, dayScale);
	}

	public void update() {
		(new Task<Void>() {
			protected Void call() throws Exception {
				Platform.runLater(new Runnable() {
					public void run() {
					}
				});
				return null;
			}
		}).run();
	}
}
