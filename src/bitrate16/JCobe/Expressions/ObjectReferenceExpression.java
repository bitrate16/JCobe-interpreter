package bitrate16.JCobe.Expressions;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Types.JavaObject;
import bitrate16.JCobe.Values.Value;

/**
 * Returns value from an Object
 * 
 * @author bitrate16
 *
 */
public class ObjectReferenceExpression implements Expression {

	public Expression object;
	public String name;

	public ObjectReferenceExpression(Expression object, String name) {
		this.object = object;
		this.name = name;
	}

	@Override
	public Value evaluate(Block b) {
		if (object == null) {
			if (name.equals("this")) // Reference to Object's context
				return b.getObjectContext();
			
			// From local context
			Value v = b.getValue(name);
			// System.err.println("Requested: " + name + ", got: " +
			// v);if(b.getValue("Java").value == null )throw new
			// RuntimeException();
			if (v == null) // XXX: Throw Exception
				return new Value();
			return v;
		}
		// From object context

		Value v = object.evaluate(b);
		if (v.value == null)
			return new Value();

		switch (v.type) {
			case OBJECT:
				bitrate16.JCobe.Types.AbstractObject o = (bitrate16.JCobe.Types.AbstractObject) v.value;
				v = o.getProperty(name);
				if (v == null) // XXX: Throw Exception
					return new Value();
				return v;
			default:
				// Java Object, number, e.t.c
				JavaObject jo = new JavaObject(v.value);
				v = jo.getProperty(name);
				if (v == null)
					return new Value();
				return v;
		}
		// Value v = object.evaluate(b);
		//
		// if (v == null || (v.type != Type.OBJECT && v.type != Type.STRING) ||
		// v.value == null)
		// return new Value();
		//
		// if (v.type == Type.STRING) // String have it's own methods
		// v = new JavaObject(v.value).getProperty(name);
		// else
		// v = ((bitrate16.JCobe.Types.AbstractObject)
		// v.value).getProperty(name);
		// if (v == null)
		// return new Value();
		//
		// return v;
	}
}
