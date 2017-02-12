package com.jagan.utilitylibrary;

public class WebElementValidatorUtil {

	/**
	 * Removes all html tags. Preserves logical spacing. Converts html escaping
	 * chars back to plain text in the process.
	 */
	public static String removeHtmlTags(final String in) {
		if (StringUtils.isEmpty(in))
			return in;
		return HtmlToText.convert(in);
	}

	public static String cleanURL(String url) {
		if (StringUtils.isEmpty(url))
			return url;
		url = StringUtils.removeSplCharsPrefix(url);
		if (!url.startsWith("http")) {
			if (!url.startsWith("www"))
				return "http:://www." + url;
			else
				return "http:://" + url;
		}
		return url;
	}

	public static String cleanImage(String image) {
		image = cleanURL(image);
		return image;
	}

	public static String cleanField(String field) {
		field = removeHtmlTags(field);
		field = removeInvalidXMLChars(field);
		return field;
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

	public static String removeInvalidXMLChars(final String source) {
		if (StringUtils.isEmpty(source))
			return source;
		final StringBuffer out = new StringBuffer();
		char current;

		if (StringUtils.isEmpty(source)) {
			return "";
		}
		for (int i = 0; i < source.length(); i++) {
			current = source.charAt(i);
			if ((current == 0x9) || (current == 0xA) || (current == 0xD) || ((current >= 0x20) && (current <= 0xD7FF))
					|| ((current >= 0xE000) && (current <= 0xFFFD))
					|| ((current >= 0x10000) && (current <= 0x10FFFF))) {
				out.append(current);
			}
		}
		return out.toString();
	}

}
