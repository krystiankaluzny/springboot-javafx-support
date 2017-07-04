package de.felixroske.jfxsupport.misc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;

/**
 * Created by Krystian Kałużny on 03.07.2017.
 */
class InactiveSpringBootAppExcludeFilter implements TypeFilter {

	static Class<?> activeSpringBootClass;

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {

		AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
		if(annotationMetadata.hasAnnotation(SpringBootApplication.class.getName())) {
			return activeSpringBootClass.getName().equals(metadataReader.getClassMetadata().getClassName());
		}
		return false;
	}
}
