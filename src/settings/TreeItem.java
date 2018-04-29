package settings;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;

public class TreeItem extends javafx.scene.control.TreeItem<String> {
	private Pane content;
	private EventHandler<Event> onApply;
	
	public TreeItem(SimpleStringProperty title, Pane content) {
		super();
		this.valueProperty().bind(title);
		this.setContent(content);
	}

	public TreeItem(SimpleStringProperty title, Pane content, EventHandler<Event> onApply) {
		this(title, content);
		this.onApply = onApply;
	}
	
	public Pane getContent() {
		return this.content;
	}
	
	public void setContent(Pane content) {
		content.setPadding(new Insets(10, 20, 10, 20));
		this.content = content;
	}
	
	public void apply() {
		if (this.onApply != null) {
			this.onApply.handle(new Event(Event.ANY));
		}
	}
}
