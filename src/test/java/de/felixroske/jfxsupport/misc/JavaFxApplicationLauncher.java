package de.felixroske.jfxsupport.misc;

import org.testfx.framework.junit.ApplicationTest;

import de.felixroske.jfxsupport.AbstractFxmlView;
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
	private boolean autoClose = false;
	private boolean closed = false;

	public void launch(Class<?> clazz) throws Exception {
		launch(clazz, FxPlainView.class);
	}

	public void launch(Class<?> clazz, Class<? extends AbstractFxmlView> viewClazz) throws Exception {
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
		if(closed) return;

		closed = true;
		Platform.runLater(() -> {
			try {
				runAdapter.internalAfter();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}

	public void setAutoClose(boolean autoClose) {
		this.autoClose = autoClose;
	}

	class RunAdapter extends ApplicationTest {
		@Override
		public void init() throws Exception {
			javaFxApplication.init();
		}

		@Override
		public void start(Stage stage) throws Exception {
			javaFxApplication.start(stage);
			if(autoClose) {
				close();
			}
		}

		@Override
		public void stop() throws Exception {
			javaFxApplication.stop();
		}
	}
}
