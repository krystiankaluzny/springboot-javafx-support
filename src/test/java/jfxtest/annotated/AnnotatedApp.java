package jfxtest.annotated;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.JavaFxSupport;

@SpringBootApplication
public class AnnotatedApp extends AbstractJavaFxApplicationSupport {

	public static void main(String[] args) {
		JavaFxSupport.launchApp(AnnotatedApp.class, AnnotatedView.class, args);
	}
}