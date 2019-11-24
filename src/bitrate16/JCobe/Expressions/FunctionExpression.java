package bitrate16.JCobe.Expressions;

import java.util.List;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Statements.Statement;
import bitrate16.JCobe.Types.CodeFunction;
import bitrate16.JCobe.Values.Value;

public class FunctionExpression implements Expression {

	private List<Statement> statements;
	private List<String> arguments;

	public FunctionExpression(List<String> arguments, List<Statement> statements) {
		this.arguments = arguments;
		this.statements = statements;
	}

	@Override
	public Value evaluate(Block b) {
		return new Value(new CodeFunction(b, arguments, statements));
	}
}
