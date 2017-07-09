package de.felixroske.jfxsupport.util;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Created by Krystian Kałużny on 09.07.2017.
 */
class InactivePackageExcludeFilter extends ExcludeFilter {

	private final String utilPackageName = "de.felixroske.jfxsupport.util";

	@Override
	boolean exclude(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
		try {
			if (isAnnotated(metadataReader, Component.class)) {

				String currentClassName = metadataReader.getClassMetadata().getClassName();
				String packageName = activeSpringBootClass.getPackage().getName();
				boolean result = Stream.of(utilPackageName, packageName).noneMatch(currentClassName::startsWith);
				if(!result) {
					System.out.println(currentClassName);
				}
				return result;
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		return true;
	}
}
