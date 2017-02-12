package com.jagan.utilitylibrary;

import java.util.Map;
import java.util.Map.Entry;

public class QueryGenerator {

	private static StringBuffer result = new StringBuffer();

	public static String generateConditions(Map<String, String> conditionsMap, String tableName) {
		int count = 1;
		if (result.length() > 0)
			result.delete(0, result.length());
		result.append("select * from ").append(tableName);
		if (conditionsMap.size() > 0)
			result.append(" where ");
		for (Entry<String, String> i : conditionsMap.entrySet()) {
			if (i.getKey().equals("startprice")) {
				result.append("price >= ").append(i.getValue());
			} else if (i.getKey().equals("endprice")) {
				result.append("price <= ").append(i.getValue());
			} else {
				result.append(i.getKey()).append(" = ").append(i.getValue());
			}
			if (count < conditionsMap.size())
				result.append(" and ");
			count++;
		}

		return result.toString();
	}

}
