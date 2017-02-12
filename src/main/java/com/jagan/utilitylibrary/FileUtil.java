package com.jagan.utilitylibrary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class FileUtil {

	public static boolean zipFile(final String filePath, final String zipPath) {
		try {
			File inFile = new File(filePath);
			inFile.setExecutable(true);
			inFile.setReadable(true);
			inFile.setWritable(true);

			String filename = inFile.getName();
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					zipPath));

			FileInputStream in = new FileInputStream(filePath);

			byte[] buf = new byte[1024];

			out.putNextEntry(new ZipEntry(filename));
			int len;
			while ((len = in.read(buf)) != -1) {
				out.write(buf, 0, len);
			}

			out.finish();
			out.close();
			in.close();

			return true;

		} catch (IllegalArgumentException iad) {
			iad.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean writeBytesToFile(final byte[] byteContent,
			final String filename) {
		FileOutputStream out = null;
		try {
			File file = new File(filename);
			file.setExecutable(true);
			file.setReadable(true);
			file.setWritable(true);

			File dir = new File(file.getParent());
			if (!dir.exists())
				dir.mkdirs();
			dir.setExecutable(true);
			dir.setReadable(true);
			dir.setWritable(true);

			out = new FileOutputStream(filename);
			out.write(byteContent);
		} catch (final IOException e1) {
			e1.printStackTrace();
			return false;
		} finally {
			try {
				out.close();
			} catch (final Exception e2) {
				// e2.printStackTrace();
				return false;
			}
		}

		return true;
	}

	public static String readStringFromFile(final String filename) {
		BufferedReader reader = null;
		StringBuffer buf = new StringBuffer();
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename)));
			String line;
			while ((line = reader.readLine()) != null) {
				buf.append(line);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buf.toString();
	}

	public static boolean writeStringToFile(final String stringContent,
			final String filename, final boolean append) {
		BufferedWriter writer = null;
		try {
			File file = new File(filename);
			file.setExecutable(true);
			file.setReadable(true);
			file.setWritable(true);

			File dir = new File(file.getParent());
			if (!dir.exists())
				dir.mkdirs();
			dir.setExecutable(true);
			dir.setReadable(true);
			dir.setWritable(true);

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filename, append), "UTF-8"));
			writer.write(stringContent);
			writer.flush();
		} catch (final IOException e1) {
			e1.printStackTrace();
			return false;
		} finally {
			try {
				writer.close();
			} catch (final Exception e2) {
				// e2.printStackTrace();
				return false;
			}
		}
		return true;
	}



	public static class ScpUserInfo implements UserInfo {

		public String getPassphrase() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getPassword() {
			return "p4th0n!";
		}

		public boolean promptPassphrase(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean promptPassword(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean promptYesNo(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		public void showMessage(String arg0) {
			// TODO Auto-generated method stub

		}

	}

	public static boolean scpFile(String sourceFilename, String host,
			String destFilename) {
		FileInputStream fis = null;
		JSch jsch = new JSch();

		try {
			jsch.setKnownHosts(host);

			Session _session = jsch.getSession(
					"user", host, 22);

			UserInfo scpUserInfo = new ScpUserInfo();
			_session.setUserInfo(scpUserInfo);
			_session.setPassword(scpUserInfo.getPassword());
			_session.connect();

			String command = "scp -p -t " + destFilename;
			Channel channel = _session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();

			if (checkAck(in) != 0) {
				return false;
			}

			// send "C0644 filesize filename" where filename doesn't contain a /
			long filesize = (new File(sourceFilename)).length();
			command = "C0644 " + filesize + " ";
			if (sourceFilename.lastIndexOf('/') > 0) {
				command += sourceFilename.substring(sourceFilename
						.lastIndexOf('/') + 1);
			} else {
				command += sourceFilename;
			}
			command += "\n";

			out.write(command.getBytes());
			out.flush();

			if (checkAck(in) != 0) {
				return false;
			}

			// send the contents of the source file
			fis = new FileInputStream(sourceFilename);
			byte[] buf = new byte[1024];
			while (true) {
				int len = fis.read(buf, 0, buf.length);

				if (len <= 0) {
					break;
				}

				out.write(buf, 0, len);
			}

			fis.close();
			fis = null;

			// send '\0' to end it
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			if (checkAck(in) != 0) {
				return false;
			}

			out.close();

			channel.disconnect();
			_session.disconnect();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}

		return false;
	}

	static int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0)
			return b;
		if (b == -1)
			return b;

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				System.out.print(sb.toString());
			}
			if (b == 2) { // fatal error
				System.out.print(sb.toString());
			}
		}
		return b;
	}

	public static void copyFile(String srFile, String dtFile) {
		try {
			File f1 = new File(srFile);
			f1.setExecutable(true);
			f1.setReadable(true);
			f1.setWritable(true);

			File f2 = new File(dtFile);
			f2.setExecutable(true);
			f2.setReadable(true);
			f2.setWritable(true);

			InputStream in = new FileInputStream(f1);

			// For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			System.out.println("File copied.");
		} catch (FileNotFoundException ex) {
			System.out
					.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] arg) {
		FileUtil fu = new FileUtil();
		fu.zipFile("C:/Users/Retailigencia1/Desktop/hi.txt",
				"C:/Users/Retailigencia1/Desktop/hi.zip");

	}
}
