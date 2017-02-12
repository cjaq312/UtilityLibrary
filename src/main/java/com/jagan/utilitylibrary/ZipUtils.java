package com.jagan.utilitylibrary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


public class ZipUtils {
	private static final int READ_BUFFER_SIZE = 4096;
	private static final int COMPRESSION_LEVEL_MAX = 9;
	private static final String MAC_TARD_DIR = "__MACOSX";

	public enum FileType {
		ZIP, GZIP, OTHER
	}

	public static FileType detectFileType(final String fileName) {
		final File file = new File(fileName);
		String finalExt = "";
		final int index = file.getName().lastIndexOf('.');
		if (index > 0 && index <= file.getName().length() - 2) {
			finalExt = file.getName().substring(index).toLowerCase();
			if ((finalExt.contentEquals(".gz")) || (finalExt.contentEquals(".gzip"))) {
				return FileType.GZIP;
			}
			if (finalExt.contentEquals(".zip")) {
				return FileType.ZIP;
			}
		}
		return FileType.OTHER;
	}

	/**
	 * Zip a file or folder specified by the source and store the processed file at the destination with the specified filename.
	 * 
	 * @param source
	 *           Source location of the file/folder to be zipped
	 * @param destination
	 *           Destination location of the file folder to be zipped
	 * @param filename
	 *           File name of the zip file
	 */
	public static void zipFile(final File source, final File destination, final String filename) throws ZipException, IOException {

		final ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(destination + File.separator + filename));

