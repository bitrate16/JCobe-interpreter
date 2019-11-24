package bitrate16.JCobe.Statements;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Expressions.Expression;
import bitrate16.JCobe.Values.Value;

public class VariableDeclarationStatement implements Statement {

	private String name;
	private Expression value;

	public VariableDeclarationStatement(String name, Expression value) {
		this.name = name;
		this.value = value;
	}

	public VariableDeclarationStatement(String name) {
		this.name = name;
	}

	@Override
	public void execute(Block b) {
//		System.out.println("----> Creating variable '" + name + "' with value '" + value.evaluate(b) + "'");
		if (value != null)
			b.putForcibly(name, value.evaluate(b));
		else
			b.putForcibly(name, new Value());
	}
}
