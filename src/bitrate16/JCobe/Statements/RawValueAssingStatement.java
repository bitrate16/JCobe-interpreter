package bitrate16.JCobe.Statements;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Expressions.Expression;
import bitrate16.JCobe.Values.Value;

/**
 * Assign value by return pointer
 * 
 * @author bitrate16
 *
 */
public class RawValueAssingStatement implements Statement {

	private Expression value;
	private Expression object;

	public RawValueAssingStatement(Expression object, Expression value) {
		this.object = object;
		this.value = value;
	}

	@Override
	public void execute(Block b) {
		Value obj = object.evaluate(b);

		if (object == null || obj == null)
			return;

		obj.setValue(value.evaluate(b).value);
	}
}
