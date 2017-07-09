package de.felixroske.jfxsupport.util;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;

/**
 * Created by Krystian Kałużny on 03.07.2017.
 */
class InactiveSpringBootAppExcludeFilter extends ExcludeFilter {

	@Override
	public boolean exclude(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
		try {
			if (isAnnotated(metadataReader, SpringBootApplication.class)) {
				return !activeSpringBootClass.getName().equals(metadataReader.getClassMetadata().getClassName());
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return false;
	}
}
