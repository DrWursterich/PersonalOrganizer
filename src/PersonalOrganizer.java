import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import java.util.GregorianCalendar;
import database.DatabaseController;
import util.Translator;
import menuBar.*;
import settings.SettingsWindow;
import views.*;

/**
 * Static Main Class for the Personal Organizer.
 * @author Mario Schäper
 */
public class PersonalOrganizer extends Application {
	private static DatabaseController dbController;
	private static View view;

	public static void main(String...args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Translator.setLanguage("de");
		VBox root = new VBox();
		Scene scene = new Scene(root, 0.75*Screen.getPrimary().getBounds().getWidth(),
				0.6*Screen.getPrimary().getBounds().getHeight());
		dbController = new DatabaseController();
		GregorianCalendar appointmentStart = new GregorianCalendar();
		appointmentStart.set(GregorianCalendar.HOUR_OF_DAY, 8);
		appointmentStart.set(GregorianCalendar.MINUTE, 30);
		GregorianCalendar appointmentEnd = new GregorianCalendar();
		appointmentEnd.set(GregorianCalendar.HOUR_OF_DAY, 12);
		appointmentEnd.set(GregorianCalendar.MINUTE, 10);
		dbController.addAppointment("Termin", "Beschreibung", appointmentStart, appointmentEnd);
		view = new DayView(dbController, new GregorianCalendar());
		VBox.setVgrow(view, Priority.ALWAYS);
		root.getChildren().addAll(this.menuBar(), view);
		view.prefWidthProperty().bind(stage.widthProperty());
		stage.setTitle(Translator.translate("title"));
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Creates and returns a menuBar
	 * @return the menuBar
	 */
	private MenuBar menuBar() {
		return new MenuBar(
				new Menu(Translator.translate("menuBar", "file", "name"),
						new Menu(Translator.translate("menuBar", "file", "new", "name"),
								new MenuItem(Translator.translate("menuBar", "file", "new", "meeting"), e -> {
									System.out.println("Neuen Termin anlegen");
								}),
								new MenuItem(Translator.translate("menuBar", "file", "new", "meetings"), e -> {
									System.out.println("Neue Termin Gruppe anlegen");
								})),
						new MenuItem(Translator.translate("menuBar", "file", "open"), e -> {
							System.out.println("Datei öffnen");
						}),
						new MenuItem(Translator.translate("menuBar", "file", "save"), e -> {
							System.out.println("Datei speichern");
						}),
						new MenuItem(Translator.translate("menuBar", "file", "save as"), e -> {
							System.out.println("Datei speichern unter");
						}),
						new MenuItem(Translator.translate("menuBar", "file", "settings"), e -> {
							System.out.println("Einstellungsdialog öffnen");
						}),
						new MenuItem(Translator.translate("menuBar", "file", "exit"), e -> {
							System.exit(0);
						})),
				new Menu(Translator.translate("menuBar", "edit", "name"),
						new MenuItem(Translator.translate("menuBar", "edit", "undo"), e -> {
							System.out.println("Aktion rückgängig machen");
						}),
						new MenuItem(Translator.translate("menuBar", "edit", "redo"), e -> {
							System.out.println("Aktion wiederholen");
						}),
						new MenuItem(Translator.translate("menuBar", "edit", "cut"), e -> {
							System.out.println("Element ausschneiden");
						}),
						new MenuItem(Translator.translate("menuBar", "edit", "copy"), e -> {
							System.out.println("Element kopieren");
						}),
						new MenuItem(Translator.translate("menuBar", "edit", "paste"), e -> {
							System.out.println("Element einfügen");
						}),
						new MenuItem(Translator.translate("menuBar", "edit", "delete"), e -> {
							System.out.println("Element löschen");
						}),
						new MenuItem(Translator.translate("menuBar", "edit", "search"), e -> {
							System.out.println("Suchdialog öffnen");
						})),
				new Menu(Translator.translate("menuBar", "view", "name"),
						new Menu(Translator.translate("menuBar", "view", "switch to", "name"),
								new MenuItem(Translator.translate("menuBar", "view", "switch to", "day view"), e -> {
									System.out.println("Zur Tages Ansicht wechseln");
								}),
								new MenuItem(Translator.translate("menuBar", "view", "switch to", "week view"), e -> {
									System.out.println("Zur Wochen Ansicht wechseln");
								}),
								new MenuItem(Translator.translate("menuBar", "view", "switch to", "month view"), e -> {
									System.out.println("Zur Monats Ansicht wechseln");
								}))),
				new Menu(Translator.translate("menuBar", "help", "name"),
						new MenuItem(Translator.translate("menuBar", "help", "manual"), e -> {
							System.out.println("Handbuch öffnen");
						}),
						new MenuItem(Translator.translate("menuBar", "help", "about"), e -> {
							System.out.println("Über Personal Organizer");
						})));
	}
}
