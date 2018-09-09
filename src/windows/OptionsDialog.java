package windows;

import javafx.beans.property.StringProperty;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import util.Translator;

/**
 * Static Class that offers Comfort-Functions to handle {@link javafx.scene.control.Dialog Dialogs}.
 * @author Mario Schäper
 */
public class OptionsDialog {
	/**
	 * Prompts the User with a {@link javafx.scene.control.Dialog Dialog}, containing the
	 * Title, Message and {@link javafx.scene.control.ButtonType ButtonTypes}.
	 * @param title the title of the dialog
	 * @param message the message of the dialog
	 * @param options the buttons the user gets to choose from
	 * @return the users choice
	 */
	public static ButtonType getOption(String title, String message, ButtonType...options) {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.getDialogPane().setContentText(message);
		dialog.getDialogPane().getButtonTypes().addAll(options);
		dialog.getDialogPane().getButtonTypes().stream().map(dialog.getDialogPane()::lookupButton)
				.forEach(node -> ButtonBar.setButtonUniformSize(node, false));
		dialog.getDialogPane().getScene().getWindow().sizeToScene();
		ButtonType ret = dialog.showAndWait().orElse(null);
		dialog.close();
		return ret;
	}

	/**
	 * Prompts the User with a {@link javafx.scene.control.Dialog Dialog},
	 * containing Title, Message and <em>YES</em>- and <em>NO</em>-Buttons.
	 * @see OptionsDialog#getOption(StringProperty, StringProperty, ButtonType...)
	 * @param title the title of the dialog
	 * @param message the message of the dialog
	 * @return the users choice
	 */
	public static boolean getBoolean(String title, String message) {
		ButtonType yes = new ButtonType(Translator.translate("general.yes"), ButtonData.YES);
		ButtonType no = new ButtonType(Translator.translate("general.no"), ButtonData.NO);
		return yes.getButtonData().equals(
				OptionsDialog.getOption(title, message, yes, no).getButtonData());
	}

	/**
	 * Displays a {@link javafx.scene.control.Dialog Dialog} containing the Title and Message.
	 * @param title the title of the dialog
	 * @param message the message of the dialog
	 */
	public static void showMessage(String title, String message) {
		OptionsDialog.getOption(title, message,
				new ButtonType(Translator.translate("general.ok"), ButtonData.CANCEL_CLOSE));
	}
}
