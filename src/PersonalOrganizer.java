import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;
import menus.*;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import java.util.GregorianCalendar;

import appointments.NewAppointmentWindow;
import database.DatabaseController;
import util.Translator;
import settings.SettingsWindow;
import views.*;

/**
 * Static Main Class for the Personal Organizer.
 * @author Mario Schäper
 */
public class PersonalOrganizer extends Application {
	private VBox root = new VBox();
	private Scene scene = new Scene(root,
			0.75*Screen.getPrimary().getBounds().getWidth(),
			0.60*Screen.getPrimary().getBounds().getHeight());
	private View view;
	private SettingsWindow settings;
	private Stage parentStage;

	public static void main(String...args) throws Exception {
		GregorianCalendar appointmentStart = new GregorianCalendar();
		GregorianCalendar appointmentEnd = new GregorianCalendar();
		appointmentStart.set(GregorianCalendar.HOUR_OF_DAY, 8);
		appointmentStart.set(GregorianCalendar.MINUTE, 30);
		appointmentEnd.set(GregorianCalendar.HOUR_OF_DAY, 12);
		appointmentEnd.set(GregorianCalendar.MINUTE, 10);
		DatabaseController.addAppointment("Termin", "Beschreibung", appointmentStart, appointmentEnd);

		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.parentStage = stage;

		this.settings = new SettingsWindow(stage);

		this.view = new DayView(new GregorianCalendar());
		this.view.prefWidthProperty().bind(stage.widthProperty());
		VBox.setVgrow(view, Priority.ALWAYS);

		this.root.getChildren().addAll(this.menuBar(), view);

		stage.titleProperty().bind(Translator.translationProperty("title"));
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Creates and returns a menuBar
	 * @return the menuBar
	 */
	private MenuBar menuBar() {
		return new MenuBar(
				new Menu(Translator.translationProperty("menuBar", "file", "name"),
					new Menu(Translator.translationProperty("menuBar", "file", "new", "name"),
						new MenuItem(Translator.translationProperty("menuBar", "file", "new", "meeting"), e -> {
							(new NewAppointmentWindow(this.parentStage)).show();
						}, "Ctrl+N"),
						new MenuItem(Translator.translationProperty("menuBar", "file", "new", "meetings"), e -> {
							System.out.println("Neue Termin Gruppe anlegen");
						}, "Ctrl+Shift+N")),
					new MenuItem(Translator.translationProperty("menuBar", "file", "open"), e -> {
						System.out.println("Datei öffnen");
					}, "Ctrl+O"),
					new MenuItem(Translator.translationProperty("menuBar", "file", "save"), e -> {
						System.out.println("Datei speichern");
					}, "Ctrl+S"),
					new MenuItem(Translator.translationProperty("menuBar", "file", "save as"), e -> {
						System.out.println("Datei speichern unter");
					}, "Ctrl+Shift+S"),
					new MenuItem(Translator.translationProperty("menuBar", "file", "settings"), e -> {
						this.settings.show();
					}, "Ctrl+E"),
					new MenuItem(Translator.translationProperty("menuBar", "file", "exit"), e -> {
						System.exit(0);
					}, "Ctrl+Shift+Q")),
				new Menu(Translator.translationProperty("menuBar", "edit", "name"),
					new MenuItem(Translator.translationProperty("menuBar", "edit", "undo"), e -> {
						System.out.println("Aktion rückgängig machen");
					}, "Ctrl+Z"),
					new MenuItem(Translator.translationProperty("menuBar", "edit", "redo"), e -> {
						System.out.println("Aktion wiederholen");
					}, "Ctrl+Y"),
					new MenuItem(Translator.translationProperty("menuBar", "edit", "cut"), e -> {
						System.out.println("Element ausschneiden");
					}, "Ctrl+X"),
					new MenuItem(Translator.translationProperty("menuBar", "edit", "copy"), e -> {
						System.out.println("Element kopieren");
					}, "Ctrl+C"),
					new MenuItem(Translator.translationProperty("menuBar", "edit", "paste"), e -> {
						System.out.println("Element einfügen");
					}, "Ctrl+V"),
					new MenuItem(Translator.translationProperty("menuBar", "edit", "delete"), e -> {
						System.out.println("Element löschen");
					}, "Ctrl+D"),
					new MenuItem(Translator.translationProperty("menuBar", "edit", "search"), e -> {
						System.out.println("Suchdialog öffnen");
					}, "Ctrl+F")),
				new Menu(Translator.translationProperty("menuBar", "view", "name"),
					new Menu(Translator.translationProperty("menuBar", "view", "switch to", "name"),
						new MenuItem(Translator.translationProperty("menuBar", "view", "switch to", "day view"), e -> {
							System.out.println("Zur Tages Ansicht wechseln");
						}),
						new MenuItem(Translator.translationProperty("menuBar", "view", "switch to", "week view"), e -> {
							System.out.println("Zur Wochen Ansicht wechseln");
						}),
						new MenuItem(Translator.translationProperty("menuBar", "view", "switch to", "month view"), e ->{
							System.out.println("Zur Monats Ansicht wechseln");
						}))),
				new Menu(Translator.translationProperty("menuBar", "help", "name"),
					new MenuItem(Translator.translationProperty("menuBar", "help", "manual"), e -> {
						System.out.println("Handbuch öffnen");
					}, "F1"),
					new MenuItem(Translator.translationProperty("menuBar", "help", "about"), e -> {
						System.out.println("Über Personal Organizer");
					}, "F2")));
	}
}
