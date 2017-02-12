package com.jagan.utilitylibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.apache.commons.lang3.StringEscapeUtils;
import org.mozilla.universalchardet.UniversalDetector;

public class StringUtils {
	public static final String STRING_EMPTY = "";
	public static final String STRING_COMMA = ",";
	public static final String STRING_NEWLINE = System.getProperty("line.separator");
	public static final String UTF8_BOM = "\uFEFF"; // UTF-8 byte order mark

	public static String formatZip(final String zip) {
		String result;
		final String usZipRegEx = "\\d*";
		if (zip != null && zip.trim().matches(usZipRegEx)) {
			// pad it to 5 digits
			result = zip;
			while (result.length() < 5) {
				result = "0" + result;
			}
			return result;
		}
		return zip;
	}

	public static String doubleUnQuote(String input) {

		if (input.startsWith("\""))
			input = input.substring(1);
		if (input.endsWith("\""))
			input = input.substring(0, input.length() - 1);
		return input;
	}

	public static String doubleQuote(String input) {
		return "\"" + input + "\"";
	}

	public static Date parseDate(String date) {
		Date dt = null;
		if (date != null) {
			String format = "yyyyMMdd";
			format = format.substring(0, Math.min(format.length(), date.length()));
			try {
				dt = DateUtils.parseDate(date, format, TimeZone.getTimeZone("GMT"));
				dt = DateUtils.stripTime(dt, true);
			} catch (ParseException e) {
				System.out.println();
			}
		}
		return dt;
	}

	public static String formatDate(final Date dt) {
		if (dt == null) {
			return null;
		}
		final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS z");
		fmt.setTimeZone(TimeZone.getTimeZone("PST"));
		return fmt.format(dt);
	}

	public static String formatShortDate(final Date dt) {
		if (dt == null) {
			return null;
		}
		final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		fmt.setTimeZone(TimeZone.getTimeZone("PST"));
		return fmt.format(dt);
	}

	public static String formatShortDateHour(final Date dt) {
		if (dt == null) {
			return null;
		}
		final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH");
		fmt.setTimeZone(TimeZone.getTimeZone("PST"));
		return fmt.format(dt);
	}

	public static String formatJSONDate(final Date dt) {
		if (dt == null) {
			return null;
		}
		final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS'Z'");
		fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		return fmt.format(dt);
	}

	public static String formatDate(final Date dt, final TimeZone zone) {
		if (dt == null) {
			return null;
		}
		final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS z");
		fmt.setTimeZone(zone);
		return fmt.format(dt);
	}

	public static String hash(final String txt) throws Exception {
		final MessageDigest md = MessageDigest.getInstance("MD5");
		final byte[] hash = md.digest(txt.toLowerCase().getBytes());
		final BigInteger bigInt = new BigInteger(1, hash);
		String hashtext = bigInt.toString(16);
		// Now we need to zero pad it if you actually want the full 32 chars.
		while (hashtext.length() < 32) {
			hashtext = "0" + hashtext;
		}
		return hashtext;
	}

	public static double formatDouble(final double value) {
		final DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		final double result = Double.parseDouble(decimalFormat.format(value));
		return result;
	}

	public static String removeAllChars(final String originalString, final Character charToRemove) {
		if ((originalString == null) || (originalString.equalsIgnoreCase(""))) {
			return "";
		} else {
			// look for the Character passed in and omit it from the result
			final String charToRemoveString = charToRemove.toString();
			final char[] chars = originalString.toCharArray();
			final StringBuffer sb = new StringBuffer();
			for (final char d : chars) {
				final String c = String.valueOf(d);
				if (c.equals(charToRemoveString)) {
					// Do nothing so it gets removed
				} else {
					sb.append(c);
				}
			}
			return sb.toString();
		}
	}

	public static String removeAllDashes(final String originalString) {
		return removeAllChars(originalString, '-');
	}

	public static String removeAllTildes(final String originalString) {
		return removeAllChars(originalString, '~');
	}

	public static String doubleUpSingleBackslashes(final String originalString) {
		if ((originalString == null) || ("".equals(originalString))) {
			return originalString;
		} else {
			// look for single quotes in the string and double them up
			final char[] chars = originalString.toCharArray();
			final StringBuffer sb = new StringBuffer();
			for (final char d : chars) {
				final String c = String.valueOf(d);
				sb.append(c);
				if (c.equalsIgnoreCase("\\")) {
					sb.append("\\");
				}
			}
			return sb.toString();
		}
	}

	public static String truncateToSize(final String originalString, final int size) {
		if ((originalString == null) || ("".equals(originalString)) || (originalString.length() <= size)) {
			return originalString;
		} else {
			return originalString.substring(0, size);
		}
	}

