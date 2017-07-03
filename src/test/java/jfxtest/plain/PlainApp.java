package jfxtest.plain;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.JavaFxSupport;

@SpringBootApplication
public class PlainApp extends AbstractJavaFxApplicationSupport {

	public static void main(String[] args) {
		JavaFxSupport.launchApp(PlainApp.class, PlainView.class, null, args);
	}
}