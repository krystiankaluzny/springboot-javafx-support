package de.felixroske.jfxsupport.lifecycle;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.stage.Stage;

/**
 * Created by Krystian Kałużny on 03.07.2017.
 */
class BaseFxSupportLifecycleApp extends AbstractJavaFxApplicationSupport {
	boolean inited = false;
	boolean started = false;
	boolean stopped = false;

	@Override
	protected void init() {
		inited = true;
	}

	@Override
	protected void start(Stage stage) {
		stage.hide();
		started = true;
	}

	@Override
	protected void stop() {
		stopped = true;
	}
}
