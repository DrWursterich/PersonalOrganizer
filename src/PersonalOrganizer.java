import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import java.util.GregorianCalendar;
import database.*;
import menuBar.*;
import views.*;

public class PersonalOrganizer extends Application {
	private static DatabaseController dbController;
	private static Translator translator;
	private static View view;

	public static void main(String...args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		VBox root = new VBox();
		Scene scene = new Scene(root, 300, 300);
		dbController = new DatabaseController();
		dbController.addAppointment("Termin", "Beschreibung",
				new GregorianCalendar(2018, 3, 20,  8, 30),
				new GregorianCalendar(2018, 3, 20, 12, 10));
		translator = new Translator("de");
		view = new DayView(dbController, new GregorianCalendar());
		StackPane a = new StackPane();
		a.getChildren().add(view);
		root.getChildren().addAll(this.menuBar(), a);
		stage.setTitle(translator.translate("title"));
		stage.setScene(scene);
		stage.show();
	}

	private MenuBar menuBar() {
		return new MenuBar(
				new Menu(translator.translate("menuBar", "file", "name"),
						new Menu(translator.translate("menuBar", "file", "new", "name"),
								new MenuItem(translator.translate("menuBar", "file", "new", "meeting"), e -> {
									System.out.println("Neuen Termin anlegen");
								}),
								new MenuItem(translator.translate("menuBar", "file", "new", "meetings"), e -> {
									System.out.println("Neue Termin Gruppe anlegen");
								})),
						new MenuItem(translator.translate("menuBar", "file", "open"), e -> {
							System.out.println("Datei öffnen");
						}),
						new MenuItem(translator.translate("menuBar", "file", "save"), e -> {
							System.out.println("Datei speichern");
						}),
						new MenuItem(translator.translate("menuBar", "file", "save as"), e -> {
							System.out.println("Datei speichern unter");
						}),
						new MenuItem(translator.translate("menuBar", "file", "settings"), e -> {
							System.out.println("Einstellungsdialog öffnen");
						}),
						new MenuItem(translator.translate("menuBar", "file", "exit"), e -> {
							System.exit(0);
						})),
				new Menu(translator.translate("menuBar", "edit", "name"),
						new MenuItem(translator.translate("menuBar", "edit", "undo"), e -> {
							System.out.println("Aktion rückgängig machen");
						}),
						new MenuItem(translator.translate("menuBar", "edit", "redo"), e -> {
							System.out.println("Aktion wiederholen");
						}),
						new MenuItem(translator.translate("menuBar", "edit", "cut"), e -> {
							System.out.println("Element ausschneiden");
						}),
						new MenuItem(translator.translate("menuBar", "edit", "copy"), e -> {
							System.out.println("Element kopieren");
						}),
						new MenuItem(translator.translate("menuBar", "edit", "paste"), e -> {
							System.out.println("Element einfügen");
						}),
						new MenuItem(translator.translate("menuBar", "edit", "delete"), e -> {
							System.out.println("Element löschen");
						}),
						new MenuItem(translator.translate("menuBar", "edit", "search"), e -> {
							System.out.println("Suchdialog öffnen");
						})),
				new Menu(translator.translate("menuBar", "view", "name"),
						new Menu(translator.translate("menuBar", "view", "switch to", "name"),
								new MenuItem(translator.translate("menuBar", "view", "switch to", "day view"), e -> {
									System.out.println("Zur Tages Ansicht wechseln");
								}),
								new MenuItem(translator.translate("menuBar", "view", "switch to", "week view"), e -> {
									System.out.println("Zur Wochen Ansicht wechseln");
								}),
								new MenuItem(translator.translate("menuBar", "view", "switch to", "month view"), e -> {
									System.out.println("Zur Monats Ansicht wechseln");
								}))),
				new Menu(translator.translate("menuBar", "help", "name"),
						new MenuItem(translator.translate("menuBar", "help", "manual"), e -> {
							System.out.println("Handbuch öffnen");
						}),
						new MenuItem(translator.translate("menuBar", "help", "about"), e -> {
							System.out.println("Über Personal Organizer");
						})));
	}
}