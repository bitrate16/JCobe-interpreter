package bitrate16.JCobe.Expressions;

import java.util.HashMap;  

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Values.Value;

public class JCobeObjectExpression implements Expression {

	private HashMap<String, Expression> values;

	public JCobeObjectExpression(HashMap<String, Expression> values) {
		this.values = values;
	}

	@Override
	public Value evaluate(Block b) {
//		System.out.println("--------> Creating CodeObject with context: ");
//		JavaUtils.printHashMap(b.localStorage);
		return new Value(new bitrate16.JCobe.Types.CodeObject(b, values));
	}
}
