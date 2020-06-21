import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import java.util.GregorianCalendar;
import util.Translator;
import menus.*;
import views.*;
import windows.CreateCategoryWindow;
import windows.CreatePriorityWindow;
import windows.ManageCategoriesWindow;
import windows.ManagePrioritiesWindow;
import windows.NewAppointmentWindow;
import windows.NodeInitializer;
import windows.SettingsWindow;
import windows.WindowController;

/**
 * Static Main Class for the Personal Organizer.
 * @author Mario Schäper
 */
public class PersonalOrganizer extends Application implements NodeInitializer {
	private View view = new DayView(new GregorianCalendar());
	private VBox root = this.vBox(this.menuBar(), this.view);
	private Stage stage;

	public static void main(String...args) throws Exception {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;

		VBox.setVgrow(this.view, Priority.ALWAYS);
		this.view.update();

		this.stage.titleProperty().bind(Translator.translationProperty("title"));
		this.stage.setScene(this.scene(
				this.root,
				0.75*Screen.getPrimary().getBounds().getWidth(),
				0.60*Screen.getPrimary().getBounds().getHeight()));
		this.stage.show();
	}

	/**
	 * Creates and returns a menuBar
	 * @return the menuBar
	 */
	private MenuBar menuBar() {
		return new MenuBar(
				new Menu("menuBar.file.name",
					new Menu("menuBar.file.new.name",
						new MenuItem("menuBar.file.new.meeting", e -> {
							WindowController.showWindow(NewAppointmentWindow.class, this.stage);
						}, "Ctrl+N"),
						new MenuItem("menuBar.file.new.category", e -> {
							WindowController.showWindow(CreateCategoryWindow.class, this.stage);
						}, "Ctrl+Shift+K"),
						new MenuItem("menuBar.file.new.priority", e -> {
							WindowController.showWindow(CreatePriorityWindow.class, this.stage);
						}, "Ctrl+Shift+P")),
					new Menu("menuBar.file.manage.name",
						new MenuItem("menuBar.file.manage.categories", e -> {
							WindowController.showWindow(ManageCategoriesWindow.class, this.stage);
						}, "Ctrl+Alt+K"),
						new MenuItem("menuBar.file.manage.priorities", e -> {
							WindowController.showWindow(ManagePrioritiesWindow.class, this.stage);
						}, "Ctrl+Alt+P")),
					new MenuItem("menuBar.file.open", e -> {
						System.out.println("Datei öffnen");
					}, "Ctrl+O"),
					new MenuItem("menuBar.file.save", e -> {
						System.out.println("Datei speichern");
					}, "Ctrl+S"),
					new MenuItem("menuBar.file.save as", e -> {
						System.out.println("Datei speichern unter");
					}, "Ctrl+Shift+S"),
					new MenuItem("menuBar.file.settings", e -> {
						WindowController.showWindow(SettingsWindow.class, this.stage);
					}, "Ctrl+E"),
					new MenuItem("menuBar.file.exit", e -> {
						System.exit(0);
					}, "Ctrl+Shift+Q")),
				new Menu("menuBar.edit.name",
					new MenuItem("menuBar.edit.undo", e -> {
						System.out.println("Aktion rückgängig machen");
					}, "Ctrl+Z"),
					new MenuItem("menuBar.edit.redo", e -> {
						System.out.println("Aktion wiederholen");
					}, "Ctrl+Y"),
					new MenuItem("menuBar.edit.cut", e -> {
						System.out.println("Element ausschneiden");
					}, "Ctrl+X"),
					new MenuItem("menuBar.edit.copy", e -> {
						System.out.println("Element kopieren");
					}, "Ctrl+C"),
					new MenuItem("menuBar.edit.paste", e -> {
						System.out.println("Element einfügen");
					}, "Ctrl+V"),
					new MenuItem("menuBar.edit.delete", e -> {
						System.out.println("Element löschen");
					}, "Ctrl+D"),
					new MenuItem("menuBar.edit.search", e -> {
						System.out.println("Suchdialog öffnen");
					}, "Ctrl+F")),
				new Menu("menuBar.view.name",
					new Menu("menuBar.view.switch to.name",
						new MenuItem("menuBar.view.switch to.day view", e -> {
							System.out.println("Zur Tages Ansicht wechseln");
						}),
						new MenuItem("menuBar.view.switch to.week view", e -> {
							System.out.println("Zur Wochen Ansicht wechseln");
						}),
						new MenuItem("menuBar.view.switch to.month view", e ->{
							System.out.println("Zur Monats Ansicht wechseln");
						}))),
				new Menu("menuBar.help.name",
					new MenuItem("menuBar.help.manual", e -> {
						System.out.println("Handbuch öffnen");
					}, "F1"),
					new MenuItem("menuBar.help.about", e -> {
						System.out.println("Über Personal Organizer");
					}, "F2")));
	}
}
