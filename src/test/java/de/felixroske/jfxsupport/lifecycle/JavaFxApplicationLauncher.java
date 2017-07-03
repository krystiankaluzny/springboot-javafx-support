package de.felixroske.jfxsupport.lifecycle;

import org.testfx.framework.junit.ApplicationTest;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.JavaFxApplication;
import de.felixroske.jfxsupport.JavaFxSupport;
import de.felixroske.jfxsupport.SplashScreen;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Created by Krystian Kałużny on 03.07.2017.
 */
class JavaFxApplicationLauncher {

	private RunAdapter runAdapter = new RunAdapter();
	private JavaFxApplication javaFxApplication;

	void launch(Class<? extends AbstractJavaFxApplicationSupport> clazz) throws Exception {
		InactiveSpringBootAppExcludeFilter.activeSpringBootClass = clazz;
		JavaFxSupport.getStartConfiguration()
				.setStartClass(clazz)
				.setStartView(LifecyclePlainView.class)
				.setStartArgs(new String[0])
				.setWebEnvironment(false)
				.setSplashScreen(new SplashScreen() {
					@Override
					public boolean visible() {
						return false;
					}
				});

		javaFxApplication = new JavaFxApplication();
		runAdapter.internalBefore();
	}

	void close() throws Exception {
		Platform.runLater(() -> {
			try {
				runAdapter.internalAfter();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

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
