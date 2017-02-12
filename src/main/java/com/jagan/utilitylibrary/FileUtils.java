package com.jagan.utilitylibrary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.mozilla.universalchardet.UniversalDetector;

public class FileUtils {
	public static String PATH_SEPARATOR = "/";
	public static final String EXTENSION_TSV = ".tsv";
	public static final String EXTENSION_TXT = ".txt";
	public static final String EXTENSION_CSV = ".csv";
	public static final String EXTENSION_XML = ".xml";
	public static final String EXTENSION_JSON = ".json";
	public static final String EXTENSION_FEED_FILE = ".ff";
	public static final String EXTENSION_LOG_FILE = ".log";
	public static final String EXTENSION_ZIP = ".zip";
	public static final String EXTENSION_GZIP = ".gz";
	public static final String EXTENSION_GZIP_ALT = ".gzip";
	public static final String DEFAULT_ENCODING = "UTF-8";
	public static final String ENCODING_CP1252 = "Cp1252";
	public static final String ENCODING_UTF_16 = "UTF-16";
	public static final String ENCODING_SHIFT_JIS = "SHIFT_JIS";
	public static final char QUOTE_DEFAULT = '\uFFFF';
	public static final char QUOTE_DOUBLE = '\"';
	public static char DELIMITER_TAB = '\t';
	public static char DELIMITER_COMMA = ',';
	public static int DEFAULT_BUFFER_SIZE = 32768;
	public static Object lock = new Object();

	public enum FILE_TYPE_PARSER {
		TEXT, XML, TEXT_CSV, UNKNOWN
	}

	/**
	 * Check whether a file or directory exists
	 * 
	 * @param fileName
	 *           the file name or directory name
	 * @return true, if successful
	 */
	public static final boolean exists(final String fileName) {
		File f = null;
		try {
			f = new File(fileName);
		} catch (final NullPointerException e) {
			return false;
		}
		if (f.exists()) {
			return true;
		}
		return false;
	}

	public static final boolean deleteDirectory(final String directory) {
		return deleteDirectory(new File(directory));
	}

	public static final boolean deleteDirectory(final File directory) {
		if (directory == null) {
			return false;
		}
		if (!directory.exists()) {
			return true;
		}
		if (!directory.isDirectory()) {
			return false;
		}

		final String[] list = directory.list();

		// Some JVMs return null for File.list() when the directory is empty.
		if (list != null) {
			for (final String element : list) {
				final File entry = new File(directory, element);
				if (entry.isDirectory()) {
					if (!FileUtils.deleteDirectory(entry)) {
						return false;
					}
				} else {
					if (!entry.delete()) {
						return false;
					}
				}
			}
		}
		return directory.delete();
	}

	public static boolean deleteFile(final String fileName) {
		final File f = new File(fileName);
		if (f.exists() && f.isFile() && f.canWrite()) {
			return f.delete();
		}
		return false;
	}

	/*
	 * wildcardFileName : "sample*.java", "hcdiff*.html"
	 */
	public static boolean deleteFiles(final String folderName, final String wildcardFileName) {
		final File dir = new File(folderName);
		final FileFilter fileFilter = new WildcardFileFilter(wildcardFileName);
		final File[] files = dir.listFiles(fileFilter);
		for (final File file : files) {
			file.delete();
		}
		return true;
	}

	public static void copy(final String srcName, final String destName) throws IOException {
		final FileInputStream src = new FileInputStream(new File(srcName));
		int endIndex = destName.lastIndexOf("/");
		if (endIndex != 1) {
			String pathTest = destName.substring(0, endIndex);
			File testDir = new File(pathTest);
			if (!testDir.isDirectory()) {
				FileUtils.createDirectory(pathTest);
			}
		}
		final FileOutputStream dest = new FileOutputStream(new File(destName));
		copy(src, dest);
	}

	public static void copy(final FileInputStream src, final FileOutputStream dest) throws IOException {
		final FileChannel srcChannel = src.getChannel();
		final FileChannel destChannel = dest.getChannel();
		long totalBytesWritten = 0L;
		long totalFileSize = srcChannel.size();

		try {
			while (totalBytesWritten < totalFileSize) {
				long bytesWritten = srcChannel.transferTo(totalBytesWritten, totalFileSize, destChannel);
				totalBytesWritten += bytesWritten;
			}
		} catch (final IOException e) {
			throw e;
		} finally {
			if (srcChannel != null) {
				srcChannel.close();
			}
			if (destChannel != null) {
				destChannel.close();
			}
			if (src != null) {
				src.close();
			}
			if (dest != null) {
				dest.close();
			}
		}
	}

