package de.felixroske.jfxsupport.lifecycle;


import net.jodah.concurrentunit.Waiter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import de.felixroske.jfxsupport.JavaFxApplication;
import javafx.stage.Stage;

/**
 * Created by Krystian Kałużny on 03.07.2017.
 */
public class JavaFxSupportLifecycleTest {

	@Test
	public void noErrorLifecycleTest() throws Throwable {
		//given/when
		javaFxApplicationLauncher.launch(NoErrorLifecycleTestApp.class);

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
		javaFxApplicationLauncher.launch(InitErrorTestApp.class);

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

	@Test
	public void initAndStartErrorTest() throws Throwable {
		//given/when
		javaFxApplicationLauncher.launch(InitAndStartErrorTestApp.class);

		//then
		waiter.await(TIMEOUT, 3);
	}

	/**
	 * Created by Krystian Kałużny on 03.07.2017.
	 * Part of {@link #initAndStartErrorTest()}
	 */
	@LifecycleSpringBootApplication
	static class InitAndStartErrorTestApp extends BaseFxSupportLifecycleApp {

		@Override
		protected void init() {
			waiter.resume();
			throw new RuntimeException("Init error");
		}

		@Override
		protected void start(Stage stage) {
			super.start(stage);
			started = false;
			waiter.assertFalse(inited);
			waiter.resume();
			throw new RuntimeException("Start error");
		}

		@Override
		protected void stop() {
			super.stop();
			waiter.assertFalse(started);
			waiter.resume();
		}
	}

	@Test
	public void javaFxApplicationInjectionTest() throws Throwable {
		//given/when
		javaFxApplicationLauncher.launch(JavaFxApplicationInjectionTestApp.class);

		//then
		waiter.await(TIMEOUT);
	}

	/**
	 * Created by Krystian Kałużny on 03.07.2017.
	 * Part of {@link #initAndStartErrorTest()}
	 */
	@LifecycleSpringBootApplication
	static class JavaFxApplicationInjectionTestApp extends BaseFxSupportLifecycleApp {

		@Autowired JavaFxApplication javaFxApplication;

		@PostConstruct
		private void constructed() {
			waiter.assertNotNull(javaFxApplication);
			waiter.resume();
		}
	}

	//////////////////////////////////
	/////// TESTS CONFIGURATION //////
	//////////////////////////////////

	private static Waiter waiter; //it have to be static cause classes with @LifecycleSpringBootApplication have to be static either
	private JavaFxApplicationLauncher javaFxApplicationLauncher;
	private static long TIMEOUT = 5000;

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
		BaseFxSupportLifecycleApp.launcher = null;
	}
}