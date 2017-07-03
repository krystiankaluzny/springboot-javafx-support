package de.felixroske.jfxsupport.lifecycle;


import net.jodah.concurrentunit.Waiter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

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

	@LifecycleSpringBootApplication
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

	@Test
	public void initErrorTest() throws Throwable {
		//given/when
		asyncStartApp(InitErrorTestApp.class);

		//then
		waiter.await(TIMEOUT, 3);
	}

	/**
	 * Created by Krystian Kałużny on 03.07.2017.
	 * Part of {@link #initErrorTest()}
	 */
	@LifecycleSpringBootApplication
	static class InitErrorTestApp extends BaseFxSupportLifecycleApp {

		@Override
		protected void init() {
			waiter.resume();
			throw new RuntimeException("Init error");
		}

		@Override
		protected void start(Stage stage) {
			super.start(stage);
			waiter.assertFalse(inited);
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
	private JavaFxApplicationLauncher javaFxApplicationLauncher;
	private static long TIMEOUT = 50000;
	private static final AtomicBoolean lock = new AtomicBoolean(false);

	private void asyncStartApp(Class<? extends AbstractJavaFxApplicationSupport> clazz) {
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

		try {
			javaFxApplicationLauncher.launch(); //java fx init and launch
		} catch (Exception e) {
			waiter.fail(e);
		}
	}

	@Before
	public void setUp() throws Exception {
		waiter = new Waiter();
		javaFxApplicationLauncher = new JavaFxApplicationLauncher();
		BaseFxSupportLifecycleApp.launcher = javaFxApplicationLauncher;
	}

	@After
	public void tearDown() throws Exception {
		waiter = null;
		javaFxApplicationLauncher = null;
	}
}