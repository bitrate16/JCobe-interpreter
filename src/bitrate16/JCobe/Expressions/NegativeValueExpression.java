package bitrate16.JCobe.Expressions;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Values.Value;

/**
 * Negative value.
 * 
 * var a = -b;
 * 
 * @author bitrate16
 *
 */
public class NegativeValueExpression implements Expression {

	private Expression expression;

	public NegativeValueExpression(Expression expression) {
		this.expression = expression;
	}

	@Override
	public Value evaluate(Block b) {
		return expression.evaluate(b).negative();
	}
}
