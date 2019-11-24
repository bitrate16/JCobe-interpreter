package bitrate16.JCobe.Expressions;

import java.util.ArrayList;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Values.Value;
import bitrate16.JCobe.Values.Value.Type;
import bitrate16.JCobe.Types.Function;
import bitrate16.JCobe.Types.Object;
import bitrate16.JCobe.Utils.JavaUtils;;

/**
 * Calls function of the given Object. If Object == null, executes function from
 * local Context
 * 
 * @author bitrate16
 *
 */
public class FunctionReferenceExpression implements Expression {

	private Expression object;
	private String funcName;
	private ArrayList<Expression> arguments;

	public FunctionReferenceExpression(Expression object, String funcName, ArrayList<Expression> arguments) {
		this.object = object;
		this.funcName = funcName;
		this.arguments = arguments;
	}

	@Override
	public Value evaluate(Block b) {
		if (object == null) {

			Value v = b.getValue(funcName);

			if (v == null || v.type != Type.FUNCTION)
				return new Value();

			return ((Function) v.value).call(b, JavaUtils.execute(arguments, b));
		} else {
			Value obj = object.evaluate(b);

			switch (obj.type) {
				case OBJECT: {
					if (obj.value == null)
						return new Value();

					Value v = ((Object) obj.value).getValue(funcName);

					if (v == null || v.type != Type.FUNCTION)
						return new Value();

					return ((Function) v.value).call(b, JavaUtils.execute(arguments, b));
				}
				case JAVA_OBJECT: {
					return new Value(JavaUtils.invokeMethod(obj.value, funcName, JavaUtils.execute(arguments, b)));
				}
				default:
					return new Value();
			}
		}
	}
}
