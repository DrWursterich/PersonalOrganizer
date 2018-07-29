package menus;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCombination;
import util.Translator;

/**
 * Wrapper Class for {@link javafx.scene.control.MenuItem MenuItem}
 * allowing for invocation with the onAction Event.
 * @author Mario Schäper
 */
public class RadioMenuItem extends javafx.scene.control.RadioMenuItem {
	public RadioMenuItem(String name, EventHandler<ActionEvent> event) {
		this.textProperty().bind(Translator.translationProperty(name));
		this.setOnAction(event);
	}

	public RadioMenuItem(String name, EventHandler<ActionEvent> event, String accelerator) {
		this(name, event);
		this.setAccelerator(KeyCombination.keyCombination(accelerator));
	}
}
