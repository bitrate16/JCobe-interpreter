package bitrate16.JCobe.Types;

import bitrate16.JCobe.Enviroment.Block; 
import bitrate16.JCobe.Values.Value;

public interface Function {
	/** Call function with presented arguments (and context) **/
	public Value call(Block b, Value... args);
}
