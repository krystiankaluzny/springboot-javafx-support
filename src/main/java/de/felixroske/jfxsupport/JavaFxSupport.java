package de.felixroske.jfxsupport;

import java.awt.SystemTray;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by Krystian Kałużny on 24.06.2017.
 */
public enum JavaFxSupport {
	INSATNCE;

	static JavaFxApplication application = null;

	private static <U> U getFromAppOrNull(Function<? super JavaFxApplication, ? extends U> mapper) {
		return getOrNull(application, mapper);
	}

	/**
	 * Get value from source or map source to other type if source exist or return null;
	 *
	 * @param source
	 * @param mapper
	 * @return
	 */
	private static <T, U> U getOrNull(T source, Function<? super T, ? extends U> mapper) {
		if (source != null) {
			return mapper.apply(source);
		}
		return null;
	}

	/**
	 * If source is not null then consume it with given value (eg use a source setter to set value)
	 *
	 * @param source
	 * @param value
	 * @param biConsumer
	 */
	private static <T, U> void setIfNotNull(T source, U value, BiConsumer<T, U> biConsumer) {
		if (source != null) {
			biConsumer.accept(source, value);
		}
	}

	public static Stage getStage() {
		return getFromAppOrNull(JavaFxApplication::getStage);
	}

	public static Scene getScene() {
		return getFromAppOrNull(JavaFxApplication::getScene);
	}

	public static HostServices getAppHostServices() {
		return getFromAppOrNull(JavaFxApplication::getHostServices);
	}

	public static String getTitle() {
		return getOrNull(getStage(), Stage::getTitle);
	}

	/**
	 * Sets the title. Allows to overwrite values applied during construction at
	 * a later time.
	 *
	 * @param title the new title
	 */
	public static void setTitle(String title) {
		setIfNotNull(getStage(), title, Stage::setTitle);
	}

	public static SystemTray getSystemTray() {
		return getFromAppOrNull(JavaFxApplication::getSystemTray);
	}

	public static void showMainViewOrError(Class<? extends AbstractFxmlView> newMainViewClass) {
		application.showMainViewOrError(newMainViewClass);
	}

	public static void showMainView(Class<? extends AbstractFxmlView> newMainViewClass) {
		application.showMainView(newMainViewClass);
	}

	public static void showViewOrError(Class<? extends AbstractFxmlView> newViewClass, Modality modality) {
		application.showViewOrError(newViewClass, modality);
	}

	public static void showView(Class<? extends AbstractFxmlView> newViewClass, Modality modality) {
		application.showView(newViewClass, modality);
	}


	public static StartConfiguration getStartConfiguration() {
		return JavaFxApplication.startConfiguration;
	}

	/**
	 * Launch app. This method is blocked as long as Java FX thread is working.
	 * If you want to setup more settings use StartConfiguration before launchApp.
	 *
	 * @param appClass the app class
	 * @param view     the view
	 * @param args     the args
	 * @see StartConfiguration
	 * @see JavaFxSupport#getStartConfiguration()
	 */
	public static void launchApp(final Class<?> appClass,
	                             final Class<? extends AbstractFxmlView> view, final String[] args) {
		getStartConfiguration()
				.setStartClass(appClass)
				.setStartView(view)
				.setStartArgs(args);

		Application.launch(JavaFxApplication.class, args);
	}
}
