package de.felixroske.jfxsupport.view.change;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.JavaFxSupport;
import de.felixroske.jfxsupport.lifecycle.BaseFxSupportLifecycleApp;
import de.felixroske.jfxsupport.util.BaseJavaFxSupportTest;
import de.felixroske.jfxsupport.util.FxSpringBootApplication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Created by Krystian Kałużny on 09.07.2017.
 */
public class ChangeViewTest extends BaseJavaFxSupportTest {

	@Test
	public void changeViewOnLifecycleAppTest() throws Exception {

		javaFxApplicationContainer.launch(ChangeViewInLifecycleTestApp.class, FirstView.class);
		waiter.await(TIMEOUT, 2);
	}

	/**
	 * Created by Krystian Kałużny on 09.07.2017.
	 * Part of {@link #changeViewOnLifecycleAppTest()}
	 */
	@FxSpringBootApplication
	static class ChangeViewInLifecycleTestApp extends BaseFxSupportLifecycleApp {

		@Override
		protected void onStart(Stage stage) {
			Button theFirstButton = find("#theFirstButton");
			Button theSecondButton = find("#theSecondButton");

			waiter.assertNotNull(theFirstButton);
			waiter.assertNull(theSecondButton);

			showView(SecondView.class);

			theFirstButton = find("#theFirstButton");
			theSecondButton = find("#theSecondButton");

			waiter.assertNull(theFirstButton);
			waiter.assertNotNull(theSecondButton);

			started(stage);
			waiter.resume();
		}

		@Override
		protected void onClose() {
			closed();
			waiter.resume();
		}
	}

	@Test
	public void changeViewOnNoLifecycleAppTest() throws Throwable {
		//given
		javaFxApplicationContainer.launch(ChangeViewInNoLifecycleTestApp.class, ControlledView.class);

		//when
		waiter.await(TIMEOUT);

		javaFxApplicationContainer.close();

		//then
		waiter.await(TIMEOUT);
	}

	/**
	 * Created by Krystian Kałużny on 09.07.2017.
	 * Part of {@link #changeViewOnNoLifecycleAppTest()}
	 */
	@FXMLController
	public static class ChangeViewController {

		@FXML
		public void initialize() {
			Platform.runLater(this::setSecondView);
		}

		private void setSecondView() {

			Button theFirstButton = find("#theFirstButton");
			Button theSecondButton = find("#theSecondButton");

			waiter.assertNotNull(theFirstButton);
			waiter.assertNull(theSecondButton);

			JavaFxSupport.showView(SecondView.class);

			theFirstButton = find("#theFirstButton");
			theSecondButton = find("#theSecondButton");

			waiter.assertNull(theFirstButton);
			waiter.assertNotNull(theSecondButton);

			waiter.resume();
		}
	}

	/**
	 * Created by Krystian Kałużny on 09.07.2017.
	 * Part of {@link #changeViewOnNoLifecycleAppTest()}
	 */
	@FxSpringBootApplication
	static class ChangeViewInNoLifecycleTestApp {

		@Autowired ChangeViewController changeViewController;

		private boolean constructed = false;

		@PostConstruct
		private void constructed() {
			waiter.assertNotNull(changeViewController);
			constructed = true;
		}

		@PreDestroy
		private void destroy() {
			waiter.assertTrue(constructed);
			waiter.resume();
		}
	}

	//////////////////////////////////
	/////// TESTS CONFIGURATION //////
	//////////////////////////////////

	private static <T extends Node> T find(String query) {
		return javaFxApplicationContainer.getApplicationTestEnvironment().lookup(query).query();
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		BaseFxSupportLifecycleApp.launcher = javaFxApplicationContainer;
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		BaseFxSupportLifecycleApp.launcher = null;
	}
}
