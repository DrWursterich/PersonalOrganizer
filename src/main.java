import MenuBar.*;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

@SuppressWarnings("restriction")
public class main extends Application {
	public static void main(String...args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Scene scene = new Scene(new VBox(), 300, 300);
		((VBox)scene.getRoot()).getChildren().add(this.menuBar());
		
		stage.setTitle("Personal Organizer");
		stage.setScene(scene);
		stage.show();
	}
	
	private MenuBar menuBar() {
		return new MenuBar(
				new Menu("Datei",
						new Menu("Neu", 
								new MenuItem("Termin", e -> {
									System.out.println("Neuen Termin anlegen");
								}),
								new MenuItem("Termin Gruppe", e -> {
									System.out.println("Neue Termin Gruppe anlegen");
								})),
						new MenuItem("Öffnen", e -> {
							System.out.println("Datei öffnen");
						}),
						new MenuItem("Speichern", e -> {
							System.out.println("Datei speichern");
						}),
						new MenuItem("Speichern unter", e -> {
							System.out.println("Datei speichern unter");
						}),
						new MenuItem("Einstellungen", e -> {
							System.out.println("Einstellungsdialog öffnen");
						}),
						new MenuItem("Beenden", e -> {
							System.exit(0);
						})),
				new Menu("Bearbeiten",
						new MenuItem("Rückgängig", e -> {
							System.out.println("Aktion rückgängig machen");
						}),
						new MenuItem("Wiederholen", e -> {
							System.out.println("Aktion wiederholen");
						}),
						new MenuItem("Ausschneiden", e -> {
							System.out.println("Element ausschneiden");
						}),
						new MenuItem("Kopieren", e -> {
							System.out.println("Element kopieren");
						}),
						new MenuItem("Einfügen", e -> {
							System.out.println("Element einfügen");
						}),
						new MenuItem("Löschen", e -> {
							System.out.println("Element löschen");
						}),
						new MenuItem("Suchen", e -> {
							System.out.println("Suchdialog öffnen");
						})),
				new Menu("Ansicht",
						new Menu("Wechsen zu", 
								new MenuItem("Tages Ansicht", e -> {
									System.out.println("Zur Tages Ansicht wechseln");
								}),
								new MenuItem("Wochen Ansicht", e -> {
									System.out.println("Zur Wochen Ansicht wechseln");
								}), 
								new MenuItem("Monats Ansicht", e -> {
									System.out.println("Zur Monats Ansicht wechseln");
								})),
						new MenuItem("Öffnen", e -> {
							System.out.println("Datei öffnen");
						}),
						new MenuItem("Speichern unter", e -> {
							System.out.println("Datei speichern unter");
						})),
				new Menu("Hilfe",
						new MenuItem("Handbuch ", e -> {
							System.out.println("Handbuch öffnen");
						}),
						new MenuItem("Über Personal Organizer", e -> {
							System.out.println("Über Personal Organizer");
						})));
	}
}