	public static String doubleUpSingleQuotes(final String originalString) {
		if ((originalString == null) || ("".equals(originalString))) {
			return originalString;
		} else {
			// look for single quotes in the string and double them up
			final char[] chars = originalString.toCharArray();
			final StringBuffer sb = new StringBuffer();
			for (final char d : chars) {
				final String c = String.valueOf(d);
				sb.append(c);
				if (c.equalsIgnoreCase("'")) {
					sb.append("'");
				}
			}
			return sb.toString();
		}
	}

	public static String backslashDoubleQuotes(final String originalString) {
		if ((originalString == null) || ("".equals(originalString))) {
			return originalString;
		} else {
			// look for single quotes in the string and double them up
			final char[] chars = originalString.toCharArray();
			final StringBuffer sb = new StringBuffer();
			for (final char d : chars) {
				final String c = String.valueOf(d);

				if (c.equalsIgnoreCase("\"")) {
					sb.append("\"");
				} else {
					sb.append(c);
				}
			}
			return sb.toString();
		}
	}

	public static String doubleUpSingleQuotesAndBackslashes(final String originalString) {
		if ((originalString == null) || ("".equals(originalString))) {
			return originalString;
		} else {
			// look for single quotes and backslashes in the string and double
			// them up
			final char[] chars = originalString.toCharArray();
			final StringBuffer sb = new StringBuffer();
			for (final char d : chars) {
				final String c = String.valueOf(d);
				sb.append(c);
				if (c.equalsIgnoreCase("'")) {
					sb.append("'");
				}
				if (c.equalsIgnoreCase("\\")) {
					sb.append("\\");
				}
			}
			return sb.toString();
		}
	}

	public static String stripQuotes(String s) {
		if (s == null || s.isEmpty()) {
			return s;
		}
		if (s.startsWith("\"") && s.endsWith("\"")) {
			return s.substring(1, s.length() - 1);
		}
		return s;
	}

	/**
	 * @param totalTime
	 * @return
	 */
	public static String timeStatsToString(long totalTime) {
		final String format = String.format("%%0%dd", 2);
		final String millis = String.format(format, totalTime % 1000);
		totalTime = totalTime / 1000;
		final String seconds = String.format(format, totalTime % 60);
		final String minutes = String.format(format, (totalTime % 3600) / 60);
		final String hours = String.format(format, totalTime / 3600);
		return hours + ":" + minutes + ":" + seconds + ":" + millis;
	}

	public static boolean isEmpty(final String string) {
		if (string == null) {
			return true;
		}
		if (string.trim().length() < 1) {
			return true;
		}
		return false;
	}

	/**
	 * Returns a boolean value indicating whether or not all provided strings
	 * are empty.
	 * 
	 * @param str
	 *            The string (or strings) to check.
	 * @return When all provided strings are null or "blank" returns true,
	 *         otherwise false.
	 */
	public static boolean areEmpty(final String... str) {
		if (str.length == 0)
			return true;

		boolean empty = true;
		for (String item : str) {
			if (item != null && item.trim().length() > 0) {
				empty = false;
				break;
			}
		}

		return empty;
	}

	public static String removeHtmlEncodings(String in) {
		if (in == null) {
			return null;
		}
		final StringBuilder out = new StringBuilder();
		in = in.replaceAll("&lt;", "<");
		in = in.replaceAll("&gt;", ">");
		in = in.replaceAll("&quot;", "\"");
		in = in.replaceAll("&apos;", "\'");
		for (;;) {
			final int x = in.indexOf("&amp;#");
			if (x < 0) {
				out.append(in);
				break;
			}
			out.append(in.substring(0, x));
			in = in.substring(x + 6);
			final int y = in.indexOf(";");
			if (y < 0) {
				out.append(in);
				break;
			}
			// final String tmp = in.substring(0, y);
			// int value = -1;
			try {
				// value = Integer.parseInt(tmp);
				// out.append((char)value);
				out.append(" ");
			} catch (final Exception e) {
				// We can't complete the substitution, so just restore the
				// string and move on.
				out.append("&amp;#");
				continue;
			}
			in = in.substring(y + 1);
		}
		final String rtn = out.toString().replaceAll("&amp;", "&");
		return rtn;
	}

	/**
	 * Returns a boolean value indicating whether or not any of the provided
	 * strings are empty.
	 * 
	 * @param str
	 *            The string (or strings) to check.
	 * @return When any of the provided strings are null or "blank" returns
	 *         true, otherwise false.
	 */
	public static boolean containsEmptyString(final String... str) {
		if (str.length == 0)
			return false;

		boolean containsEmptyString = false;
		for (String item : str) {
			if (item == null || item.trim().length() == 0) {
				containsEmptyString = true;
				break;
			}
		}

		return containsEmptyString;
	}

