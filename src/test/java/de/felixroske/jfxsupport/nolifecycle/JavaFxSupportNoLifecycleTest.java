package de.felixroske.jfxsupport.nolifecycle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import de.felixroske.jfxsupport.JavaFxApplication;
import de.felixroske.jfxsupport.util.BaseJavaFxSupportTest;
import de.felixroske.jfxsupport.util.FxSpringBootApplication;
import javafx.application.Application;

/**
 * Created by Krystian Kałużny on 06.07.2017.
 */
public class JavaFxSupportNoLifecycleTest extends BaseJavaFxSupportTest {

	@Test
	public void noAbstractJavaFxApplicationSupportTest() throws Throwable {
		//given
		javaFxApplicationContainer.launch(NoAbstractJavaFxApplicationSupportTestApp.class);

		//when
		waiter.await(TIMEOUT);

		javaFxApplicationContainer.close();

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
		javaFxApplicationContainer.launch(JavaFxApplicationInjectionIntoNoLifecycleTestApp.class);

		//when
		waiter.await(TIMEOUT);

		javaFxApplicationContainer.close();

		//then
		waiter.await(TIMEOUT);
	}

	/**
	 * Created by Krystian Kałużny on 03.07.2017.
	 * Part of {@link #javaFxApplicationInjectionIntoNoLifecycleAppTest()}
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

	@Test
	public void constructorInjectionIntoNoLifecycleAppTest() throws Throwable {
		//given
		javaFxApplicationContainer.launch(ConstructorInjectionIntoNoLifecycleTestApp.class);

		//when
		waiter.await(TIMEOUT);

		javaFxApplicationContainer.close();

		//then
		waiter.await(TIMEOUT);
	}

	/**
	 * Created by Krystian Kałużny on 03.07.2017.
	 * Part of {@link #constructorInjectionIntoNoLifecycleAppTest()}
	 */

	@FxSpringBootApplication
	static class ConstructorInjectionIntoNoLifecycleTestApp {

		private final JavaFxApplication javaFxApplication;

		@Autowired
		ConstructorInjectionIntoNoLifecycleTestApp(JavaFxApplication javaFxApplication) {
			this.javaFxApplication = javaFxApplication;
		}

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

	@Test
	public void injectAsApplicationTest() throws Throwable {
		//given
		javaFxApplicationContainer.launch(InjectAsApplicationTestApp.class);

		//when
		waiter.await(TIMEOUT);

		javaFxApplicationContainer.close();

		//then
		waiter.await(TIMEOUT);
	}

	/**
	 * Created by Krystian Kałużny on 03.07.2017.
	 * Part of {@link #injectAsApplicationTest()}
	 */

	@FxSpringBootApplication
	static class InjectAsApplicationTestApp {

		private final Application application;
		private final JavaFxApplication javaFxApplication;

		@Autowired
		InjectAsApplicationTestApp(Application application, JavaFxApplication javaFxApplication) {
			this.application = application;
			this.javaFxApplication = javaFxApplication;
		}

		@PostConstruct
		private void constructed() {
			waiter.assertNotNull(application);
			waiter.assertNotNull(javaFxApplication);
			waiter.assertEquals(application, javaFxApplication);
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
