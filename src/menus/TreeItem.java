package menus;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import util.Translator;

public class TreeItem extends javafx.scene.control.TreeItem<String> {
	private Pane content;
	private EventHandler<Event> onApply;

	public TreeItem(String title, Pane content) {
		super();
		this.valueProperty().bind(Translator.translationProperty(title));
		this.setContent(content);
	}

	public TreeItem(String title, Pane content, EventHandler<Event> onApply) {
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
