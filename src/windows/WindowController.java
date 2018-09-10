package windows;

import java.util.HashMap;
import java.util.logging.Level;
import javafx.stage.Stage;
import logging.LoggingController;

public class WindowController {
	private static HashMap<Class<? extends Window>, Window> windows = new HashMap<>();

	public static void registerWindow(Class<? extends Window> windowClass) {
		LoggingController.log(Level.FINE, "registered " + windowClass.getName());
		WindowController.windows.put(windowClass, null);
	}

	public static void showWindow(Class<? extends Window> windowClass, Stage parentStage) {
		try {
			Window window = WindowController.windows.get(windowClass);
			if (window == null) {
				window = windowClass.newInstance();
				window.initModality();
				WindowController.windows.put(windowClass, window);
			}
			window.show();
		} catch (Exception e) {
			LoggingController.log(Level.SEVERE,
					"Unable to display " + windowClass.getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}
}