	public static boolean isEmpty(final String string[]) {
		if (string == null) {
			return true;
		}
		if (string.length < 1)
			return true;
		return false;
	}

	public static String arrayToString(final List<?> list, final String delimeter) {
		if (list == null)
			return "";
		final StringBuffer result = new StringBuffer();
		boolean first = true;
		if (list.size() > 0) {
			for (final Object entry : list) {
				if (!first) {
					if (delimeter != null) {
						result.append(delimeter);
					}
				}
				first = false;
				result.append(entry.toString().trim().replaceAll("\t", ",").replaceAll("\n", ","));
			}
		}
		return result.toString();
	}

	// Removes all characters except those you would find in a floating point
	// value
	public static String stripToNumber(String s) {
		final String keepChars = ".0123456789";
		s = s.toLowerCase().replaceAll(" ", "");
		String result = "";
		for (int i = 0; i < s.length(); i++) {
			if (keepChars.indexOf(s.charAt(i)) >= 0) {
				result += s.charAt(i);
			}
		}
		return result;
	}

	public static String detectEncoding(final String input) throws java.io.IOException {
		final byte[] buf = input.getBytes();
		final UniversalDetector detector = new UniversalDetector(null);
		detector.handleData(buf, 0, buf.length);
		detector.dataEnd();
		return detector.getDetectedCharset();
	}

	public static String detectAndRemoveBom(String line) {
		if (line.startsWith(UTF8_BOM)) {
			line = line.substring(1);
		}
		return line;
	}

	public static String decode(final String input) throws IOException {
		// String encoding = detectEncoding(input);
		// if (encoding == null) {
		// encoding = "UTF-8";
		// }
		final String decoded = new String(input.getBytes(), Charset.forName("UTF-8"));
		return decoded;
	}

	public static int countOccurrences(final String source, final String target) {
		int ctr = -1;
		int total = 0;
		while (true) {
			if (ctr == -1) {
				ctr = source.indexOf(target);
			} else {
				ctr = source.indexOf(target, ctr);
			}

			if (ctr == -1) {
				break;
			} else {
				total++;
				ctr += target.length();
			}
		}
		return total;
	}

	public static String removeSplCharsPrefix(String input) {
		StringBuffer buffer = new StringBuffer();

		char[] urlArr = input.toCharArray();
		boolean isFirst = true;
		for (char i : urlArr) {
			// if not alphanum
			if ((65 <= i && i <= 90) || (97 <= i && i <= 122) || (48 <= i && i <= 57)) {
				buffer.append(i);
				isFirst = false;
			} else {
				if (isFirst) {// do nothing
				} else {
					buffer.append(i);
				}
			}
		}
		return buffer.toString();
	}

	public static int countOccurrences(final String haystack, final char needle) {
		int count = 0;
		for (int i = 0; i < haystack.length(); i++) {
			if (haystack.charAt(i) == needle) {
				count++;
			}
		}
		return count;
	}

	public static List<Integer> findAll(final String source, final String target) {
		List<Integer> result = null;
		int index = -1;
		index = source.indexOf(target, index);
		while (index != -1) {
			if (result == null) {
				result = new ArrayList<Integer>();
			}
			result.add(index);
			index += target.length();
			index = source.indexOf(target, index);
		}
		return result;
	}

	public static String convertStreamToString(final InputStream is) throws Exception {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		final StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		return sb.toString();
	}

	public static String toDelimitedString(final Collection<String> list) {
		final StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (final String role : list) {
			if (first) {
				first = false;
			} else {
				builder.append(",");
			}
			builder.append(role);
		}
		return builder.toString();
	}

	public static Set<String> parseDelimitedStringToSet(final String role) {
		final String[] str = role.split(",");
		final Set<String> rtn = new TreeSet<String>();
		for (final String part : str) {
			rtn.add(part.trim());
		}
		return rtn;
	}

	public static boolean equals(final String thisS, final String thatS) {
		if (thisS == null && thatS == null) {
			return true;
		}
		return (thisS != null && thisS.equals(thatS));
	}

	public static String parse(String inputString, String startToken, String endToken) {
		int i = 0;
		if (startToken != null) {
			i = inputString.indexOf(startToken);
			inputString = inputString.substring(i + startToken.length());
		}
		if (endToken == null)
			return inputString;
		i = inputString.indexOf(endToken);
		if (i > 0)
			return inputString.substring(0, i);
		return inputString;
	}

	public static String toDelimitedString(String[] list, String delimiter) {
		if (list == null)
			return "";
		final StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (final String role : list) {
			if (StringUtils.isEmpty(role))
				continue;
			if (first) {
				first = false;
			} else {
				builder.append(delimiter);
			}
			builder.append(role);
		}
		return builder.toString();
	}

