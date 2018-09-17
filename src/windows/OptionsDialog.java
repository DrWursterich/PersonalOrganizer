package windows;

import java.util.Arrays;

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
	 * Immutable Class, that holds the Data of a executable Option of a Dialog.
	 * @author Mario Schäper
	 */
	public static class Option {
		private final ButtonType buttonType;
		private final Runnable action;

		public Option(ButtonType buttonType, Runnable action) {
			this.buttonType = buttonType;
			this.action = action;
		}

		public ButtonType getButtonType() {
			return this.buttonType;
		}

		public Runnable getAction() {
			return this.action;
		}

		public void execute() {
			this.action.run();
		}
	}

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
	 * Uses {@link #getOption(String, String, ButtonType...) getOption()} to
	 * prompt the User with a {@link javafx.scene.control.Dialog Dialog} containing
	 * the {@link javafx.scene.control.ButtonType ButtonTypes} of the given
	 * {@link Option Options}.<br/>
	 * After the Userer has choosen a Option its {@link Runnable Runnable} will be executed.
	 * @param title the title of the dialog
	 * @param message the message of the dialog
	 * @param options the options the user gets to choose from
	 */
	public static void executeOption(String title, String message, Option...options) {
		ButtonType result = OptionsDialog.getOption(
				title, message, Arrays.stream(options)
						.map(Option::getButtonType)
						.toArray(ButtonType[]::new));
		Arrays.stream(options)
				.filter(e -> e.getButtonType().getButtonData().equals(
						result.getButtonData()))
				.forEach(Option::execute);
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
