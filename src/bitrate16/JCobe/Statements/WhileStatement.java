package bitrate16.JCobe.Statements;

import java.util.HashMap;
import java.util.List;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Enviroment.Block.ExecutionListener;
import bitrate16.JCobe.Expressions.Expression;
import bitrate16.JCobe.Values.Value;

public class WhileStatement implements Statement {

	// while(<condition>) { <statements> }
	public Expression condition;
	public List<Statement> statements;
	// Context
	public Block context = new Block();

	// Can execute
	public boolean executing = true;

	public WhileStatement(Expression condition, List<Statement> statements) {
		this.condition = condition;
		this.statements = statements;

		// Add Break listener
		context.executionListener = new ExecutionListener() {
			@Override
			public boolean stop(Statement s) {
				// Check if contains return value
				if (context.containsValue("$return"))
					return true;

				if (context.containsValue("$break")) {
					executing = false;
					context.localStorage.remove("$break");
					return true;
				}

				if (context.containsValue("$continue")) {
					context.localStorage.remove("$continue");
					return true;
				}

				// Return statement
				if (s instanceof BreakStatement) {
					executing = false;
					return true;
				}

				if (s instanceof ContinueStatement)
					return true;

				if (s instanceof ReturnStatement) {
					executing = false;
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
				if (parent != null)
					if (context.containsValue("$return"))
						parent.putValue("$return", context.getValue("$return"));
			}
		};
	}

	@Override
	public void execute(Block b) {
		// Executes in parent context, cuz all Expressions is referenced by
		// parent
		while (condition.evaluate(b).booleanValue() && executing)
			context.execute(b, statements, new HashMap<String, Value>());
	}
}
