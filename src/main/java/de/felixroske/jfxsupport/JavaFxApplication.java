package de.felixroske.jfxsupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.awt.SystemTray;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by Krystian Kałużny on 24.06.2017.
 * <p>
 * Class have to be public that JavaFx Launcher could create an instance of the class.
 */

public class JavaFxApplication extends Application {

	private static final Logger LOGGER = LoggerFactory.getLogger(JavaFxApplication.class);

	static StartConfiguration startConfiguration = new StartConfiguration();

	private final BooleanProperty appInitFinished = new SimpleBooleanProperty(false);
	private ConfigurableApplicationContext applicationContext;
	private List<Image> icons = new ArrayList<>();
	private Stage stage;
	private Scene scene;
	private SystemTray systemTray;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Optional<AbstractJavaFxApplicationSupport> abstractJavaFxApplicationSupport = Optional.empty();

	/**
	 * (non-Javadoc)
	 *
	 * @see javafx.application.Application#init()
	 */
	@Override
	public void init() throws Exception {
		CompletableFuture.runAsync(this::createApplicationContext)
				.thenRun(this::tryToGetAbstractFxSupport)
				.thenRun(this::tryToCallAbstractFxSupportOnInit)
				.thenRun(this::checkSystemTray)
				.thenRun(this::populateApplicationInstance)
				.thenRun(this::tryToLoadApplicationIcons)
				.thenRun(this::initApplicationFinished)
				.exceptionally(throwable -> {
					handleAppInitError(throwable);
					return null;
				});
	}

	private void createApplicationContext() {
		SpringApplication springApplication = new SpringApplication(startConfiguration.startClass);
		springApplication.setWebEnvironment(startConfiguration.webEnvironment);
		springApplication.addInitializers(new JavaFxApplicationInitializer(this));
		applicationContext = springApplication.run(startConfiguration.startArgs);
	}

	private void tryToGetAbstractFxSupport() {
		if (AbstractJavaFxApplicationSupport.class.isAssignableFrom(startConfiguration.startClass)) {
			String[] beanNames = applicationContext.getBeanNamesForType(AbstractJavaFxApplicationSupport.class);
			int beansCount = beanNames.length;
			if (beansCount == 1) {
				try {
					abstractJavaFxApplicationSupport = Optional.ofNullable(applicationContext.getBean(AbstractJavaFxApplicationSupport.class));
				} catch (Exception e) {
					LOGGER.error("Can't obtain AbstractJavaFxApplicationSupport instance", e);
				}
			} else if (beansCount > 1) {
				LOGGER.error("To many AbstractJavaFxApplicationSupport beans, count: {}, beans: {}", beansCount, beanNames);
			}
		}
	}

	private void tryToCallAbstractFxSupportOnInit() {
		try {
			abstractJavaFxApplicationSupport.ifPresent(AbstractJavaFxApplicationSupport::onInit);
		} catch (Exception e) {
			LOGGER.error("Exception in onInit method", e);
		}
	}

	private void checkSystemTray() {
		if (SystemTray.isSupported()) {
			systemTray = SystemTray.getSystemTray();
		}
	}

	private void populateApplicationInstance() {
		JavaFxSupport.application = this;
	}

	private void tryToLoadApplicationIcons() {
		try {
			final List<String> fsImages = PropertyReaderHelper.get(applicationContext.getEnvironment(), "javafx.appicons");

			if (!fsImages.isEmpty()) {
				fsImages.forEach((s) -> icons.add(new Image(getClass().getResource(s).toExternalForm())));
			} else { // add factory images
				icons.add(new Image(getClass().getResource("/icons/gear_16x16.png").toExternalForm()));
				icons.add(new Image(getClass().getResource("/icons/gear_24x24.png").toExternalForm()));
				icons.add(new Image(getClass().getResource("/icons/gear_36x36.png").toExternalForm()));
				icons.add(new Image(getClass().getResource("/icons/gear_42x42.png").toExternalForm()));
				icons.add(new Image(getClass().getResource("/icons/gear_64x64.png").toExternalForm()));
			}
		} catch (Exception e) {
			LOGGER.error("Failed to load icons: ", e);
		}
	}

	private void initApplicationFinished() {
		appInitFinished.set(true);
	}

	private void handleAppInitError(Throwable throwable) {
		LOGGER.error("Failed to initialize application: ", throwable);
		Platform.runLater(() -> showErrorAlert(throwable));
	}

