package bitrate16.JCobe.Types;

import java.util.HashMap;

public interface Library {
	/** Might return all Object's **/
	public HashMap<String, bitrate16.JCobe.Types.AbstractObject> getObjects();

	/** Might return all Functions **/
	public HashMap<String, bitrate16.JCobe.Types.Function> getFunctions();
}
