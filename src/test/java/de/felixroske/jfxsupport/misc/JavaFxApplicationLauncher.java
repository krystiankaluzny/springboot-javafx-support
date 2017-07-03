package de.felixroske.jfxsupport.misc;

import org.testfx.framework.junit.ApplicationTest;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.JavaFxApplication;
import de.felixroske.jfxsupport.JavaFxSupport;
import de.felixroske.jfxsupport.SplashScreen;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Created by Krystian Kałużny on 03.07.2017.
 */
public class JavaFxApplicationLauncher {

	private RunAdapter runAdapter = new RunAdapter();
	private JavaFxApplication javaFxApplication;

	public void launch(Class<? extends AbstractJavaFxApplicationSupport> clazz) throws Exception {
		launch(clazz, FxPlainView.class);
	}

	public void launch(Class<? extends AbstractJavaFxApplicationSupport> clazz, Class<? extends AbstractFxmlView> viewClazz) throws Exception {
		InactiveSpringBootAppExcludeFilter.activeSpringBootClass = clazz;
		JavaFxSupport.getStartConfiguration()
				.setStartClass(clazz)
				.setStartView(viewClazz)
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

	public void close() throws Exception {
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
