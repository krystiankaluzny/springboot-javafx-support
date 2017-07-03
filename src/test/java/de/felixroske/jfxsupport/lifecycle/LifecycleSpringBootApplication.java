package de.felixroske.jfxsupport.lifecycle;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.felixroske.jfxsupport.FXMLView;

/**
 * Created by Krystian Kałużny on 03.07.2017.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootApplication
@ComponentScan(useDefaultFilters = false,
		includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = FXMLView.class),
		excludeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes = InactiveSpringBootAppExcludeFilter.class))
@interface LifecycleSpringBootApplication {
}
