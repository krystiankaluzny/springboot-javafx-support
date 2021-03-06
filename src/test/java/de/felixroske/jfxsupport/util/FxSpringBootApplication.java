package de.felixroske.jfxsupport.util;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.felixroske.jfxsupport.FXMLView;

/**
 * Created by Krystian Kałużny on 03.07.2017.
 * Annotation designed for testing purpose. In normal project using @{@link SpringBootApplication} is enough.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootApplication
@ComponentScan(useDefaultFilters = false,
		includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Component.class),
		excludeFilters = {
				@ComponentScan.Filter(type = FilterType.CUSTOM, classes = InactiveSpringBootAppExcludeFilter.class),
				@ComponentScan.Filter(type = FilterType.CUSTOM, classes = InactivePackageExcludeFilter.class)
		},
		basePackages = { "de.felixroske.jfxsupport" })
public @interface FxSpringBootApplication {
}
