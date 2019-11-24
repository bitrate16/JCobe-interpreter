package bitrate16.JCobe.Statements;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Expressions.Expression;
import bitrate16.JCobe.Expressions.ObjectElementReferenceExpression;
import bitrate16.JCobe.Types.JavaObject;
import bitrate16.JCobe.Values.Value;

public class ArrayElementReferenceAssignStatement implements Statement {

	private Expression value;
	private ObjectElementReferenceExpression e; // Object reference Value

	public ArrayElementReferenceAssignStatement(ObjectElementReferenceExpression e, Expression value) {
		this.e = e;
		this.value = value;
	}

	@Override
	public void execute(Block b) {
		Expression object = e.object;
		if (object == null) {
			return;
		} else {
			Value obj = object.evaluate(b);

			if (obj.value == null)
				return;

			switch (obj.type) {
				case OBJECT:
					bitrate16.JCobe.Types.AbstractObject o = (bitrate16.JCobe.Types.AbstractObject) obj.value;
					o.setValue(e.key.evaluate(b).stringValue(), value.evaluate(b));
					return;

				default:
					// Java Object, number, e.t.c
					JavaObject jo = new JavaObject(obj.value);
					jo.setValue(e.key.evaluate(b).stringValue(), value.evaluate(b));
					return;
			}

			// AbstractObject o = (AbstractObject) obj.value;
			// o.setValue(e.key.evaluate(b).stringValue(), value.evaluate(b));
		}
	}
}
