package bitrate16.JCobe.Types;

import java.util.List;

import bitrate16.JCobe.Values.Value;

/**
 * Used for creating an instance of Object
 * 
 * @author bitrate16
 *
 */
public class RawConstructor {
	// List of all arguments
	public List<String> args;
	// List of all Statements
	public Block constructorBody;

	public RawConstructor(List<String> args, Block constructorBody) {
		this.args = args;
		this.constructorBody = constructorBody;
	}

	/**
	 * Execute constructor for object
	 */
	public void execute(List<Value> args, Block objectBlock) {

	}
}
