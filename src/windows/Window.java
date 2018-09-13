package windows;

import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class Window implements NodeInitializer {
	protected Stage stage;
	protected Region root;
	protected Scene scene;

	protected Window() {
	}

	protected final void initModality() {
		this.stage.initModality(Modality.APPLICATION_MODAL);
	}

	protected void initialize() {};

	public void show() {
		this.initialize();
		this.stage.showAndWait();
	}

	public <T extends Region> T root(T root, int width, int height, String title) {
		this.root = root;
		this.scene = this.scene(root);
		this.stage = this.stage(title, width, height);
		this.stage.setScene(this.scene);
		return root;
	}

	public void rootTranslatable(Region root, int width, int height, String title) {
		this.root = root;
		this.scene = this.scene(root);
		this.stage = this.stageTranslatable(title, width, height);
		this.stage.setScene(this.scene);
	}
}
