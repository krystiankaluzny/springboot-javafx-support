package de.felixroske.jfxsupport.lifecycle;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.util.JavaFxApplicationContainer;
import javafx.stage.Stage;

/**
 * Created by Krystian Kałużny on 03.07.2017.
 */
public abstract class BaseFxSupportLifecycleApp extends AbstractJavaFxApplicationSupport {
	public static JavaFxApplicationContainer launcher;
	boolean inited = false;
	boolean initedView = false;
	boolean started = false;
	boolean stopped = false;
	boolean closed = false;

	protected void exception(String text) {
		throw new RuntimeException("[Testing exception] " + text);
	}

	@Override
	protected void onInit() {
		inited();
	}

	protected void inited() {
		inited = true;
	}

	@Override
	protected void onInitialView() {
		initedView();
	}


	protected void initedView() {
		initedView = true;
	}

	@Override
	protected void onStart(Stage stage) {
		started(stage);
	}

	protected void started(Stage stage) {
		started();
		hideAndClose(stage);
	}

	protected void started() {
		started = true;
	}

	protected void hideAndClose(Stage stage) {
		stage.hide();
		try {
			launcher.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStop() {
		stopped();
	}

	protected void stopped() {
		stopped = true;
	}

	@Override
	protected void onClose() {
		closed();
	}

	protected void closed() {
		closed = true;
	}
}
