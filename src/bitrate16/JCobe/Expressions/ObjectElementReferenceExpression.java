package bitrate16.JCobe.Expressions;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Types.JavaObject;
import bitrate16.JCobe.Values.Value;

/**
 * Something like myObject[2]
 * 
 * @author bitrate16
 *
 */
public class ObjectElementReferenceExpression implements Expression {

	/** Object key **/
	public Expression key;
	public Expression object;

	public ObjectElementReferenceExpression(Expression object, Expression key) {
		this.object = object;
		this.key = key;
	}

	@Override
	public Value evaluate(Block b) {
		if (object == null)
			return new Value();

		Value v = object.evaluate(b);
		if (v.value == null)
			return new Value();

		switch (v.type) {
			case OBJECT:
				bitrate16.JCobe.Types.AbstractObject o = (bitrate16.JCobe.Types.AbstractObject) v.value;
				String name = key.evaluate(b).stringValue();
				v = o.getValue(name);
				if (v == null)
					return new Value();
				return v;
			default:
				// Java Object, number, e.t.c
				JavaObject jo = new JavaObject(v.value);
				name = key.evaluate(b).stringValue();
				v = jo.getValue(name);
				if (v == null)
					return new Value();
				return v;
		}
	}
}
