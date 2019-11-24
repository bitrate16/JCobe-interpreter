import bitrate16.JCobe.JCobe;
import bitrate16.JCobe.Utils.FileUtils;

public class Test {
	public static void main(String[] args) {// XXX: Add byte
		String code = FileUtils.read("scripts/coding/code.jc");

		JCobe jcobe = new JCobe();

		jcobe.execute(code);
	}
}
