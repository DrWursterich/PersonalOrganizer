package views;

import database.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import containerItem.Appointment;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

@SuppressWarnings("restriction")
public class DayView extends DateView {
	private BorderPane pane = new BorderPane();
	
	private class Time {
		private IntegerProperty time = new SimpleIntegerProperty(this, "time", 0);
		
		private Time(byte hour, byte minute) {
			this.time.setValue((short)((hour << 8) + minute));
		}
		
		private Time(byte hour) {
			this(hour, (byte)0);
		}
		
		private byte getHour() {
			return (byte)(this.time.getValue() >> 8);
		}
		
		private byte getMinute() {
			return (byte)(this.time.getValue() & 0xFF);
		}
		
		private IntegerProperty timeProperty() {
			return this.time;
		}
		
		private void setHour(byte hour) {
			this.time.setValue((short)(hour << 8 + this.getMinute()));
		}
		
		private void setMinute(short minute) {
			this.time.setValue((short)(this.getHour() << 8 + minute));
		}
		
		private short inMinutes() {
			return (short)(this.getHour() * 60 + this.getMinute());
		}
	}
	
	private class DayScale extends Pane {
		private DatabaseController database;
		private Font font;
		private Time start;
		private Time end;
		
		private DayScale(DatabaseController database, Time start, Time end) {
			this.database = database;
			this.start = start;
			this.end = end;
			this.font = Font.font("Courier New");
			this.setHeight(300);
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
			Rectangle backgroundLeft = new Rectangle(0, 0, 50, 1);
			backgroundLeft.heightProperty().bind(this.heightProperty());
			backgroundLeft.setFill(Color.BURLYWOOD);
			Rectangle backgroundRight = new Rectangle(50, 0, 150, 1);
			backgroundRight.xProperty().bind(backgroundLeft.xProperty().add(backgroundLeft.widthProperty()));
			backgroundRight.heightProperty().bind(this.heightProperty());
			backgroundRight.setFill(Color.AQUAMARINE);
			this.getChildren().addAll(backgroundLeft, backgroundRight);
			double heightPerMinute = this.getHeight() / (this.end.inMinutes()-this.start.inMinutes());
			for (byte i=(byte)Math.ceil((double)this.start.inMinutes()/60);i<=this.end.getHour();i++) {
				double height = (i*60-this.start.inMinutes())*heightPerMinute;
				Line line = new Line(0, height, 50, height);
				line.startXProperty().bind(backgroundLeft.xProperty().add(line.strokeWidthProperty().divide(2)));
				line.endXProperty().bind(backgroundLeft.xProperty().add(backgroundLeft.widthProperty())
						.subtract(line.strokeWidthProperty().divide(2)));
				Label label = new Label(String.format("%2d:00", Math.abs(i%24)));
				label.setFont(this.font); 
				label.setPrefWidth(40);
				label.relocate(10, height+1);
				Line line2 = new Line(50, height, 200, height);
				line2.startXProperty().bind(backgroundRight.xProperty().add(line2.strokeWidthProperty().divide(2)));
				line2.endXProperty().bind(backgroundRight.xProperty().add(backgroundRight.widthProperty())
						.subtract(line2.strokeWidthProperty().divide(2)));
				this.getChildren().addAll(line, label, line2);
			}
			ArrayList<Appointment> appointments = this.database.getDayAppointments(date);
			if (appointments != null) {
				for (Appointment a : appointments) {
					Rectangle appRec = new Rectangle();
					appRec.xProperty().bind(backgroundRight.xProperty().add(10));
					appRec.widthProperty().bind(backgroundRight.widthProperty().subtract(20));
					appRec.yProperty().bind(backgroundRight.yProperty().add(
							((a.getStartDay() == date.get(GregorianCalendar.DAY_OF_MONTH) ?
								a.getStartHour()*60+a.getStartMinute() : 0)-this.start.inMinutes())*heightPerMinute));
					appRec.heightProperty().bind(backgroundRight.yProperty().subtract(appRec.yProperty()).add(
							((a.getEndDay() == date.get(GregorianCalendar.DAY_OF_MONTH) ?
								a.getEndHour()*60+a.getEndMinute() : 1440)-this.start.inMinutes())*heightPerMinute));
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
		Rectangle topBar = new Rectangle(10, 40);
		topBar.setManaged(false);
		topBar.setFill(Color.GAINSBORO);
		topBar.setStroke(Color.SLATEGREY);
		topBar.setStrokeWidth(3);
		topBar.widthProperty().bind(this.widthProperty());
		topBar.yProperty().bind(topBar.strokeWidthProperty().divide(2));
		this.pane.setTop(topBar);
		
		DayScale dayScale = new DayScale(database, new Time((byte)7, (byte)0), new Time((byte)22, (byte)0));
		this.pane.setCenter(dayScale);
		
		this.getChildren().add(this.pane);
	}
	
	public void update() {
		DayView me = this;
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
