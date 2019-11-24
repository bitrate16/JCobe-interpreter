package bitrate16.JCobe.Expressions;

import java.util.ArrayList;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Values.Value;

/**
 * Executes function of the given Object.
 * 
 * If Object not found by path, returns invocation of the java static method
 * 
 * @author bitrate16
 *
 */
// XXX: Complete
public class CodeFunctionInvocationExpression implements Expression {

	private ArrayList<String> objectPath;
	private ArrayList<Expression> argExpr;

	public CodeFunctionInvocationExpression(ArrayList<String> objectPath, ArrayList<Expression> argExpr) {
		this.objectPath = objectPath;
		this.argExpr = argExpr;
	}

	@Override
	public Value evaluate(Block b) {
		return null;
	}
}
