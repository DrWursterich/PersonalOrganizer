package windows;

import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class Window {
	protected final Stage stage = new Stage();

	protected Window() {
	}

	protected final void initModality() {
		this.stage.initModality(Modality.APPLICATION_MODAL);
	}

	public abstract void show();
}
