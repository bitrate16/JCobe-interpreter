package bitrate16.JCobe.Expressions;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Values.Value;
import bitrate16.JCobe.Types.Object;
import bitrate16.JCobe.Utils.JavaUtils;;

/**
 * Returns Object in given Object by Object Path
 * 
 * If object == null, returns object from local context
 * 
 * @author bitrate16
 *
 */
public class ObjectReferenceExpression implements Expression {

	private Expression object;
	private String name;

	public ObjectReferenceExpression(Expression object, String name) {
		this.object = object;
		this.name = name;
	}

	@Override
	public Value evaluate(Block b) {
		if (object == null) {

			Value v = b.getValue(name);

			if (v == null)
				return new Value();

			return v;
		} else {
			Value obj = object.evaluate(b);

			switch (obj.type) {
				case OBJECT: {
					if (obj.value == null)
						return new Value();

					Value v = ((Object) obj.value).getValue(name);

					if (v == null)
						return new Value();

					return v;
				}
				case JAVA_OBJECT: {
					if (obj.value == null)
						return new Value();

					return new Value(JavaUtils.getField(name, obj.value));
				}
				default:
					return new Value();
			}
		}
	}
}
