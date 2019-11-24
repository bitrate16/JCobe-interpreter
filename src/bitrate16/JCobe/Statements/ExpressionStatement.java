package bitrate16.JCobe.Statements;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Expressions.Expression;

/**
 * Does nothing, executes Expressions
 * 
 * @author bitrate16
 *
 */
public class ExpressionStatement implements Statement {

	private Expression[] exp;

	public ExpressionStatement(Expression... exp) {
		this.exp = exp;
	}

	@Override
	public void execute(Block b) {
		for (int i = 0; i < exp.length; i++)
			exp[i].evaluate(b);
	}
}
