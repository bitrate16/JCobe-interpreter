package bitrate16.JCobe.Statements;

import java.util.List; 

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Enviroment.StandartBlockListener;
import bitrate16.JCobe.Expressions.Expression;

public class IfElseStatement implements Statement {

	// if(<expression>) { <IfStatements> } else { <ElseStatements> }
	public Expression expression;
	public List<Statement> ifStatements;
	public List<Statement> elseStatements;

	// Context
	public Block context = new Block();

	public IfElseStatement(Expression expression, List<Statement> ifStatements, List<Statement> elseStatements) {
		this.expression = expression;
		this.ifStatements = ifStatements;
		this.elseStatements = elseStatements;

		// Add listener for return statement
		context.executionListener = new StandartBlockListener(context);
	}

	@Override
	public void execute(Block b) {
		if (expression.evaluate(b).booleanValue()) {
			context.execute(b, ifStatements);
		} else if (elseStatements != null) {
			context.execute(b, elseStatements);
		}
	}
}
