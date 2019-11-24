package bitrate16.JCobe.Syntax;

import java.util.ArrayList;

import bitrate16.JCobe.Utils.StringUtils;

/**
 * Does pre-processing of source code (remove comments, e.t.c.)
 * 
 * @author bitrate16
 *
 */
public class PreProcessor {
	/**
	 * Multi-Line Preprocessing
	 * 
	 * @param code
	 */
	public static String preprocess(String code) {
		// Remove Single-Line Comments
		ArrayList<String> lines = StringUtils.splitByLines(code);
		ArrayList<String> newLines = new ArrayList<String>();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			int commentIndex = line.indexOf("//");
			if (commentIndex != -1)
				line = line.substring(0, commentIndex).trim();
			if (!line.isEmpty())
				newLines.add(line);
		}
		code = StringUtils.concate(newLines, "\n");

		// Remove Multi-Line comments
		code = code.replaceAll("(\\/\\*([\\s\\S]*?)\\*\\/)", "").trim();
		return code;
	}

	/**
	 * Single-Line preprocessing
	 */
	public static String preprocessLine(String line) {
		return preprocess(line);
	}
}
