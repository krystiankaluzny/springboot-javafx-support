package de.felixroske.jfxsupport.lifecycle;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.stage.Stage;

/**
 * Created by Krystian Kałużny on 03.07.2017.
 */
abstract class BaseFxSupportLifecycleApp extends AbstractJavaFxApplicationSupport {
	boolean inited = false;
	boolean started = false;
	boolean stopped = false;

	static JavaFxApplicationLauncher launcher;

	@Override
	protected void init() {
		inited = true;
	}

	@Override
	protected void start(Stage stage) {
		stage.hide();
		started = true;

		try {
			launcher.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void stop() {
		stopped = true;
	}
}
