package de.felixroske.jfxsupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.SystemTray;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The Class AbstractJavaFxApplicationSupport.
 *
 * @author Felix Roske
 */
public abstract class AbstractJavaFxApplicationSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJavaFxApplicationSupport.class);

	@Autowired JavaFxApplication javaFxApplication;

	public Application getApplication() {
		return javaFxApplication;
	}

	public Stage getStage() {
		return javaFxApplication.getStage();
	}

	public Scene getScene() {
		return javaFxApplication.getScene();
	}

	public HostServices getHostServices() {
		return javaFxApplication.getHostServices();
	}

	public String getTitle() {
		Stage stage = getStage();
		if (stage != null) {
			return stage.getTitle();
		}
		return null;
	}

	/**
	 * Sets the title. Allows to overwrite values applied during construction at
	 * a later time.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		Stage stage = getStage();
		if (stage != null) {
			stage.setTitle(title);
		}
	}

	public SystemTray getSystemTray() {
		return javaFxApplication.getSystemTray();
	}

	public void showMainViewOrError(Class<? extends AbstractFxmlView> newMainViewClass) {
		javaFxApplication.showMainViewOrError(newMainViewClass);
	}

	public void showMainView(Class<? extends AbstractFxmlView> newMainViewClass) {
		javaFxApplication.showMainView(newMainViewClass);
	}

	public void showViewOrError(Class<? extends AbstractFxmlView> newViewClass, Modality modality) {
		javaFxApplication.showViewOrError(newViewClass, modality);
	}

	public void showView(Class<? extends AbstractFxmlView> newViewClass, Modality modality) {
		javaFxApplication.showView(newViewClass, modality);
	}

	/**
	 * Call after Java Fx init and creation spring application context.
	 * Called on worker thread.
	 */
	protected void onInit() {
	}

	/**
	 * Gets called after full initialization of Spring application context
	 * and JavaFX platform right before the initial view is shown.
	 * Override this method as a hook to add special code for your app. Especially meant to
	 * add AWT code to add a system tray icon and behavior by calling
	 * GUIState.getSystemTray() and modifying it accordingly.
	 * Called on FX thread.
	 */
	protected void onInitialView() {
	}

	/**
	 * Call after Java Fx start and full fx support initialization.
	 * Called on FX thread.
	 *
	 * @param stage main app stage instance
	 */
	protected void onStart(Stage stage) {
	}

	/**
	 * Call in Java Fx stop. After that spring application context is close.
	 *
	 * @see Application#stop()
	 */
	protected void onStop() {
	}

	/**
	 * Call in Java Fx stop after spring application context close.
	 *
	 * @see Application#stop()
	 */
	protected void onClose() {

	}
}
