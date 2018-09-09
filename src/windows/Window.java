package windows;

import javafx.stage.Stage;

public abstract class Window {
	protected final Stage stage = new Stage();

	protected Window() {
	}

	protected final void initOwner(Stage parentStage) {
		this.stage.initOwner(parentStage);
	}

	public abstract void show();
}
