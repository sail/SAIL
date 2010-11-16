package uk.ac.ebi.sail.client.ui;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

public class StringUtils {

	/*
	 * http://forums.sun.com/thread.jspa?threadID=506456
	 */
	static String[] wrapText2Array(String text, int len) {
		// return empty array for null text
		if (text == null) {
			return new String[] {};
		}

		// return text if len is zero or less
		if (len <= 0) {
			return new String[] { text };
		}

		// return text if less than length
		if (text.length() <= len) {
			return new String[] { text };
		}

		char[] chars = text.toCharArray();
		Vector lines = new Vector();
		StringBuffer line = new StringBuffer();
		StringBuffer word = new StringBuffer();

		for (int i = 0; i < chars.length; i++) {
			word.append(chars[i]);

			if (chars[i] == ' ') {
				if ((line.length() + word.length()) > len) {
					lines.add(line.toString());
					line.delete(0, line.length());
				}

				line.append(word);
				word.delete(0, word.length());
			}
		}

		// handle any extra chars in current word
		if (word.length() > 0) {
			if ((line.length() + word.length()) > len) {
				lines.add(line.toString());
				line.delete(0, line.length());
			}
			line.append(word);
		}

		// handle extra line
		if (line.length() > 0) {
			lines.add(line.toString());
		}

		String[] ret = new String[lines.size()];
		int c = 0; // counter
		for (Enumeration e = lines.elements(); e.hasMoreElements(); c++) {
			ret[c] = (String) e.nextElement();
		}

		return ret;
	}

	// JM
	public static String wrapText(String text, int len, String lineBreak) {

		if (text == null)
			throw new RuntimeException(
					"Error in wraping the string.. strinmg is null");
		if (text.length() == 0)
			return "";

		text = text.replaceAll("\\s+", " ");

		String[] arr = wrapText2Array(text, len);

		String res = "";
		if (arr == null || arr.length == 0)
			throw new RuntimeException("Error in wraping the string..");

		//hack around a possible bug if first word longer than len
		int first = 0;
		if ( arr[0].length() == 0) {
			first++;
			if ( arr.length == 1) { 
				return text; // we should not come here..
			}
		}
		res = arr[ first];
		
		for (int i = first + 1; i < arr.length; i++) {
			res = res + lineBreak + arr[i].trim();
		}
		return res;
	}

	public static void main(String[] args) {

		System.out.println(wrapText(
				"this is long text string and I would like to wrapit ", 3,
				"<br/>"));
		System.out.println(wrapText(
				"this is long text string and I would like to wrapit ", 5,
				"<br/>"));
		System.out.println(wrapText(
				"this is long text string and I would like to wrapit ", 10,
				"<br/>"));

		System.out.println(wrapText(
				"this is long text string and I would like to wrapit ", 20,
				"<br/>"));
		System.out.println(wrapText(
				"this is long text string and I would like to wrapit ", 200,
				"<br/>"));
		System.out
				.println(wrapText(
						"thisxsssssssss is long text string and I would like to wrapit ",
						10, "<br/>"));
		System.out.println(wrapText(
				"circumference of waist (physical finding)", 10, "<br/>"));
		System.out.println(wrapText(
				"circumference circumference circumference     circumference",
				10, "<br/>"));
		System.out.println(wrapText(
				"circumference",
				10, "<br/>"));
		System.out.println(wrapText(
				"circumference ",
				10, "<br/>"));
		System.out.println(wrapText(
				"circumference circumference circumference circumference", 5,
				"<br/>"));
		System.out.println(wrapText("x gemaless inf asasass", 10, "<br/>"));
		System.out.println(wrapText("x gemal inf asasass", 10, "<br/>"));
		System.out.println(wrapText("", 5, "<br/>")); 

	}
}
