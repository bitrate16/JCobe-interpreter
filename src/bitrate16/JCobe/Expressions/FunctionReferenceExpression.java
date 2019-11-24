package bitrate16.JCobe.Expressions;

import java.util.ArrayList;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Types.Function;
import bitrate16.JCobe.Utils.JavaUtils;
import bitrate16.JCobe.Values.Value;
import bitrate16.JCobe.Values.Value.Type;

/**
 * Executes Given Object as function
 * 
 * @author bitrate16
 *
 */
public class FunctionReferenceExpression implements Expression {

	private ArrayList<Expression> arguments;
	private Expression object;

	public FunctionReferenceExpression(Expression object, ArrayList<Expression> arguments) {
		this.object = object;
		this.arguments = arguments;
	}

	@Override
	public Value evaluate(Block b) {
		if (object == null)
			return new Value();

		Value v = object.evaluate(b);
		if (v.type != Type.FUNCTION)
			return new Value();

		return ((Function) v.value).call(b, JavaUtils.execute(arguments, b));
	}
}
