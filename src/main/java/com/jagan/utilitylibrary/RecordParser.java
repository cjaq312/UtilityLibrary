package com.jagan.utilitylibrary;

import java.util.ArrayList;
import java.util.List;

public class RecordParser {

	public static List<String> parse(String toBeParsed) {
		List<String> tokensList = new ArrayList<String>();
		if (toBeParsed.isEmpty() || toBeParsed == null)
			return tokensList;
		boolean inQuotes = false;
		StringBuilder b = new StringBuilder();
		for (char c : toBeParsed.toString().toCharArray()) {
			switch (c) {
			case ',':
				if (inQuotes) {
					b.append(c);
				} else {
					tokensList.add(b.toString());
					b = new StringBuilder();
				}
				break;
			case '\"':
				inQuotes = !inQuotes;
			default:
				b.append(c);
				break;
			}
		}
		tokensList.add(b.toString());
		return tokensList;
	}

}
