package bitrate16.JCobe.Statements;

import java.util.List;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Types.CodeFunction;
import bitrate16.JCobe.Values.Value;

public class FunctionDeclarationStatement implements Statement {

	private String name;
	private List<Statement> statements;
	private List<String> arguments;

	public FunctionDeclarationStatement(String name, List<String> arguments, List<Statement> statements) {
		this.name = name;
		this.arguments = arguments;
		this.statements = statements;
	}

	@Override
	public void execute(Block b) {
		b.putValue(name, new Value(new CodeFunction(b, arguments, statements)));
	}
}
