package bitrate16.JCobe.Expressions;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Statements.ArrayElementReferenceAssignStatement;
import bitrate16.JCobe.Statements.ObjectReferenceAssignStatement;
import bitrate16.JCobe.Values.Value;

public class PostIncrementExpression implements Expression {
	private Expression expression;

	public PostIncrementExpression(Expression expression) {
		this.expression = expression;
	}

	@Override
	public Value evaluate(Block b) {
		// return
		Value ret = new Value();

		if (expression instanceof ObjectReferenceExpression) {
			// Assign as variable
			ObjectReferenceExpression e = (ObjectReferenceExpression) expression;

			ret = e.evaluate(b);

			// Assign new Value
			new ObjectReferenceAssignStatement(e, new PostIncrementExpression(ret)).execute(b);
		} else if (expression instanceof ObjectElementReferenceExpression) {
			// Assign as Object subObject
			ObjectElementReferenceExpression e = (ObjectElementReferenceExpression) expression;

			ret = e.evaluate(b);

			// Assign new Value
			new ArrayElementReferenceAssignStatement(e, new PostIncrementExpression(ret)).execute(b);
		} else {
			// Assign as object value
			Value v = expression.evaluate(b);
			switch (v.type) {
				case BOOLEAN:
					return new Value(new Value(v.intValue() + 1).booleanValue());
				case CHARACTER:
					return new Value((char) (v.intValue() + 1));
				case DOUBLE:
					return new Value(v.doubleValue() + 1.0);
				case INTEGER:
					return new Value(v.intValue() + 1);
				case LONG:
					return new Value(v.longValue() + 1L);
				case STRING:
					return new Value(v.stringValue() + v.stringValue());
				case JAVA_OBJECT:
				case FUNCTION:
				case OBJECT:
				default:
					return v;
			}
		}
		return ret;
	}
}
