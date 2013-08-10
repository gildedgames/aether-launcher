package net.aetherteam.aether.launcher.utils;

import java.util.Map;

public class StrSubstitutor {

	private Map<String, String> map;

	public StrSubstitutor(Map<String, String> map) {
		this.map = map;
	}

	public String replace(String str) {
		StringBuffer sb = new StringBuffer();
		char[] strArray = str.toCharArray();
		int i = 0;
		while (i < (strArray.length - 1)) {
			if ((strArray[i] == '$') && (strArray[i + 1] == '{')) {
				i = i + 2;
				int begin = i;
				while (strArray[i] != '}') {
					++i;
				}
				sb.append(this.map.get(str.substring(begin, i++)));
			} else {
				sb.append(strArray[i]);
				++i;
			}
		}
		if (i < strArray.length) {
			sb.append(strArray[i]);
		}
		return sb.toString();
	}
}
