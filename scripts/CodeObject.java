package bitrate16.JCobe.Types;

import java.util.HashMap;

import bitrate16.JCobe.Exceptions.ExecutionException;
import bitrate16.JCobe.Values.Value;
import bitrate16.JCobe.Values.Value.Type;

/**
 * Object, Created directly from code
 * 
 * @author bitrate16
 *
 */
public class CodeObject extends Object {
	// Local context block
	protected Block block;

	public CodeObject(HashMap<String, Value> objects) {
		block = new Block();
		block.localVariables = objects;
	}

	@Override
	public Value callFunction(String name, Value... args) {
		// Find in objects & check type
		Value v = block.localVariables.get(name);

		if (v == null || v.type != Type.FUNCTION)
			throw new ExecutionException("Function " + name + " not found in the Table");

		return ((Function) v.value).call(block, args);
	}

	@Override
	public Value getVariable(String name) {
		// Find in objects & check type
		Value v = block.localVariables.get(name);

		if (v == null || (v.type != Type.OBJECT && v.type != Type.JAVA_OBJECT))
			throw new ExecutionException("Variable " + name + " not found in the Table");

		return v;
	}
}