	/**
	 * Show error alert that close app.
	 *
	 * @param throwable cause of error
	 */
	private void showErrorAlert(Throwable throwable) {
		Alert alert = new Alert(Alert.AlertType.ERROR, "Oops! An unrecoverable error occurred.\n" +
				"Please contact your software vendor.\n\n" +
				"The application will stop now.");
		alert.showAndWait().ifPresent(response -> Platform.exit());
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(final Stage stage) throws Exception {
		this.stage = stage;

		final Stage splashStage = createSplashStage();

		if (appInitFinished.get()) {
			// Spring ContextLoader was faster
			afterAppContextInitAndFxStart(splashStage);
		} else {
			appInitFinished.addListener((ov, oVal, nVal) -> afterAppContextInitAndFxStart(splashStage));
		}
	}

	private Stage createSplashStage() {
		Stage splashStage = new Stage(StageStyle.UNDECORATED);

		if (startConfiguration.splashScreen.visible()) {
			final Scene splashScene = new Scene(startConfiguration.splashScreen.getParent());
			splashStage.setScene(splashScene);
			splashStage.show();
		}

		return splashStage;
	}

	private void afterAppContextInitAndFxStart(Stage splashStage) {
		showInitialAndCloseSplash(splashStage);
		tryToCallAbstractFxSupportOnStart();
	}

	private void showInitialAndCloseSplash(final Stage splashStage) {
		Platform.runLater(() -> {
			showInitialView();
			closeSplashScreen(splashStage);
		});
	}

	/**
	 * Show initial view.
	 */
	private void showInitialView() {
		setStageStyle();
		tryToCallAbstractFxSupportOnInitialView();
		showMainViewOrError(startConfiguration.startView);
	}

	private void tryToCallAbstractFxSupportOnInitialView() {
		try {
			abstractJavaFxApplicationSupport.ifPresent(AbstractJavaFxApplicationSupport::onInitialView);
		} catch (Exception e) {
			LOGGER.error("Exception in onInitialView method", e);
		}
	}

	private void setStageStyle() {
		final String stageStyle = applicationContext.getEnvironment().getProperty("javafx.stage.style");
		final StageStyle style = stageStyle != null ? StageStyle.valueOf(stageStyle.toUpperCase()) : StageStyle.DECORATED;

		if (!stage.isShowing() && !stage.getStyle().equals(style)) {
			stage.initStyle(style);
		}
	}

	private void closeSplashScreen(final Stage splashStage) {
		if (startConfiguration.splashScreen.visible()) {
			splashStage.hide();
			splashStage.setScene(null);
		}
	}

	private void tryToCallAbstractFxSupportOnStart() {
		abstractJavaFxApplicationSupport.ifPresent(fxSupport -> Platform.runLater(() -> {
					try {
						fxSupport.onStart(stage);
					} catch (Exception e) {
						LOGGER.error("Exception in onStart method", e);
					}
				}
		));
	}

	/**
	 * Show main view. If showMainView throws any exception, error dialog will show.
	 *
	 * @param newView the new view
	 * @see #showMainView(Class)
	 */
	void showMainViewOrError(final Class<? extends AbstractFxmlView> newView) {
		try {
			showMainView(newView);
		} catch (Throwable t) {
			handleShowViewError(t);
		}
	}

	/**
	 * Show view. May throw an Exception.
	 *
	 * @param newView the new view
	 */
	void showMainView(final Class<? extends AbstractFxmlView> newView) throws RuntimeException {
		final AbstractFxmlView view = applicationContext.getBean(newView);

		if (scene == null) {
			scene = new Scene(view.getView());
		} else {
			scene.setRoot(view.getView());
		}
		stage.setScene(scene);

		applyEnvPropsToStage();

		stage.getIcons().addAll(icons);
		stage.show();
	}

	private void applyEnvPropsToStage() {
		Environment env = applicationContext.getEnvironment();
		PropertyReaderHelper.setIfPresent(env, "javafx.title", String.class, stage::setTitle);
		PropertyReaderHelper.setIfPresent(env, "javafx.stage.width", Double.class, stage::setWidth);
		PropertyReaderHelper.setIfPresent(env, "javafx.stage.height", Double.class, stage::setHeight);
		PropertyReaderHelper.setIfPresent(env, "javafx.stage.resizable", Boolean.class, stage::setResizable);
	}

	/**
	 * Show main view. If showView throws any exception, error dialog will show.
	 *
	 * @param viewClass the new window view class.
	 * @param mode      See {@code javafx.stage.Modality}.
	 * @see #showView(Class, Modality)
	 */
	void showViewOrError(final Class<? extends AbstractFxmlView> viewClass, final Modality mode) {
		try {
			showView(viewClass, mode);
		} catch (Throwable t) {
			handleShowViewError(t);
		}
	}

	/**
	 * @param viewClass The FxmlView derived class that should be shown.
	 * @param mode      See {@code javafx.stage.Modality}.
	 */
	void showView(final Class<? extends AbstractFxmlView> viewClass, final Modality mode) {
		final AbstractFxmlView view = applicationContext.getBean(viewClass);
		Stage newStage = new Stage();

		Scene scene = view.getView().getScene();
		if (scene == null) {
			scene = new Scene(view.getView());
		}

		newStage.setScene(scene);
		newStage.initModality(mode);
		newStage.initOwner(getStage());
		newStage.setTitle(view.getDefaultTitle());
		newStage.initStyle(view.getDefaultStyle());

		newStage.showAndWait();
	}

	private void handleShowViewError(Throwable throwable) {
		LOGGER.error("Failed to create view: ", throwable);
		showErrorAlert(throwable);
	}

	/**
	 * Close application context.
	 *
	 * @see javafx.application.Application#stop()
	 */
	@Override
	public void stop() throws Exception {
		tryToCallAbstractFxSupportOnStop();
		if (applicationContext != null) {
			applicationContext.close();
		} // else: someone did it already
		tryToCallAbstractFxSupportOnClose();
	}

	private void tryToCallAbstractFxSupportOnStop() {
		try {
			abstractJavaFxApplicationSupport.ifPresent(AbstractJavaFxApplicationSupport::onStop);
		} catch (Exception e) {
			LOGGER.error("Exception in onStop method", e);
		}
	}

	private void tryToCallAbstractFxSupportOnClose() {
		try {
			abstractJavaFxApplicationSupport.ifPresent(AbstractJavaFxApplicationSupport::onClose);
		} catch (Exception e) {
			LOGGER.error("Exception in onStop method", e);
		}
	}

	Stage getStage() {
		return stage;
	}

	Scene getScene() {
		return scene;
	}

	SystemTray getSystemTray() {
		return systemTray;
	}

	static class JavaFxApplicationInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		final JavaFxApplication javaFxApplication;

		JavaFxApplicationInitializer(JavaFxApplication javaFxApplication) {
			this.javaFxApplication = javaFxApplication;
		}

		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {
			applicationContext.getBeanFactory().registerSingleton(JavaFxApplication.class.getSimpleName(), javaFxApplication);
		}
	}
}
