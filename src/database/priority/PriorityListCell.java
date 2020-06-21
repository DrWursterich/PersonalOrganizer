package database.priority;

import javafx.scene.control.ListCell;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class PriorityListCell extends ListCell<Priority> {
	@Override
	protected void updateItem(Priority item, final boolean empty) {
		super.updateItem(item, empty);
		if (!empty && item != null) {
			if (item.equals(Priority.NONE)) {
				this.setFontStyle(FontWeight.BOLD, FontPosture.ITALIC);
			} else {
				this.setFontStyle(FontWeight.BOLD, FontPosture.REGULAR);
			}
			this.textProperty().bind(item.nameProperty());
			this.installContextMenu();
		} else {
			this.textProperty().unbind();
			this.textProperty().setValue("");
		}
	}

	protected void installContextMenu() {}

	private void setFontStyle(
			final FontWeight fontWeight,
			final FontPosture fontPosture) {
		this.setFont(Font.font(
				this.getFont().getFamily(),
				fontWeight,
				fontPosture,
				this.getFont().getSize()));
	}
}

