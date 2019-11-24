package bitrate16.JCobe.Statements;

import java.util.List; 

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Expressions.Expression;
import bitrate16.JCobe.Values.Value;

public class SwitchStatement implements Statement {

	// case(<expression>) { <CaseStatements> }
	private Expression expression;
	private List<CaseStatement> statements;

	public SwitchStatement(Expression expression, List<CaseStatement> statements) {
		this.expression = expression;
		this.statements = statements;
	}

	@Override
	public void execute(Block b) {
		Value caze = expression.evaluate(b);

		for (CaseStatement cs : statements)
			if (cs.expression.evaluate(b).hardEquals(caze))
				cs.execute(b);
	}
}
