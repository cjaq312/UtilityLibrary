package com.jagan.utilitylibrary;

public class PhoneUtils {

	public static String stripFormatting(String phone) {
		String result = phone;
		if (!StringUtils.isEmpty(phone)) {
			result = phone.replaceAll("[\\.\\s\\-()]", "");
		}
		return result;
	}

	public static void main(String[] args) {
		String phone1 = "(213) 555-1212";
		String phone2 = "(213) 555.1212";
		System.out.println(stripFormatting(phone1));
		System.out.println(stripFormatting(phone2));
	}
}
