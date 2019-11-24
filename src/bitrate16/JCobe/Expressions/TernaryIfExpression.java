package bitrate16.JCobe.Expressions;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Values.Value;

/**
 * Ternary operator
 * 
 * <condition> ? <return1> : <return2>
 * 
 * @author bitrate16
 *
 */
public class TernaryIfExpression implements Expression {

	private Expression ret1;
	private Expression ret2;
	private Expression condition;

	public TernaryIfExpression(Expression condition, Expression ret1, Expression ret2) {
		this.condition = condition;
		this.ret1 = ret1;
		this.ret2 = ret2;
	}

	@Override
	public Value evaluate(Block b) {
		return condition.evaluate(b).booleanValue() ? ret1.evaluate(b) : ret2.evaluate(b);
	}
}
