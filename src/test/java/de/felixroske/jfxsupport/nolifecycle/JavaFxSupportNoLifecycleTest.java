package de.felixroske.jfxsupport.nolifecycle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import de.felixroske.jfxsupport.JavaFxApplication;
import de.felixroske.jfxsupport.util.BaseJavaFxSupportTest;
import de.felixroske.jfxsupport.util.FxSpringBootApplication;
import de.felixroske.jfxsupport.util.OnFxInitialize;
import javafx.application.Application;

/**
 * Created by Krystian Kałużny on 06.07.2017.
 */
public class JavaFxSupportNoLifecycleTest extends BaseJavaFxSupportTest {

	@Test
	public void noAbstractJavaFxApplicationSupportTest() throws Throwable {
		//given/when
		javaFxApplicationContainer.launch(NoAbstractJavaFxApplicationSupportTestApp.class);

		//then
		waiter.await(TIMEOUT, 3);
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
		//given/when
		javaFxApplicationContainer.launch(JavaFxApplicationInjectionIntoNoLifecycleTestApp.class);

		//then
		waiter.await(TIMEOUT, 3);
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
		//given/when
		javaFxApplicationContainer.launch(ConstructorInjectionIntoNoLifecycleTestApp.class);

		//then
		waiter.await(TIMEOUT, 3);
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
		//given/when
		javaFxApplicationContainer.launch(InjectAsApplicationTestApp.class);

		//then
		waiter.await(TIMEOUT, 3);
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

	@Component
	static class CloseOnInitialize implements OnFxInitialize {

		@Override
		public void onInitialize() {
			try {
				waiter.resume();
				javaFxApplicationContainer.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

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
