package bitrate16.JCobe.Utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
	// Splits code by Lines
	public static ArrayList<String> splitByLines(String code) {
		ArrayList<String> lines = new ArrayList<String>();
		String[] linesArray = code.split("\n");
		for (int i = 0; i < linesArray.length; i++) {
			lines.add(linesArray[i].trim());
		}
		return lines;
	}

	// Concates String With Surrounder & Delimiter
	public static String concate(List<String> parts, String delimiter, String surrounder_left,
			String surrounder_right) {
		String ret = "";
		for (int i = 0; i < parts.size(); i++) {
			ret += surrounder_left + parts.get(i) + surrounder_right;
			if (i < parts.size() - 1)
				ret += delimiter;
		}
		return ret;
	}

	// Concates String With Delimiter
	public static String concate(List<String> parts, String delimiter) {
		String ret = "";
		for (int i = 0; i < parts.size(); i++) {
			ret += parts.get(i);
			if (i < parts.size() - 1)
				ret += delimiter;
		}
		return ret;
	}

	// Concates String With Surrounder & Delimiter
	public static String concate(List<String> parts, int start, int end, String delimiter, String surrounder_left,
			String surrounder_right) {
		return concate(parts.subList(start, end), delimiter, surrounder_left, surrounder_right);
	}

	// Concates String With Delimiter
	public static String concate(List<String> parts, int start, int end, String delimiter) {
		return concate(parts.subList(start, end), delimiter);
	}

	/** Util. Check if String is Number **/
	public static boolean isNumber(String number) {
		return number.matches("[-+]{0,1}[0-9]+");
	}

	public static boolean isPositiveNumber(String number) {
		return number.matches("[+]{0,1}[0-9]+");
	}

	public static int parseNumber(String number) {
		return Integer.parseInt(number);
	}

	/** Returns true if left number > right number **/
	public static boolean greater(String number1, String number2) {
		if (number1.startsWith("-") && number2.startsWith("-")) {
			number1 = number1.replace("-", "");
			number2 = number2.replace("-", "");

			if (number1.length() > number2.length())
				return false;
			else if (number1.length() < number2.length())
				return true;

			for (int i = 0; i < number1.length(); i++)
				if (number1.charAt(i) > number2.charAt(i))
					return false;
				else if (number1.charAt(i) < number2.charAt(i))
					return true;
		}

		if (!number1.startsWith("-") && !number2.startsWith("-")) {
			if (number1.length() > number2.length())
				return true;
			else if (number1.length() < number2.length())
				return false;

			for (int i = 0; i < number1.length(); i++)
				if (number1.charAt(i) > number2.charAt(i))
					return true;
				else if (number1.charAt(i) < number2.charAt(i))
					return false;
		}
		return !number1.startsWith("-");
	}

	/** Checks if given number representation is long **/
	public static boolean isLong(String number) {
		return number.matches("([0-9]*(l|L))") // Long preffix
				|| greater(number, "" + Integer.MAX_VALUE) // Maximal int
															// overflow
				|| greater("-" + Integer.MAX_VALUE, number); // Minimal int
																// downflow
	}

	/** Checks if given number representation is byte **/
	public static boolean isByte(String number) {
		return number.matches("(([0-9]{1,3}(b|B))|(0[xX][a-fA-F0-9]{2}))");
	}

	/** Parses byte **/
	public static byte parseByte(String number) {
		if (number.endsWith("B") || number.endsWith("b")) {
			return (byte) Integer.parseInt(number.substring(0, number.length() - 1));
		} else if (number.charAt(1) == 'x' || number.charAt(1) == 'X') {
			return (byte) Integer.parseInt(number.substring(2));//Byte.parseByte(number.substring(0, number.length() - 2));
		}
		return Byte.parseByte(number);
	}

	/** Parse Long **/
	public static long parseLong(String number) {
		return Long.parseLong(number.replace("L", "").replace("l", ""));
	}
}
