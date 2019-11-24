package bitrate16.JCobe.Expressions;

import java.util.ArrayList;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Values.Value;

/**
 * Returns value of given element of given array of given Object.
 * 
 * @author bitrate16
 *
 */
public class ArrayReferenceExpression implements Expression {

	private Expression object;
	private ArrayList<Expression> arguments;

	public ArrayReferenceExpression(Expression object, ArrayList<Expression> arguments) {
		this.object = object;
		this.arguments = arguments;
	}

	@Override
	public Value evaluate(Block b) {
		Value object = this.object.evaluate(b);

		if (object == null)
			return new Value();

		int[] dimens = new int[arguments.size()];

		for (int i = 0; i < dimens.length; i++)
			dimens[i] = arguments.get(i).evaluate(b).intValue();

		return new Value(object.getArrayElement(dimens));
	}
}
