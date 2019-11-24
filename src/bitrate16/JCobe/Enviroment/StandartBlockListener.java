package bitrate16.JCobe.Enviroment;

import bitrate16.JCobe.Enviroment.Block.ExecutionListener;
import bitrate16.JCobe.Statements.BreakStatement;
import bitrate16.JCobe.Statements.ContinueStatement;
import bitrate16.JCobe.Statements.ReturnStatement;
import bitrate16.JCobe.Statements.Statement;
import bitrate16.JCobe.Values.Value;

public class StandartBlockListener extends ExecutionListener {

	private Block context;

	public StandartBlockListener(Block context) {
		this.context = context;
	}

	@Override
	public boolean stop(Statement s) {
		// Check if contains return value
		if (context.containsValue("$return") || context.containsValue("$continue") || context.containsValue("$break"))
			return true;

		// Return statement
		if (s instanceof BreakStatement) {
			context.putValue("$break", new Value());
			return true;
		}

		// Return statement
		if (s instanceof ContinueStatement) {
			context.putValue("$continue", new Value());
			return true;
		}

		// Return statement
		if (s instanceof ReturnStatement) {
			ReturnStatement ret = (ReturnStatement) s;

			if (ret.ret != null)
				context.putValue("$return", ret.ret.evaluate(context));
			return true;
		}
		return false;
	}

	@Override
	public void onEnd(Block parent) {
		// Push returned Value upwards
		if (parent != null) {
			if (context.containsValue("$return"))
				parent.putValue("$return", context.getValue("$return"));

			if (context.containsValue("$break"))
				parent.putValue("$break", new Value());

			if (context.containsValue("$continue"))
				parent.putValue("$continue", new Value());
		}
	}
}
