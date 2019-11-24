package bitrate16.JCobe.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import bitrate16.JCobe.Exceptions.ExecutionException;
import bitrate16.JCobe.Statements.Statement;
import bitrate16.JCobe.Values.Value;

/**
 * Created on program parsing
 *
 * RAW Class - prototype of object. same as in java
 * 
 * @author bitrate16
 *
 */
public class RawClass {
	// Store all Objects (Variables & functions)
	public HashMap<String, Value> objects;

	// Store all constructors
	public ArrayList<RawConstructor> constructors;

	// Store all static (on class creation executable) statements
	public ArrayList<Statement> staticStatements;

	// Store Object name
	public String name;

	/**
	 * Create new RawCLass from presented args
	 */
	public RawClass(String name, HashMap<String, Value> objects, ArrayList<RawConstructor> constructors,
			ArrayList<Statement> staticStatements) {
		this.name = name;
		this.objects = objects;
		this.constructors = constructors;
		this.staticStatements = staticStatements;
	}

	/** Create new Instance of Object */
	public Object newInstance(Value... args) {
		// 1. Find constructor with same args count
		RawConstructor rConstructor = getRawConstructor(args.length);

		if (rConstructor == null)
			throw new ExecutionException("Can't find constructor with " + args.length + " arguments");

		// 2. Create copy of all objects
		HashMap<String, Value> objectsCopy = new HashMap<String, Value>();

		// Iterator list
		ArrayList<String> nameIterator = new ArrayList<String>(objects.keySet());

		for (int i = 0; i < nameIterator.size(); i++) {
			objectsCopy.put(nameIterator.get(i), objects.get(nameIterator.get(i)));
		}

		// 3. Initialize new Object instance
		CodeObject obj = new CodeObject(objectsCopy);

		// 4. Invoke static statements
		for (int i = 0; i < staticStatements.size(); i++)
			staticStatements.get(i).execute(obj.block);

		// 5. Invoke Constructor
		rConstructor.execute(Arrays.<Value> asList(args), obj.block);

		// Return
		return obj;
	}

	// - - - - - - - - - - - - - - - - - Util-Methods - - - - - - - - - - - - -
	// - - - -
	/**
	 * Returns constructor by arguments count
	 */
	public RawConstructor getRawConstructor(int argCount) {
		for (int i = 0; i < constructors.size(); i++)
			if (constructors.get(i).args.size() == argCount)
				return constructors.get(i);
		return null;
	}
}
