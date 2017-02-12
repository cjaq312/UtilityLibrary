package com.jagan.utilitylibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils {

	/**
	 * Download file from a URL
	 * 
	 * @throws IOException
	 */
	public static String getResponseString(final String sourceUrl, final int timeout) throws IOException {

		final StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;

		try {
			URL url;
			url = new URL(sourceUrl);

			final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setReadTimeout(timeout);
			String line;

			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}

		} catch (final MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return builder.toString();
	}

}
