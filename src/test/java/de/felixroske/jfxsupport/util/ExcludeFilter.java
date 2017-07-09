package de.felixroske.jfxsupport.util;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * Created by Krystian Kałużny on 09.07.2017.
 */
abstract class ExcludeFilter implements TypeFilter {
	static Class<?> activeSpringBootClass;

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
		return exclude(metadataReader, metadataReaderFactory);
	}

	protected boolean isAnnotated(MetadataReader metadataReader, Class<? extends Annotation> annotation) throws ClassNotFoundException {
		Class<?> clazz = ClassUtils.forName(metadataReader.getClassMetadata().getClassName(), getClass().getClassLoader());
		return AnnotationUtils.findAnnotation(clazz, annotation) != null;
	}

	abstract boolean exclude(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException;
}
