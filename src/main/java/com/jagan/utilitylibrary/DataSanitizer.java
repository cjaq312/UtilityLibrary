package com.jagan.utilitylibrary;

public class DataSanitizer {

	public DataSanitizer() {
	}

	public static String cleanField(String field) {
		return WebElementValidatorUtil.cleanField(field);
	}

	public static String cleanImage(String image) {
		return WebElementValidatorUtil.cleanImage(image);
	}

	public static String cleanURL(String url) {
		return WebElementValidatorUtil.cleanURL(url);
	}

	public static float cleanPrice(String price) {
		price = price.replaceAll("\"", "");
		float result = 0;
		try {
			result = Float.parseFloat(price);
		} catch (Exception e) {
			return 0;
		}
		return result;
	}

}
