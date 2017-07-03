package de.felixroske.jfxsupport.lifecycle;

import net.jodah.concurrentunit.Waiter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.JavaFxSupport;
import de.felixroske.jfxsupport.SplashScreen;
import javafx.stage.Stage;

/**
 * Created by Krystian Kałużny on 03.07.2017.
 */
public class JavaFxSupportLifecycleTest {

	@Test
	public void noErrorLifecycleTest() throws Throwable {
		//given/when
		asyncStartApp(NoErrorLifecycleTestApp.class);

		//then
		waiter.await(TIMEOUT, 4);
	}

	/**
	 * Created by Krystian Kałużny on 03.07.2017.
	 * Part of {@link #noErrorLifecycleTest()}
	 */
	@SpringBootApplication
	static class NoErrorLifecycleTestApp extends BaseFxSupportLifecycleApp {

		private boolean constructed = false;

		@PostConstruct
		private void constructed() {
			constructed = true;
			waiter.resume();
		}

		@Override
		protected void init() {
			super.init();
			waiter.assertTrue(constructed);
			waiter.resume();
		}

		@Override
		protected void start(Stage stage) {
			super.start(stage);
			waiter.assertTrue(inited);
			waiter.resume();
		}

		@Override
		protected void stop() {
			super.stop();
			waiter.assertTrue(started);
			waiter.resume();
		}
	}

	//////////////////////////////////
	/////// TESTS CONFIGURATION //////
	//////////////////////////////////

	private static Waiter waiter;
	private static long TIMEOUT = 5000;

	private void asyncStartApp(Class<? extends AbstractJavaFxApplicationSupport> clazz) {
		CompletableFuture.runAsync(() -> {
			JavaFxSupport.getStartConfiguration()
					.setWebEnvironment(false)
					.setSplashScreen(new SplashScreen() {
						@Override
						public boolean visible() {
							return false;
						}
					});

			//launchApp is blocking so it have to be call in different thread
			JavaFxSupport.launchApp(clazz, LifecyclePlainView.class, new String[] {});
		});
	}
	@Before
	public void setUp() throws Exception {
		if (waiter == null) {
			waiter = new Waiter();
		}
	}

	@After
	public void tearDown() throws Exception {
		waiter = null;
	}
}