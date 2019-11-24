package bitrate16.JCobe.Statements;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Expressions.Expression;
import bitrate16.JCobe.Expressions.ObjectReferenceExpression;
import bitrate16.JCobe.Types.JavaObject;
import bitrate16.JCobe.Values.Value;

public class ObjectReferenceAssignStatement implements Statement {

	private ObjectReferenceExpression e; // Object assignation Expression
	private Expression value;

	public ObjectReferenceAssignStatement(ObjectReferenceExpression e, Expression value) {
		this.e = e;
		this.value = value;
	}

	@Override
	public void execute(Block b) {
		Expression object = e.object;
		if (object == null) {
			if (e.name.equals("this")) // Reference to Object's context
				return; // Can't assign

			// Assign to local variable
			// System.err.println(e.name + " old: " + b.getValue(e.name));
			b.putValue(e.name, value.evaluate(b));
			// System.err.println(e.name + " new: " + b.getValue(e.name));
		} else {
			Value obj = object.evaluate(b);

			if (obj.value == null)
				return;

			switch (obj.type) {
				case OBJECT:
					bitrate16.JCobe.Types.AbstractObject o = (bitrate16.JCobe.Types.AbstractObject) obj.value;
					o.setProperty(e.name, value.evaluate(b));
					return;
				default:
					// Java Object, number, e.t.c
					JavaObject jo = new JavaObject(obj.value);
					jo.setProperty(e.name, value.evaluate(b));
					return;
			}

			// AbstractObject o = (AbstractObject) obj.value;
			// o.setProperty(e.name, value.evaluate(b));
		}
	}
}
