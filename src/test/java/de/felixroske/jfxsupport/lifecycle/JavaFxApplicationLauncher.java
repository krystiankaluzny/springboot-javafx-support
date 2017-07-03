package de.felixroske.jfxsupport.lifecycle;

import org.testfx.framework.junit.ApplicationTest;

import de.felixroske.jfxsupport.JavaFxApplication;
import javafx.stage.Stage;

/**
 * Created by Krystian Kałużny on 03.07.2017.
 */
class JavaFxApplicationLauncher {

	private RunAdapter runAdapter = new RunAdapter();
	private JavaFxApplication javaFxApplication;

	void launch() throws Exception {
		javaFxApplication = new JavaFxApplication();
		runAdapter.internalBefore();
	}

	void close() throws Exception {
		runAdapter.internalAfter();
	}

	class RunAdapter extends ApplicationTest {
		@Override
		public void init() throws Exception {
			javaFxApplication.init();
		}

		@Override
		public void start(Stage stage) throws Exception {
			javaFxApplication.start(stage);
		}

		@Override
		public void stop() throws Exception {
			javaFxApplication.stop();
		}
	}
}
