package menus;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.beans.property.StringProperty;

/**
 * Wrapper Class for {@link javafx.scene.control.MenuItem MenuItem}
 * allowing for invocation with the onAction Event.
 * @author Mario Schäper
 */
public class MenuItem extends javafx.scene.control.MenuItem {
	public MenuItem(String name, EventHandler<ActionEvent> event) {
		Label label = new Label(name);
		label.setPrefWidth(150);
		label.setWrapText(true);
		label.setTextFill(Color.BLACK);
		this.setGraphic(label);
		this.setOnAction(event);
	}

	public MenuItem(StringProperty name, EventHandler<ActionEvent> event) {
		Label label = new Label();
		label.textProperty().bind(name);
		label.setPrefWidth(150);
		label.setWrapText(true);
		label.setTextFill(Color.BLACK);
		this.setGraphic(label);
		this.setOnAction(event);
	}

	public MenuItem(String name, EventHandler<ActionEvent> event, String accelerator) {
		this(name, event);
		this.setAccelerator(KeyCombination.keyCombination(accelerator));
	}

	public MenuItem(StringProperty name, EventHandler<ActionEvent> event, String accelerator) {
		this(name, event);
		this.setAccelerator(KeyCombination.keyCombination(accelerator));
	}
}
