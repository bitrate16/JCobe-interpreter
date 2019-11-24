package bitrate16.JCobe.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Expressions.Expression;
import bitrate16.JCobe.Statements.Statement;
import bitrate16.JCobe.Utils.JavaUtils;
import bitrate16.JCobe.Values.Value;

/**
 * Structure prototype - prototype of the future object. can be used for
 * creating Objects from raw construction.
 * 
 * @author bitrate16
 *
 */
public class StructurePrototype implements Function, Expression, Statement {

	private HashMap<String, Expression> objects;
	private String name;
	// Intherit structures
	private List<String> superStructs;
	// Execution context. CONSTANT
	public Block context;

	public StructurePrototype(String name, HashMap<String, Expression> objects) {
		this(name, new ArrayList<String>(), objects);
	}

	public StructurePrototype(String name, List<String> superStructures, HashMap<String, Expression> objectValues) {
		this.name = name;
		superStructs = superStructures;
		objects = objectValues;
	}

	@Override
	public Value call(Block block, Value... args) {
		// Return new Object from prototype
		CodeObject result = new CodeObject(context);
		Block b = result.context;
		// Parents
		final List<Value> parentStructs = new ArrayList<Value>();

		// Put all extended fields
		for (String superStruct : superStructs) {
			Value v = b.getValue(superStruct);

			if (v == null || !(v.value instanceof StructurePrototype))
				continue;

			parentStructs.add(v);
			StructurePrototype sp = (StructurePrototype) v.value;

			Value object = sp.call(b, args);
			CodeObject co = (CodeObject) object.value;

			result.context.localStorage.putAll(co.context.localStorage); // LOL
		}

		// Put Overriden values
		result.context.localStorage.putAll(JavaUtils.execute(objects, b));

		final StructurePrototype instance = this;
		// Add link to self
		result.context.localStorage.put("getStruct", new Value(new Function() {
			@Override
			public Value call(Block b, Value... args) {
				return new Value(instance);
			}
		}));

		// Add link to SuperStructs
		result.context.localStorage.put("getParentStructs", new Value(new Function() {
			@Override
			public Value call(Block b, Value... args) {
				return new Value(new ArrayObject(parentStructs));
			}
		}));

		// Add link to class name
		result.context.localStorage.put("getStructName", new Value(new Function() {
			@Override
			public Value call(Block b, Value... args) {
				return new Value(instance.name);
			}
		}));

		return new Value(result);
	}

	@Override
	public void execute(Block b) {
		this.context = b;
		b.putValue(name, new Value(this));
	}

	@Override
	public Value evaluate(Block b) {
		this.context = b;
		return new Value(this);
	}
}
