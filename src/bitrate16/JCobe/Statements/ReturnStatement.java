package bitrate16.JCobe.Statements;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Expressions.Expression;

/**
 * Called when function needs to return value
 * 
 * @author bitrate16
 *
 */
public class ReturnStatement implements Statement {
	public Expression ret;

	public ReturnStatement(Expression ret) {
		this.ret = ret;
	}

	@Override
	public void execute(Block b) {
	}
}
