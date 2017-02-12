package com.jagan.utilitylibrary;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassFinder {
	private static String jarSentinel = ".jar!/";

	/**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 * 
	 * @param packageName
	 *           The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Class<?>[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		ArrayList<Class> classes = new ArrayList<Class>();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		for (File directory : dirs) {
//			if (directory.toString().contains(jarSentinel)) {
//				classes.addAll(findClassesFromJar(directory, packageName));
//			} else {
				classes.addAll(findClasses(directory, packageName));
//			}
		}
		return classes.toArray(new Class[classes.size()]);
	}

	private static List<Class<?>> findClassesFromJar(File directory, String packageName) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		String dirName = directory.toString();
		String fileTag = "file:";
		int splitIndex = dirName.indexOf(jarSentinel);
		String jarFile = dirName.substring(0, splitIndex + jarSentinel.length() - 2);
		if (jarFile.contains(fileTag)) {
			jarFile = jarFile.substring(fileTag.length(), jarFile.length());
		}
		String pkg = dirName.substring(splitIndex + jarSentinel.length(), dirName.length());
		List<String> strClasses = getClassesFromJarFile(jarFile, pkg);
		for (String cn : strClasses) {
			Class<?> cls = Class.forName(cn);
			classes.add(cls);
		}
		return classes;
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 * 
	 * @param directory
	 *           The base directory
	 * @param packageName
	 *           The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

	public static List<String> getClassesFromJarFile(String file, String pkg) {
		List<String> classNames = new ArrayList<String>();
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(file);
			Enumeration allEntries = jarFile.entries();
			while (allEntries.hasMoreElements()) {
				JarEntry entry = (JarEntry)allEntries.nextElement();
				String name = entry.getName();
				String className = filterfileByPackage(name, pkg);
				if (!StringUtils.isEmpty(className)) {
					classNames.add(className);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classNames;
	}

	public static String filterfileByPackage(String s, String packageName) {
		String pkg = packageName.replace('.', '/');
		String classFile = null;
		if (s.contains(pkg)) {
			int index = s.lastIndexOf("/");
			if (index != -1) {
				index += 1;
				if (index < s.length()) {
					if (s.endsWith(".class")) {
						s = s.substring(0, s.length() - 6);
					}
					classFile = s.replace('/', '.'); // create canonical class name
				}
			}
		}
		return classFile;
	}

}
