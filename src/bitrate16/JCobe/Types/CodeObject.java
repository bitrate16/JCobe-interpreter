package bitrate16.JCobe.Types;

import java.util.ArrayList;
import java.util.HashMap;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Expressions.Expression;
import bitrate16.JCobe.Statements.Statement;
import bitrate16.JCobe.Utils.JavaUtils;
import bitrate16.JCobe.Utils.Temp;
import bitrate16.JCobe.Values.Value;
import bitrate16.JCobe.Values.Value.Type;

/**
 * Implementation of the AbstractObject.
 * 
 * <pre>
 * [$string] - reference to an Object
 * self.$keyword - reference to an Object
 * </pre>
 * 
 * @author bitrate16
 *
 */
public class CodeObject extends AbstractObject {
	protected Block context;
	private Block parent; // Global, static context
	public AbstractObject instance = this;

	public CodeObject(Block parent, HashMap<String, Expression> objects) {
		context = new Block() {
			@Override
			public Value getObjectContext() {
				return new Value(instance);// Override context access
			}
		};
		// Parenthese
		context.parent = parent;
		// Clone parent into here
		// Will execute in Block's context
		context.putAllForcibly(JavaUtils.execute(objects, context));
		// System.out.println("PARENT: : : : :: : : : :::: : :: : : : : : : : :
		// : ");
		// JavaUtils.printHashMap(parent.localStorage);
		// System.err.println("JAVA - - - - - - - - - - - - - - - - - - - - - -
		// - - - - - - - - - > " + parent.getValue("Java"));
		this.parent = parent;
	}

	protected CodeObject(Block parent) {
		this(parent, new HashMap<String, Expression>());
	}

	@Override
	public Value getValue(String name) {
		final Value v = JavaUtils.getMapValue(context.localStorage, name);
		// System.out.println("requested: " + name + ", got: " + v);
		if (v.type == Type.FUNCTION)
			return new Value(new Function() {
				@Override
				public Value call(Block block, final Value... args) {
					// Call function in fake context. Mix with block context
					final Temp<Value> ret = new Temp<Value>(new Value());

					context.execute(parent, new Statement() {
						@Override
						public void execute(Block b) {
							ret.value = ((Function) v.value).call(b, args);
						}
					});
					return ret.value;
				}
			});

		// Nothing
		return v;
	}

	@Override
	public Value setValue(String name, Value v) {
		// System.err.println("Change '" + name + "' from '" +
		// context.getValue(name) + "' to '" + v.asString() + "'");
		// copyContext.putValue(name, v);
		context.putForcibly(name, v);
		// System.err.println("CONTAINS AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA: " +
		// context.getValue("pint"));
		return v;
	}

	@Override
	public Value getProperty(String name) {
		if ("size".equals(name))
			return new Value(context.localStorage.size());
		if ("keys".equals(name))
			return new Value(
					new ArrayObject(JavaUtils.listToValueList(new ArrayList<String>(context.localStorage.keySet()))));
		// Clean all
		if ("clean".equals(name)) // Clean all
			return new Value(new Function() {
				@Override
				public Value call(Block b, Value... args) {
					// copyContext.localStorage.clear();
					context.localStorage.clear();
					return new Value();
				}
			});
		// Remove element
		if ("delete".equals(name))
			return new Value(new Function() {
				@Override
				public Value call(Block b, Value... args) {
					if (args.length < 1)
						return new Value();
					// copyContext.localStorage.remove(args[0].stringValue());
					return context.localStorage.remove(args[0].stringValue());
				}
			});
		// true if contains key
		if ("contains".equals(name))
			return new Value(new Function() {
				@Override
				public Value call(Block b, Value... args) {
					if (args.length < 1)
						return new Value(false);
					return new Value(context.localStorage.containsKey(args[0].stringValue()));
				}
			});

		return getValue(name);
	}

	@Override
	public Value setProperty(String name, Value v) {
		return setValue(name, v);
	}
}
