package de.felixroske.jfxsupport.lifecycle;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import de.felixroske.jfxsupport.JavaFxApplication;
import de.felixroske.jfxsupport.util.BaseJavaFxSupportTest;
import de.felixroske.jfxsupport.util.FxSpringBootApplication;
import javafx.stage.Stage;

/**
 * Created by Krystian Kałużny on 03.07.2017.
 */
public class JavaFxSupportLifecycleTest extends BaseJavaFxSupportTest {

	@Test
	public void noErrorLifecycleTest() throws Throwable {
		//given/when
		javaFxApplicationLauncher.launch(NoErrorLifecycleTestApp.class);

		//then
		waiter.await(TIMEOUT, 5);
	}

	/**
	 * Created by Krystian Kałużny on 03.07.2017.
	 * Part of {@link #noErrorLifecycleTest()}
	 */

	@FxSpringBootApplication
	static class NoErrorLifecycleTestApp extends BaseFxSupportLifecycleApp {

		private boolean constructed = false;

		@PostConstruct
		private void constructed() {
			waiter.resume();
			constructed = true;
		}

		@Override
		protected void onInit() {
			waiter.assertTrue(constructed);
			waiter.resume();
			inited();
		}

		@Override
		protected void onStart(Stage stage) {
			waiter.assertTrue(inited);
			waiter.resume();
			started(stage);
		}

		@Override
		protected void onStop() {
			waiter.assertTrue(started);
			waiter.resume();
			stopped();
		}

		@Override
		protected void onClose() {
			System.out.println(toString());
			waiter.assertTrue(stopped);
			closed();

			waiter.resume();
		}
	}

	@Test
	public void initErrorTest() throws Throwable {
		//given/when
		javaFxApplicationLauncher.launch(InitErrorTestApp.class);

		//then
		waiter.await(TIMEOUT, 4);
	}

	/**
	 * Created by Krystian Kałużny on 03.07.2017.
	 * Part of {@link #initErrorTest()}
	 */
	@FxSpringBootApplication
	static class InitErrorTestApp extends BaseFxSupportLifecycleApp {

		@Override
		protected void onInit() {
			waiter.resume();
			exception("Init error");
			inited();
		}

		@Override
		protected void onStart(Stage stage) {
			waiter.assertFalse(inited);
			waiter.resume();
			started(stage);
		}

		@Override
		protected void onStop() {
			waiter.assertTrue(started);
			waiter.resume();
			stopped();
		}

		@Override
		protected void onClose() {
			System.out.println(toString());
			waiter.assertTrue(stopped);
			closed();

			waiter.resume();
		}
	}

	@Test
	public void initAndStartErrorTest() throws Throwable {
		//given/when
		javaFxApplicationLauncher.launch(InitAndStartErrorTestApp.class);

		//then
		waiter.await(TIMEOUT, 4);
	}

	/**
	 * Created by Krystian Kałużny on 03.07.2017.
	 * Part of {@link #initAndStartErrorTest()}
	 */
	@FxSpringBootApplication
	static class InitAndStartErrorTestApp extends InitErrorTestApp {

		@Override
		protected void onStart(Stage stage) {
			waiter.assertFalse(inited);
			waiter.resume();
			closeAfterHide(stage);
			exception("Start error");
			started();
		}

		@Override
		protected void onStop() {
			waiter.assertFalse(started);
			waiter.resume();
			stopped();
		}

		@Override
		protected void onClose() {
			waiter.assertTrue(stopped);
			closed();

			waiter.resume();
		}
	}

	@Test
	public void initStartAndStoppedErrorTest() throws Throwable {
		//given/when
		javaFxApplicationLauncher.launch(InitStartAndStopErrorTestApp.class);

		//then
		waiter.await(TIMEOUT, 4);
	}

	/**
	 * Created by Krystian Kałużny on 03.07.2017.
	 * Part of {@link #initStartAndStoppedErrorTest()}
	 */
	@FxSpringBootApplication
	static class InitStartAndStopErrorTestApp extends InitAndStartErrorTestApp {

		@Override
		protected void onStop() {
			waiter.assertFalse(started);
			waiter.resume();
			exception("Stop error");
			stopped();
		}

		@Override
		protected void onClose() {
			waiter.assertFalse(stopped);
			closed();

			waiter.resume();
		}
	}

	@Test
	public void javaFxApplicationInjectionTest() throws Throwable {
		//given/when
		javaFxApplicationLauncher.launch(JavaFxApplicationInjectionTestApp.class);

		//then
		waiter.await(TIMEOUT, 2);
	}

	/**
	 * Created by Krystian Kałużny on 03.07.2017.
	 * Part of {@link #javaFxApplicationInjectionTest()}
	 */
	@FxSpringBootApplication
	static class JavaFxApplicationInjectionTestApp extends BaseFxSupportLifecycleApp {

		@Autowired JavaFxApplication javaFxApplication;

		@PostConstruct
		private void constructed() {
			waiter.assertNotNull(javaFxApplication);
			waiter.resume();
		}

		@Override
		protected void onClose() {
			closed();
			waiter.assertNotNull(javaFxApplication);
			waiter.resume();
		}
	}

	//////////////////////////////////
	/////// TESTS CONFIGURATION //////
	//////////////////////////////////

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		BaseFxSupportLifecycleApp.launcher = javaFxApplicationLauncher;
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		BaseFxSupportLifecycleApp.launcher = null;
	}
}