	public static String fixCase(String in) {
		if (in == null)
			return null;
		if (in.length() < 1)
			return "";
		StringBuilder rtn = new StringBuilder();
		int x = in.indexOf("&");
		if (x > 0) {
			do {
				rtn.append(in.substring(0, x));
				in = in.substring(x + 1);
				if (in.length() > 0) {
					if (in.charAt(0) != ' ') {
						rtn.append(' ');
					}
					rtn.append('&');
					if (in.charAt(0) != ' ') {
						rtn.append(' ');
					}
				}
				x = in.indexOf("&");
			} while (x > 0);
			in = rtn.toString() + in;
			rtn = new StringBuilder();
		}
		boolean first = true;
		for (String word : in.split(" ")) {
			if (first) {
				first = false;
			} else {
				rtn.append(" ");
			}
			if (word.length() > 1) {
				rtn.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
			} else {
				rtn.append(word);
			}
		}
		return rtn.toString();

	}

	public static boolean isEmpty(List<?> list) {
		if (list == null)
			return true;
		return list.size() == 0;
	}

	public static String[] split(String string, String delimiter) {
		if (string != null)
			return string.split(delimiter);
		return null;
	}

	public static List<String> parseDelimitedString(String string, String delimiter) {
		List<String> rtn = new ArrayList<String>();
		String parts[] = string.split(delimiter);
		for (String part : parts) {
			rtn.add(part.trim());
		}
		return rtn;
	}

	public static String[] toLowerCase(String[] str) {
		List<String> l = new ArrayList<String>(str.length);
		for (String s : str) {
			l.add(s.toLowerCase());
		}
		return l.toArray(new String[0]);
	}

	public static String arrayToString(String[] list, String delimeter) {
		if (list == null)
			return "";
		final StringBuffer result = new StringBuffer();
		boolean first = true;
		if (list.length > 0) {
			for (final String entry : list) {
				if (!first) {
					if (delimeter != null) {
						result.append(delimeter);
					}
				}
				first = false;
				result.append(entry.trim().replaceAll("\t", ",").replaceAll("\n", ","));
			}
		}
		return result.toString();
	}

	public static boolean contains(String[] category, String string) {
		if (category == null)
			return false;
		for (String str : category) {
			if (str.toLowerCase().contains(string.toLowerCase()))
				return true;
		}
		return false;
	}

	public static String zeroPad(boolean start, String str, int totalLength) {
		StringBuilder builder = new StringBuilder(str);
		for (int x = str.length(); x < totalLength; x++) {
			if (start) {
				builder.insert(0, "0");
			} else {
				builder.append("0");
			}
		}
		return builder.toString();
	}

	/**
	 * Pads a string or clips it to the specified length. <br>
	 * If the string is clipped, it clips off the end of the string <br>
	 * If the string is padded, it pads at the beginning on the string
	 * 
	 * @param source
	 *            String to modify
	 * @param padChar
	 *            The character to use when padding the string
	 * @param size
	 *            The desired size of the final string
	 * @return padded or clipped string
	 */
	public static String padOrClipToSize(String source, char padChar, int size) {
		if (source.length() > size) {
			int clipAmt = source.length() - size;
			return source.substring(clipAmt);
		}
		StringBuilder sb = new StringBuilder(source);
		sb.reverse();
		int padAmt = size - source.length();
		for (int i = 0; i < padAmt; i++) {
			sb.append(padChar);
		}
		return sb.reverse().toString();
	}

	public static boolean isAllUpperCase(String s) {
		if (s == null)
			return false;
		for (int x = 0; x < s.length(); x++) {
			if (Character.isLetter(s.charAt(x)) && Character.isLowerCase(s.charAt(x))) {
				return false;
			}
		}
		return true;
	}

	// Split a line by commons but avoid commons in double quotes. Applies to
	// all delimiters
	public static String[] splitCsv(String line, String delimiter) {
		String otherThanQuote = " [^\"] ";
		String quotedString = String.format(" \" %s* \" ", otherThanQuote);
		String regex = String.format(
				"(?x) " + // enable comments, ignore white spaces
						delimiter// match a comma (delimiter)
						+ "(?=" // start positive look ahead
						+ "(" // start group 1
						+ "%s*" // match 'otherThanQuote' zero or more times
						+ "%s" // match 'quotedString'
						+ ")*" // end group 1 and repeat it zero or more times
						+ "%s*" // match 'otherThanQuote'
						+ "$" // match the end of the string
						+ ")", // stop positive look ahead
				otherThanQuote, quotedString, otherThanQuote);

		String[] columns = line.split(regex, -1);
		for (int i = 0; i < columns.length; i++) {
			columns[i] = StringEscapeUtils.unescapeCsv(columns[i]);
		}
		return columns;
	}

	public static void main(String[] args) {
		float a  = (float) 34.56;
		System.out.println(String.valueOf(a));
	}

}
