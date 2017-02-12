package com.jagan.utilitylibrary;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClassesInfo {

	private String packageName = "";
	private String path = "";

	public void setPath(String path) {
		this.path = path;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPath() {
		return this.path;
	}

	public String getPackageName() {
		return this.packageName;
	}

	public HashMap<String, String> getClassMap(String packageName, String path) {
		HashMap<String, String> classMaps = new HashMap<String, String>();

		if (path.contains(".jar")) {
		
			File jar;
			try {
				jar = new File(path);

				ZipInputStream zip = new ZipInputStream(new FileInputStream(jar));
				ZipEntry entry;
				while ((entry = zip.getNextEntry()) != null) {
					if (entry.getName().endsWith(".class")) {
						String className = entry.getName()
								.replaceAll("[$].*", "")
								.replaceAll("[.]class", "").replace('/', '.');
						if (className.startsWith(packageName)) {
							classMaps.put(getStrippedClassName(className)
									.toLowerCase(),
									getStrippedClassName(className));
						}
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return classMaps;
	}

	public void displayClassMap(HashMap<String, String> classMaps) {
		Set<Entry<String, String>> set = classMaps.entrySet();
		Iterator<Entry<String, String>> iterate = set.iterator();
		while (iterate.hasNext()) {
			Entry<String, String> entry = iterate.next();
			System.out.println(entry.getKey() + " :: " + entry.getValue());
		}
	}

	public List<String> getClassList(String packageName, String path) {
		return null;
	}

	public String getStrippedClassName(String classPath) {
		return classPath.substring(classPath.lastIndexOf(".") + 1);
	}

	public static void main(String[] args) {
		ClassesInfo cl = new ClassesInfo();
		HashMap<String,String> map = cl.getClassMap("com.retailigence.crawl.crawler", "C:/Users/Retailigencia1/workspace/rtl-crawl/target/rtl-crawl-1.0.0.jar");
		
		if(map.containsKey("walgreens")) {
			System.out.println(map.get("walgreens"));
		}

	}

}
