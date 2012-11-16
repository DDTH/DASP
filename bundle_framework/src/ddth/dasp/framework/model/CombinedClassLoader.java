package ddth.dasp.framework.model;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CombinedClassLoader extends ClassLoader {
	private Set<ClassLoader> loaders = new HashSet<ClassLoader>();

	public CombinedClassLoader() {
	}

	public CombinedClassLoader(Collection<ClassLoader> classLoaders) {
		for (ClassLoader classLoader : classLoaders) {
			addLoader(classLoader);
		}
	}

	public void addLoader(ClassLoader loader) {
		if (loader != null) {
			loaders.add(loader);
		}
	}

	public void addLoader(Class<?> clazz) {
		addLoader(clazz.getClassLoader());
	}

	public Class<?> findClass(String name) throws ClassNotFoundException {
		for (ClassLoader loader : loaders) {
			try {
				return loader.loadClass(name);
			} catch (ClassNotFoundException cnfe) {
				// Try next
			}
		}
		throw new ClassNotFoundException(name);
	}

	public URL getResource(String name) {
		for (ClassLoader loader : loaders) {
			URL url = loader.getResource(name);
			if (url != null) {
				return url;
			}
		}
		return null;
	}
}
