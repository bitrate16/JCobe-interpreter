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
public class NotValueExpression implements Expression {

	private Expression expression;

	public NotValueExpression(Expression expression) {
		this.expression = expression;
	}

	@Override
	public Value evaluate(Block b) {
		Value result = expression.evaluate(b);
		
		return new Value(!result.booleanValue());
	}
}