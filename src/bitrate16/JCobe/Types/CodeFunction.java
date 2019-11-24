package bitrate16.JCobe.Types;

import java.util.Arrays;
import java.util.List;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Enviroment.Block.ExecutionListener;
import bitrate16.JCobe.Statements.ReturnStatement;
import bitrate16.JCobe.Statements.Statement;
import bitrate16.JCobe.Values.Value;

/**
 * Function Implementation. Used for executing Statements as a single function.
 * 
 * @author bitrate16
 *
 */
public class CodeFunction implements Function {

	private Block context;
	private List<Statement> statements;
	private List<String> argumentNames;
	private Block parent; // Execution context

	public CodeFunction(Block parent, List<String> argumentNames, List<Statement> statements) {
		this.argumentNames = argumentNames;
		this.statements = statements;
		this.parent = parent;
		context = new Block();

		// Add return statement listener
		context.executionListener = new ExecutionListener() {
			@Override
			public boolean stop(Statement s) {
				// Check if contains return value
				if (context.containsValue("$return"))
					return true;

				// Return statement
				if (s instanceof ReturnStatement) {
					ReturnStatement ret = (ReturnStatement) s;
					if (ret.ret != null)
						context.putValue("$return", ret.ret.evaluate(context));
					return true;
				}
				return false;
			}
		};
	}

	@Override
	public Value call(final Block block, final Value... args) {
		context.execute(parent, statements, argumentNames, Arrays.asList(args));

		if (context.containsValue("$return")) {
			Value result = context.localStorage.get("$return");
			context.localStorage.remove("$return"); // remove
			return result;
		}
		return new Value();
	}
}
