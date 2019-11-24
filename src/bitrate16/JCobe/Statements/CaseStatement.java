package bitrate16.JCobe.Statements;

import java.util.List;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Enviroment.Block.ExecutionListener;
import bitrate16.JCobe.Expressions.Expression;
import bitrate16.JCobe.Values.Value;

public class CaseStatement implements Statement {

	// case <expression>: { <statements> }
	public Expression expression;
	public List<Statement> statements;

	// Context
	public Block context = new Block();

	public CaseStatement(Expression expression, List<Statement> statements) {
		this.expression = expression;
		this.statements = statements;

		// Add Break listener
		context.executionListener = new ExecutionListener() {
			@Override
			public boolean stop(Statement s) {
				// Check if contains return value
				if (context.containsValue("$return") || context.containsValue("$continue") || context.containsValue("$break"))
					return true;

				// Return statement
				if (s instanceof BreakStatement) {
					// Nope
					return true;
				}

				// Return statement
				if (s instanceof ContinueStatement) {
					context.putValue("$continue", new Value());
					return true;
				}

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

					if (context.containsValue("$continue"))
						parent.putValue("$continue", new Value());
				}
			}
		};
	}

	@Override
	public void execute(Block b) {
		context.execute(b, statements);
	}
}
