package bitrate16.JCobe.Expressions;

import java.util.List;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Types.ArrayObject;
import bitrate16.JCobe.Utils.JavaUtils;
import bitrate16.JCobe.Values.Value;

public class JCobeLinearArrayExpression implements Expression {

	private List<Expression> arrayValues;

	public JCobeLinearArrayExpression(List<Expression> arrayValues) {
		this.arrayValues = arrayValues;
	}

	@Override
	public Value evaluate(Block b) {
		return new Value(new ArrayObject(JavaUtils.executeList(arrayValues, b)));
	}
}
