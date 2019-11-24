package bitrate16.JCobe.Utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class FileUtils {
	/**
	 * Does Reading file
	 */
	public static String read(String path) {
		String content = null;
		File file = new File(path);
		FileReader reader = null;
		try {
			reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return content;
	}

	/** Writes file string into given file **/
	public static boolean writeFile(String path, String data) {
		try {
			PrintWriter writer = new PrintWriter(path, "UTF-8");
			writer.print(data);
			writer.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/** Checks if File exists on disk **/
	public static boolean exist(String path) {
		return new File(path).exists();
	}

	/** Creates new File on path **/
	public static void mkfile(String path) {
		File file = new File(path);
		File parent = file.getParentFile();
		if (parent != null) {
			parent.mkdirs();
			parent.mkdir();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
		}
	}

	/** Creates new File Directory on path **/
	public static void mkdir(String path) {
		File file = new File(path);
		File parent = file.getParentFile();
		if (parent != null) {
			parent.mkdirs();
			parent.mkdir();
		}
	}

	/** Deletes given file **/
	public static void delete(String path) {
		if (exist(path))
			new File(path).delete();
	}
}
