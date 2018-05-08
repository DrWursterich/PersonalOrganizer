package menus;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCombination;
import javafx.beans.property.StringProperty;

/**
 * Wrapper Class for {@link javafx.scene.control.CheckMenuItem CheckMenuItem}
 * allowing for invocation with the onAction Event.
 * @author Mario Schäper
 */
public class CheckMenuItem extends javafx.scene.control.CheckMenuItem {
	public CheckMenuItem(String name, EventHandler<ActionEvent> event) {
		this.setText(name);
		this.setOnAction(event);
	}

	public CheckMenuItem(StringProperty name, EventHandler<ActionEvent> event) {
		this.textProperty().bind(name);
		this.setOnAction(event);
	}

	public CheckMenuItem(String name, EventHandler<ActionEvent> event, String accelerator) {
		this(name, event);
		this.setAccelerator(KeyCombination.keyCombination(accelerator));
	}

	public CheckMenuItem(StringProperty name, EventHandler<ActionEvent> event, String accelerator) {
		this(name, event);
		this.setAccelerator(KeyCombination.keyCombination(accelerator));
	}
	public CheckMenuItem(String name, EventHandler<ActionEvent> event, boolean checked) {
		this(name, event);
		this.setSelected(checked);
	}

	public CheckMenuItem(StringProperty name, EventHandler<ActionEvent> event, boolean checked) {
		this(name, event);
		this.setSelected(checked);
	}

	public CheckMenuItem(String name, EventHandler<ActionEvent> event, String accelerator, boolean checked) {
		this(name, event, accelerator);
		this.setSelected(checked);
	}

	public CheckMenuItem(StringProperty name, EventHandler<ActionEvent> event, String accelerator, boolean checked) {
		this(name, event, accelerator);
		this.setSelected(checked);
	}
}
