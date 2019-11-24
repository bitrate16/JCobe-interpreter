package bitrate16.JCobe.Expressions;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Values.Value;

public class ExpressionObjectReferenceExpression implements Expression {

	private String objName;
	private Expression exp;

	public ExpressionObjectReferenceExpression(Expression exp,
			String objName) {
		this.exp = exp;
				this.objName = objName;
	}

	@Override
	public Value evaluate(Block b) {
		return null;
	}
}
