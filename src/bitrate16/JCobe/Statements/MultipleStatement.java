package bitrate16.JCobe.Statements;

import java.util.List;

import bitrate16.JCobe.Enviroment.Block;

/**
 * Executes all given statements as one
 * 
 * @author bitrate16
 *
 */
public class MultipleStatement implements Statement {

	private List<Statement> statements;

	public MultipleStatement(List<Statement> statements) {
		this.statements = statements;
	}

	@Override
	public void execute(Block b) {
		for (Statement s : statements)
			s.execute(b);
	}
}