	public static boolean createDirectory(final String dirName) {
		return (new File(dirName)).mkdirs();
	}

	public static boolean move(final String srcFileName, final String destFileName, final boolean overwrite) throws IOException {
		final File srcFile = new File(srcFileName);
		final File destFile = new File(destFileName);
		if ((srcFile == null) || (destFile == null)) {
			return false;
		}

		if (destFile.exists()) {
			if (overwrite) {
				if (!destFile.delete()) {
					throw new IOException("Unable to delete source file: " + destFile.getName());
				}
			} else {
				throw new IOException("File exists, but overwrite = false for file: " + destFile.getName());
			}
		}

		if (srcFile.renameTo(destFile)) {
			return true;
		}

		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(srcFile);
			out = new FileOutputStream(destFile);
			copy(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
			if (!srcFile.delete()) {
				throw new IOException("Unable to delete source file: " + srcFile.getName());
			}
		} finally {
			if (in != null) {
				in.close();
				in = null;
			}
			if (out != null) {
				out.flush();
				out.close();
				out = null;
			}
		}
		return true;
	}

	public static String[] getFilesInDirectory(final String directory, final FilenameFilter filter) {
		final File dir = new File(directory);
		if (filter != null) {
			return dir.list(filter);
		} else {
			return dir.list();
		}
	}

	public static FilenameFilter filterFeedFiles = new FilenameFilter() {
		public boolean accept(final File file, final String name) {
			return (name.toLowerCase().endsWith(EXTENSION_FEED_FILE));
		}
	};

	public static FilenameFilter filterLogFiles = new FilenameFilter() {
		
		public boolean accept(final File file, final String name) {
			return (name.toLowerCase().endsWith(EXTENSION_LOG_FILE));
		}
	};

	public static FilenameFilter filterZips = new FilenameFilter() {
		
		public boolean accept(final File file, final String name) {
			return (name.toLowerCase().endsWith(EXTENSION_ZIP));
		}
	};

	public static FilenameFilter filterGzips = new FilenameFilter() {
		
		public boolean accept(final File file, final String name) {
			return (name.toLowerCase().endsWith(EXTENSION_GZIP) || name.toLowerCase().endsWith(EXTENSION_GZIP_ALT));
		}
	};

	public static FilenameFilter filterParseableFiles = new FilenameFilter() {
		public boolean accept(final File file, final String name) {
			return ((name.toLowerCase().endsWith(EXTENSION_TSV)) || (name.toLowerCase().endsWith(EXTENSION_XML)) || (name.toLowerCase().endsWith(EXTENSION_TXT)));
		}
	};

	public static FilenameFilter filterParseableFilesNonXml = new FilenameFilter() {
		public boolean accept(final File file, final String name) {
			return ((name.toLowerCase().endsWith(EXTENSION_TSV)) || (name.toLowerCase().endsWith(EXTENSION_TXT)));
		}
	};

	public static FilenameFilter filterTsv = new FilenameFilter() {
		public boolean accept(final File file, final String name) {
			return (name.toLowerCase().endsWith(EXTENSION_TSV));
		}
	};

	public static String[] getZipFileList(final String dir) {
		return FileUtils.getFilesInDirectory(dir, FileUtils.filterZips);
	}

	public static String[] getGzipFileList(final String dir) {
		return FileUtils.getFilesInDirectory(dir, FileUtils.filterGzips);
	}

	public static String[] getParseableFileList(final String dir) {
		return FileUtils.getFilesInDirectory(dir, filterParseableFiles);
	}

	public static String[] getFeedFileList(final String dir) {
		return FileUtils.getFilesInDirectory(dir, filterFeedFiles);
	}

	public static String getFileExtension(final String fileName) {
		final int dotInd = fileName.lastIndexOf('.');
		// if dot is in the first position, this is a hidden file rather than an
		// extension
		return (dotInd > 0 && dotInd < fileName.length()) ? fileName.substring(dotInd + 1) : null;
	}

	public static String removeFileExtension(final String fileName) {
		final int dotInd = fileName.lastIndexOf('.');
		return (dotInd > 0 && dotInd < fileName.length()) ? fileName.substring(0, dotInd) : null;
	}

