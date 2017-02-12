package com.jagan.utilitylibrary;

import java.io.File;
import java.io.IOException;

//Simple customizable recursive class to iterate over dirs and files
public class DirectoryWalker {
	public final void walk(final File f) throws IOException {
		if (f.isDirectory()) {
			onDirectory(f);
			final File[] children = listFiles(f);
			if (children != null) {
				for (final File child : children) {
					walk(child);
				}
			}
			return;
		}
		onFile(f);
	}

	public File[] listFiles(final File f) {
		return f.listFiles();
	}

	public void onDirectory(final File d) {
	}

	public void onFile(final File f) {
	}

}
