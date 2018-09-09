package database.category;

import java.util.logging.Level;
import javafx.scene.control.ListCell;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import logging.LoggingController;

public class CategoryListCell extends ListCell<Category> {
	@Override
	protected void updateItem(Category item, boolean empty) {
		super.updateItem(item, empty);
		if (!empty && item != null) {
			if (item.equals(Category.NONE)) {
				this.setFontStyle(FontWeight.BOLD, FontPosture.ITALIC);
				if (item != Category.NONE) {
					LoggingController.log(Level.WARNING,
							"NONE-Category Item is equals but not the NONE-Category Item");
					item = Category.NONE;
				}
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

	protected void installContextMenu() {

	}

	private void setFontStyle(FontWeight fontWeight, FontPosture fontPosture) {
		this.setFont(Font.font(
				this.getFont().getFamily(),
				fontWeight,
				fontPosture,
				this.getFont().getSize()));
	}
}