	public static String getFilePath(final String fileName) {
		final int slashInd = fileName.lastIndexOf(PATH_SEPARATOR);
		// if dot is in the first position, this is a hidden file rather than an extension
		return (slashInd > 0 && slashInd < fileName.length()) ? fileName.substring(0, slashInd + 1) : null;
	}

	public static String getFileNameFromFullPath(final String fileName) {
		final int slashInd = fileName.lastIndexOf(PATH_SEPARATOR);
		// if dot is in the first position, this is a hidden file rather than an extension
		return (slashInd > 0 && slashInd < fileName.length()) ? fileName.substring(slashInd + 1, fileName.length()) : null;
	}

	public static String getTopmostPath(final String fileAndPath) {
		if (fileAndPath.indexOf(PATH_SEPARATOR) == -1) {
			return null;
		}
		final String[] tokens = fileAndPath.split("\\/", -1);
		if (tokens.length > 1) {
			return tokens[tokens.length - 2];
		} else {
			return tokens[0];
		}
	}

	// This finds the most recent file that makes it through the supplied filter.
	public static String getMostRecentFile(final String sourceDir, final FilenameFilter filter) {
		String latestFileName = "";
		long latestDate = 0;
		final String finalPath = (sourceDir.endsWith(FileUtils.PATH_SEPARATOR) ? sourceDir : sourceDir + FileUtils.PATH_SEPARATOR);
		final String[] fileList = FileUtils.getFilesInDirectory(sourceDir, filter);
		if (fileList == null || fileList.length == 0) {
			return null;
		}
		for (final String fileName : fileList) {
			final File f = new File(finalPath + fileName);
			if (f.lastModified() > latestDate) {
				latestDate = f.lastModified();
				latestFileName = fileName;
			}
		}
		return latestFileName;
	}

	public static String getOldestFile(final String sourceDir, final FilenameFilter filter) {
		String latestFileName = "";
		long latestDate = new Date().getTime();
		final String finalPath = (sourceDir.endsWith(FileUtils.PATH_SEPARATOR) ? sourceDir : sourceDir + FileUtils.PATH_SEPARATOR);
		final String[] fileList = FileUtils.getFilesInDirectory(sourceDir, filter);
		if (fileList == null || fileList.length == 0) {
			return null;
		}
		for (final String fileName : fileList) {
			final File f = new File(finalPath + fileName);
			if (f.lastModified() < latestDate) {
				latestDate = f.lastModified();
				latestFileName = fileName;
			}
		}
		return latestFileName;
	}

	/**
	 * Detect character encoding in a file. <code>
	 * 	String encoding = FileUtils.detectEncoding("/home/dmcocca/products.txt");
	 * 	if(encoding == null) encoding = "UTF-8";
	 * 	BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream("/home/dmcocca/products.txt"), Charset.forName(encoding)));
	 * </code>
	 * 
	 * @param fileName
	 * @return
	 * @throws java.io.IOException
	 */
	public static String detectEncoding(final String fileName) throws java.io.IOException {
		return detectEncoding(new File(fileName));
	}

