package de.felixroske.jfxsupport.view.change;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import de.felixroske.jfxsupport.lifecycle.BaseFxSupportLifecycleApp;
import de.felixroske.jfxsupport.util.BaseJavaFxSupportTest;
import de.felixroske.jfxsupport.util.FxSpringBootApplication;
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
