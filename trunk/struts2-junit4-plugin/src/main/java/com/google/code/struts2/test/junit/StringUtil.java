package com.google.code.struts2.test.junit;


public class StringUtil {

	public static String unsplit(String separator, String[] stringArray) {
		if (stringArray.length > 0) {
			String unsplitString = stringArray[0];
			for (int i = 1; i < stringArray.length; i++) {
				unsplitString += separator + stringArray[i];
			}
			return unsplitString;
		} else {
			return null;
		}
	}

}