	public static String detectEncoding(final File file) throws java.io.IOException {
		final byte[] buf = new byte[4096];
		final java.io.FileInputStream fis = new java.io.FileInputStream(file);
		final UniversalDetector detector = new UniversalDetector(null);

		int nread;
		while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}
		detector.dataEnd();
		String encode = detector.getDetectedCharset();
		if (encode == null) {
			encode = FileUtils.DEFAULT_ENCODING;
		}
		return encode;
	}

	public static boolean renameFile(final String oldFileName, final String newFileName) {
		final File oldFile = new File(oldFileName);
		return oldFile.renameTo(new File(newFileName));
	}

	public static void writeStringFile(final String stringContent, final String filename) {
		writeStringFile(stringContent, filename, false);
	}

	public static void writeStringFile(final String stringContent, final String filename, final boolean append) {
		if (!FileUtils.exists(FileUtils.getFilePath(filename))) {
			FileUtils.createDirectory(FileUtils.getFilePath(filename));
		}

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, append), "UTF-8"));
			writer.write(stringContent);
		} catch (final IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (final Exception e2) {
				// e2.printStackTrace();
			}
		}
	}

	public static void writeToFile(final List<String> content, final String filename) {
		writeToFile(content, filename, false);
	}

	public static void writeToFile(final List<String> content, final String filename, final boolean append) {
		if (content.isEmpty()) {
			return;
		}
		if (!FileUtils.exists(FileUtils.getFilePath(filename))) {
			FileUtils.createDirectory(FileUtils.getFilePath(filename));
		}
		String newLine = System.getProperty("line.separator");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, append), "UTF-8"));
			for (String s : content) {
				writer.write(s + newLine);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();
				writer.close();
			} catch (final Exception e2) {
				// yum
			}
		}
	}

	// write to a file using a syncronized block so that threads cannot overwrite each other
	public static void writeStringFileTS(final String stringContent, final String filename, String encoding, final boolean append) {
		if (!FileUtils.exists(FileUtils.getFilePath(filename))) {
			FileUtils.createDirectory(FileUtils.getFilePath(filename));
		}

		BufferedWriter writer = null;
		try {
			synchronized (lock) {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, append), encoding));
				writer.write(stringContent);
			}
		} catch (final IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (final Exception e2) {
				// e2.printStackTrace();
			}
		}
	}

	// write to a file using a syncronized block so that threads cannot overwrite each other
	public static void writeStringFileTS(final String stringContent, final String filename, final boolean append) {
		writeStringFileTS(stringContent, filename, DEFAULT_ENCODING, append);
	}

	/**
	 * Determine the type of parsable file
	 */
	public static FILE_TYPE_PARSER detectFileTypeParser(final String fileName) {
		if (fileName.toLowerCase().endsWith(EXTENSION_TSV) || fileName.toLowerCase().endsWith(EXTENSION_TXT) || fileName.toLowerCase().endsWith(EXTENSION_CSV)) {
			return FILE_TYPE_PARSER.TEXT;
		} else if (fileName.toLowerCase().endsWith(EXTENSION_XML)) {
			return FILE_TYPE_PARSER.XML;
		}
		return FILE_TYPE_PARSER.UNKNOWN;
	}

	/**
	 * Download file from a URL
	 * 
	 * @throws IOException
	 */
	public static boolean downloadImageFile(final String sourceUrl, final String destFile) {
		/*
		 * if (!FileUtils.exists(targetFolder) && !FileUtils.createDirectory(targetFolder)) { throws new FolderNotFoundException(); }
		 */
		BufferedInputStream in = null;

		BufferedOutputStream bos = null;
		try {
			URL url;
			url = new URL(sourceUrl);

			final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			// conn.setRequestProperty("Content-Type", "application/image");
			final String contentType = conn.getContentType();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND || !contentType.contains("image")) {
				return false;
			}
			in = new BufferedInputStream(url.openStream());
			bos = new BufferedOutputStream(new FileOutputStream(destFile), 1024);

			final byte[] buffer = new byte[1024];
			int x = 0;
			while ((x = in.read(buffer, 0, 1024)) >= 0) {
				bos.write(buffer, 0, x);
			}
		} catch (final MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
			} catch (final Exception e) {
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	/**
	 * Create a string representation of the current date and time
	 */
	public static String createDateTimeString() {
		final String dt = DateUtils.now("yyyy/MM/dd HH:mm:ss");
		return (((dt.replace('\\', '_')).replace('/', '_')).replace(' ', '.')).replace(':', '.');
	}

	/**
	 * Create a string representation of the current date
	 */
	public static String createDateString() {
		final String dt = DateUtils.now("yyyy/MM/dd");
		return ((dt.replace('\\', '_')).replace('/', '_'));
	}

	public static long getLastModifiedTime(final String fileName) {
		return new File(fileName).lastModified();
	}

	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}

		if (ext == null) {
			return "";
		}
		return ext;
	}

	// zero-based index read
	public static String readSingleLine(String fileName, String encoding, int lineToRead) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding), DEFAULT_BUFFER_SIZE);
			int counter = 0;
			for (String line; (line = br.readLine()) != null;) {
				if (lineToRead == counter++) {
					return line;
				}
			}
		} catch (final IOException e) {
			// yum
		}
		return null;
	}

	public static List<String> readLines(String fileName, String encoding, int startLine, int endLine) {
		List<String> results = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding), DEFAULT_BUFFER_SIZE);
			int counter = 0;
			for (String line; (line = br.readLine()) != null;) {
				if (counter >= startLine && counter <= endLine) {
					results.add(line);
				}
				if (counter > endLine) {
					break;
				}
				++counter;
			}
		} catch (final IOException e) {
			// yum
		}
		return results;
	}

	// List files in directory and sub-directories
	public static void listf(String directoryName, List<File> files) {
		File directory = new File(directoryName);

		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				files.add(file);
			} else if (file.isDirectory()) {
				listf(file.getAbsolutePath(), files);
			}
		}
	}
}
