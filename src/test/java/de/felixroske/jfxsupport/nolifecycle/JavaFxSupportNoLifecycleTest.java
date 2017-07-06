package de.felixroske.jfxsupport.nolifecycle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import de.felixroske.jfxsupport.JavaFxApplication;
import de.felixroske.jfxsupport.misc.BaseJavaFxSupportTest;
import de.felixroske.jfxsupport.misc.FxSpringBootApplication;

/**
 * Created by Krystian Kałużny on 06.07.2017.
 */
public class JavaFxSupportNoLifecycleTest extends BaseJavaFxSupportTest {

	@Test
	public void noAbstractJavaFxApplicationSupportTest() throws Throwable {
		//given
		javaFxApplicationLauncher.launch(NoAbstractJavaFxApplicationSupportTestApp.class);

		//when
		waiter.await(TIMEOUT);

		javaFxApplicationLauncher.close();

		//then
		waiter.await(TIMEOUT);
	}

	/**
	 * Created by Krystian Kałużny on 03.07.2017.
	 * Part of {@link #noAbstractJavaFxApplicationSupportTest()}
	 */

	@FxSpringBootApplication
	static class NoAbstractJavaFxApplicationSupportTestApp {

		private boolean constructed = false;

		@PostConstruct
		private void constructed() {
			waiter.resume();
			constructed = true;
		}

		@PreDestroy
		private void destroy() {
			waiter.assertTrue(constructed);
			waiter.resume();
		}
	}

	@Test
	public void javaFxApplicationInjectionIntoNoLifecycleAppTest() throws Throwable {
		//given
		javaFxApplicationLauncher.launch(JavaFxApplicationInjectionIntoNoLifecycleTestApp.class);

		//when
		waiter.await(TIMEOUT);

		javaFxApplicationLauncher.close();

		//then
		waiter.await(TIMEOUT);
	}

	/**
	 * Created by Krystian Kałużny on 03.07.2017.
	 * Part of {@link #noAbstractJavaFxApplicationSupportTest()}
	 */

	@FxSpringBootApplication
	static class JavaFxApplicationInjectionIntoNoLifecycleTestApp {

		@Autowired JavaFxApplication javaFxApplication;

		@PostConstruct
		private void constructed() {
			waiter.assertNotNull(javaFxApplication);
			waiter.resume();
		}

		@PreDestroy
		private void destroy() {
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
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