		for (final File file : source.listFiles()) {
			final byte[] buf = new byte[READ_BUFFER_SIZE];
			int len;

			final FileInputStream fis = new FileInputStream(file);
			zipFile.putNextEntry(new ZipEntry(file.getName()));

			while ((len = fis.read(buf)) > 0) {
				zipFile.write(buf, 0, len);
			}
			zipFile.closeEntry();
			fis.close();
		}
		zipFile.close();

	}

	public static void createZip(final String destFile, final String[] sourceFileList) throws IOException {
		final List<String> tempList = new ArrayList<String>(Arrays.asList(sourceFileList));
		createZip(destFile, tempList);
	}

	public static void createZip(final String destFile, final String sourceFile) throws IOException {
		final List<String> tempList = new ArrayList<String>(Arrays.asList(sourceFile));
		createZip(destFile, tempList);
	}

	public static void createZip(final String destFile, final List<String> sourceFileList) throws IOException {
		final File archiveFile = new File(destFile);
		final byte buffer[] = new byte[READ_BUFFER_SIZE];

		final FileOutputStream stream = new FileOutputStream(archiveFile);
		final ZipOutputStream out = new ZipOutputStream(stream);

		for (final String fileItem : sourceFileList) {
			final File file = new File(fileItem);
			if (fileItem == null || !file.exists() || file.isDirectory()) {
				continue;
			}

			// Add archive entry
			final ZipEntry zipAdd = new ZipEntry(file.getName());
			zipAdd.setTime(file.lastModified());
			out.putNextEntry(zipAdd);

			// Read input & write to output
			final FileInputStream in = new FileInputStream(fileItem);
			while (true) {
				final int nRead = in.read(buffer, 0, buffer.length);
				if (nRead <= 0) {
					break;
				}
				out.write(buffer, 0, nRead);
			}
			in.close();
		}
		out.close();
		stream.close();
	}

	public static void createZipFromDir(String destFile, String sourceDir) throws IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destFile));
		String path = "";
		addDirectory(sourceDir, out, path);
		out.close();
	}

	private static void addDirectory(String directory, ZipOutputStream out, String path) throws IOException {
		File zipDir = new File(directory);
		String[] dirList = zipDir.list();
		byte[] readBuffer = new byte[READ_BUFFER_SIZE];
		int bytesIn = 0;
		for (int i = 0; i < dirList.length; i++) {
			File f = new File(zipDir, dirList[i]);
			if (f.isDirectory()) {
				String filePath = f.getPath();
				addDirectory(filePath, out, path + f.getName() + "/");
				continue;
			}
			FileInputStream in = new FileInputStream(f);
			try {
				ZipEntry anEntry = new ZipEntry(path + f.getName());
				out.putNextEntry(anEntry);
				bytesIn = in.read(readBuffer);
				while (bytesIn != -1) {
					out.write(readBuffer, 0, bytesIn);
					bytesIn = in.read(readBuffer);
				}
			} finally {
				in.close();
			}
		}
	}

	/**
	 * @param source
	 * @param destination
	 * @param fileName
	 * @throws ZipException
	 * @throws IOException
	 */
	public static void zipFile(final String source, final String destination, final String fileName) throws ZipException, IOException {
		zipFile(new File(source), new File(destination), fileName);
	}

	/**
	 * @param source
	 * @param fileName
	 * @throws ZipException
	 * @throws IOException
	 */
	public static void zipFile(final File source, final String fileName) throws ZipException, IOException {
		final File destination = source.isDirectory() ? source : source.getParentFile();
		zipFile(source, destination, fileName);
	}

	/**
	 * @param source
	 * @param fileName
	 * @throws ZipException
	 * @throws IOException
	 */
	public static void zipFile(final String source, final String fileName) throws ZipException, IOException {
		zipFile(new File(source), fileName);
	}

	/**
	 * @param source
	 */
	public static void unzipFile(final File source) throws ZipException, IOException {
		unzipFile(source, source.getParentFile(), StringUtils.STRING_EMPTY);
	}

	/**
	 * @param source
	 * @param destination
	 */
	public static void unzipFile(final File source, final File destination) throws ZipException, IOException {
		unzipFile(source, destination, StringUtils.STRING_EMPTY);
	}

	/**
	 * Unzip a file or a folder containing multiple files from the source location into the destination location. Optionally you can specify a prefix that all
	 * extracted files should be prefixed with.
	 * 
	 * @param source
	 *           Source file or folder that needs to be unzipped
	 * @param destination
	 *           Destination folder that will hold all unzipped files
	 * @param prefix
	 *           Optional string prefix for all extracted files
	 * @throws IOException
	 * @throws ZipException
	 */
	@Deprecated
	public static void unzipFile(final File source, final File destination, final String prefix) throws ZipException, IOException {
		final ZipFile zipFile = new ZipFile(source.getAbsoluteFile());
		final Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();

		// Create destination folders if non-existent
		if (!destination.exists()) {
			destination.mkdirs();
		}

		// Perform unzip process
		while (zipEntries.hasMoreElements()) {
			final ZipEntry zipEntry = zipEntries.nextElement();

			if (zipEntry.getName().startsWith(MAC_TARD_DIR)) {
				continue;
			}

			if (zipEntry.isDirectory()) {
				(new File(zipEntry.getName())).mkdirs();
				continue;
			}
			try {
				copyStream(zipFile.getInputStream(zipEntry), new BufferedOutputStream(new FileOutputStream(destination.getAbsolutePath() + File.separator
																																			+ prefix
																																			+ zipEntry.getName())));
			} catch (final FileNotFoundException e) {
			}
		}
		zipFile.close();

	}

	/**
	 * @param source
	 */
	public static void unzipFile(final String source) throws ZipException, IOException {
		unzipFile(new File(source), new File(source).getParentFile(), StringUtils.STRING_EMPTY);
	}

	/**
	 * @param source
	 * @param destination
	 */
	public static void unzipFile(final String source, final String destination) throws ZipException, IOException {
		unzipFile(new File(source), new File(destination), StringUtils.STRING_EMPTY);
	}

	/**
	 * @param source
	 * @param destination
	 * @param prefix
	 */
	public static void unzipFile(final String source, final String destination, final String prefix) throws ZipException, IOException {
		unzipFile(new File(source), new File(destination), prefix);
	}

	/**
	 * Copies a stream of data from the specified input stream to the specified output stream.
	 * 
	 * @param in
	 *           Stream containing data to be copied
	 * @param out
	 *           Stream holding the copied data
	 * @throws IOException
	 */
	private static void copyStream(final InputStream in, final OutputStream out) throws IOException {
		final byte[] buffer = new byte[READ_BUFFER_SIZE];
		int len;

		while ((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
		}

		in.close();
		out.close();
	}

	/**
	 * Zip file list.
	 * 
	 * @param fileList
	 *           the list of files to add to the zip file
	 * @param zipFileName
	 *           the zip file name
	 * @throws IOException
	 *            Signals that an I/O exception has occurred.
	 */
	public static void zipFileList(final List<String> fileList, final String zipFileName) throws IOException {
		final byte[] buffer = new byte[READ_BUFFER_SIZE];
		final FileOutputStream fileOut = new FileOutputStream(zipFileName);
		final ZipOutputStream zipOut = new ZipOutputStream(fileOut);
		zipOut.setLevel(COMPRESSION_LEVEL_MAX);
		for (final String srcFile : fileList) {
			final FileInputStream fileIn = new FileInputStream(srcFile);
			zipOut.putNextEntry(new ZipEntry(srcFile));
			int length;
			while ((length = fileIn.read(buffer)) > 0) {
				zipOut.write(buffer, 0, length);
			}
			zipOut.closeEntry();
			fileIn.close();
		}
		zipOut.close();
	}

	/**
	 * Zip file list with no path info saved per file.
	 * 
	 * @param fileList
	 *           the list of files to add to the zip file
	 * @param zipFileName
	 *           the zip file name
	 * @throws IOException
	 *            Signals that an I/O exception has occurred.
	 */
	public static void zipFileListNoPathInfo(final List<String> fileList, final String zipFileName) throws IOException {
		final byte[] buffer = new byte[READ_BUFFER_SIZE];
		final FileOutputStream fileOut = new FileOutputStream(zipFileName);
		final ZipOutputStream zipOut = new ZipOutputStream(fileOut);
		zipOut.setLevel(COMPRESSION_LEVEL_MAX);
		for (final String srcFile : fileList) {
			final FileInputStream fileIn = new FileInputStream(srcFile);
			int slash = -1;
			if (srcFile.lastIndexOf('\\') != -1) {
				slash = srcFile.lastIndexOf('\\');
			} else {
				slash = srcFile.lastIndexOf('/');
			}
			final String filename = srcFile.substring(slash + 1);
			zipOut.putNextEntry(new ZipEntry(filename));
			int length;
			while ((length = fileIn.read(buffer)) > 0) {
				zipOut.write(buffer, 0, length);
			}
			zipOut.closeEntry();
			fileIn.close();
		}
		zipOut.close();
	}

	/**
	 * Zip file list and delete the source files when complete.
	 * 
	 * @param fileList
	 *           the list of files to add to the zip file and delete after
	 * @param zipFileName
	 *           the zip file name
	 * @throws IOException
	 *            Signals that an I/O exception has occurred.
	 */
	public static void zipFileListAndDelete(final List<String> fileList, final String zipFileName) throws IOException {
		zipFileList(fileList, zipFileName);
		for (final String deleteName : fileList) {
			final File deleteFile = new File(deleteName);
			final boolean success = FileUtils.deleteDirectory(deleteFile);
			if (!success) {
			}
		}
	}

	public static boolean isArchive(final String fileName) {
		if (fileName.endsWith(FileUtils.EXTENSION_ZIP)) {
			return true;
		} else if (fileName.endsWith(FileUtils.EXTENSION_GZIP)) {
			return true;
		} else if (fileName.endsWith(FileUtils.EXTENSION_GZIP_ALT)) {
			return true;
		}
		return false;
	}

	public static boolean isValid(final String fileName) {
		ZipFile zipfile = null;
		try {
			zipfile = new ZipFile(new File(fileName));
			return true;
		} catch (final ZipException e) {
			return false;
		} catch (final IOException e) {
			return false;
		} finally {
			try {
				if (zipfile != null) {
					zipfile.close();
					zipfile = null;
				}
			} catch (final IOException e) {
			}
		}
	}

	/**
	 * Test for GZip file validity. The gzip support is not as robust as the zip support. To verify that a file is valid we must read the entire file.
	 * 
	 * @param fileName
	 *           the name of the gzip file to test
	 */
	public static boolean isGzipValid(final String fileName) {
		InputStream gzipStream = null;
		final byte[] buffer = new byte[READ_BUFFER_SIZE];
		try {
			gzipStream = new BufferedInputStream(new GZIPInputStream(new FileInputStream(fileName)));
			int len;
			while ((len = gzipStream.read(buffer)) > 0) {
			}
		} catch (final IOException e) {
			return false;
		} finally {
			try {
				if (gzipStream != null) {
					gzipStream.close();
					gzipStream = null;
				}
			} catch (final IOException e) {
			}
		}
		return true;
	}

	public static boolean gzipCompress(final String inFile, final String outFile) {
		GZIPOutputStream out = null;
		FileInputStream in = null;
		try {
			out = new GZIPOutputStream(new FileOutputStream(outFile)) {
				{
					def.setLevel(Deflater.BEST_COMPRESSION);
				}
			};
			in = new FileInputStream(inFile);

			final byte[] buf = new byte[READ_BUFFER_SIZE];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();

			out.finish();
			out.close();
		} catch (final FileNotFoundException e) {
	
			return false;
		} catch (final IOException e) {
	
			return false;
		}
		return true;
	}

	public static boolean gzipCompress(final String inFile) {
		return gzipCompress(inFile, inFile + ".gz");
	}

	public static boolean gzipDecompress(final String inputFileName, final String outputFileName) {
		final byte[] buffer = new byte[READ_BUFFER_SIZE];
		try {
			final OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFileName));
			final InputStream inputStream = new BufferedInputStream(new GZIPInputStream(new FileInputStream(inputFileName)));

			int len;
			while ((len = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, len);
			}
			inputStream.close();
			outputStream.close();
	

		} catch (final IOException e) {
	
			return false;
		}
		return true;
	}

	public static boolean gzipDecompress(final String fileName) {
		// we infer the dest file based on the src file name
		String destFileName = "unknown";
		final File file = new File(fileName);
		final int index = file.getName().lastIndexOf('.');
		if (index > 0 && index <= file.getName().length() - 2) {
			destFileName = file.getName().substring(0, index);
		}
		return gzipDecompress(fileName, destFileName);
	}

	public static void unzip(final String zipname) {

		try {
			final ZipFile zipFile = new ZipFile(zipname);
			final Enumeration enumeration = zipFile.entries();
			while (enumeration.hasMoreElements()) {
				final ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
				System.out.println("Unzipping: " + zipEntry.getName());
				final BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
				int size;
				final byte[] buffer = new byte[2048];
				final FileOutputStream fos = new FileOutputStream(zipEntry.getName());
				final BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length);
				while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
					bos.write(buffer, 0, size);
				}
				bos.flush();
				bos.close();
				fos.close();
				bis.close();
			}
		} catch (final IOException e) {
			e.printStackTrace();

		}
	}

	/**
	 * Unzip a file or a folder containing multiple files from the source location into the destination location. Optionally you can specify a prefix that all
	 * extracted files should be prefixed with.
	 * 
	 * @param source
	 *           Source file or folder that needs to be unzipped
	 * @param destination
	 *           Destination folder that will hold all unzipped files
	 * @param prefix
	 *           Optional string prefix for all extracted files
	 * @throws IOException
	 * @throws ZipException
	 */
	public static void unzipToDest(final File source, final File destination, String prefix) throws ZipException, IOException {
		
		if (prefix == null) prefix = "";
		final ZipFile zipFile = new ZipFile(source.getAbsoluteFile());
		final Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();

		// Create destination folders if non-existent
		if (!destination.exists()) {
			destination.mkdirs();
		}

		// Perform unzip process
		while (zipEntries.hasMoreElements()) {
			final ZipEntry zipEntry = zipEntries.nextElement();

			if (zipEntry.getName().startsWith(MAC_TARD_DIR)) {
				continue;
			}

			if (zipEntry.isDirectory()) {
				(new File(zipEntry.getName())).mkdirs();
				continue;
			}
			try {
				copyStream(zipFile.getInputStream(zipEntry), new BufferedOutputStream(new FileOutputStream(destination.getAbsolutePath() + File.separator
																																			+ prefix
																																			+ zipEntry.getName())));
			} catch (final FileNotFoundException e) {
			}
		}
		zipFile.close();
	}

	public static void unzipToDest(final String source, final String destination, final String prefix) throws ZipException, IOException {
		unzipToDest(new File(source), new File(destination), prefix);
	}

	public static void unzipToDest(final String source, final String destination) throws ZipException, IOException {
		unzipToDest(source, destination, null);
	}

}
