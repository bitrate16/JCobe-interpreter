package bitrate16.JCobe.Expressions;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Values.Value;
import bitrate16.JCobe.Values.Value.Type;

/**
 * Operator Expression. Invokes as a single operations.
 *
 * @author bitrate16
 *
 */
// XXX XXX XXX XXX XXX: Complete
// XXX XXX XXX XXX XXX: Complete
// XXX XXX XXX XXX XXX: Complete
// XXX XXX XXX XXX XXX: Complete
// XXX XXX XXX XXX XXX: Complete
public class OperatorExpression implements Expression {

	private Expression left;
	private String operator;
	private Expression right;

	public OperatorExpression(Expression left, Expression right, String operator) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	@Override
	public Value evaluate(Block b) {
		Value left = this.left.evaluate(b);
		Value right = this.right.evaluate(b);
		Object ret = operate(left, right);
		// System.err.println("- - - - - - - - - - - - - - - - - - - - - - - >
		// Eval " + left.asString() + " " + operator + " " + right.asString());
		// System.err.println("- - - - - - - - - - - - - - - - - - - - - - - > "
		// + ret);
		return new Value(ret);
	}

	public Object operate(Value left, Value right) {
		switch (operator) {
			// Comparation
			case "==":
				return left.equals(right);
			case "===":
				return left.equalType(right);
			case "!=":
				return !left.equals(right);
			case ">":
				switch (left.type) {
					case BOOLEAN:
					case CHARACTER:
					case INTEGER:
					case BYTE:
						switch (right.type) {
							case BOOLEAN:
							case CHARACTER:
							case INTEGER:
								return left.intValue() > right.intValue();
							case DOUBLE:
								return left.doubleValue() > right.doubleValue();
							case BYTE:
								return left.byteValue() > right.byteValue();
							case LONG:
								return left.longValue() > right.longValue();
							case STRING:
								return left.intValue() > right.intValue();
							case JAVA_OBJECT:
							case FUNCTION:
							case OBJECT:
							default:
								return false;
						}
					case DOUBLE:
						return left.doubleValue() > right.doubleValue();
					case LONG:
						return left.longValue() > right.longValue();
					case JAVA_OBJECT:
					case FUNCTION:
					case OBJECT:
					case STRING:
					default:
						return false;
				}
			case "<":
				switch (left.type) {
					case BOOLEAN:
					case CHARACTER:
					case INTEGER:
					case BYTE:
						switch (right.type) {
							case BOOLEAN:
							case CHARACTER:
							case INTEGER:
								return left.intValue() < right.intValue();
							case DOUBLE:
								return left.doubleValue() < right.doubleValue();
							case BYTE:
								return left.byteValue() < right.byteValue();
							case LONG:
								return left.longValue() < right.longValue();
							case STRING:
								return left.intValue() < right.intValue();
							case JAVA_OBJECT:
							case FUNCTION:
							case OBJECT:
							default:
								return false;
						}
					case DOUBLE:
						return left.doubleValue() < right.doubleValue();
					case LONG:
						return left.longValue() < right.longValue();
					case JAVA_OBJECT:
					case FUNCTION:
					case OBJECT:
					case STRING:
					default:
						return false;
				}
			case ">=":
				switch (left.type) {
					case BOOLEAN:
					case CHARACTER:
					case INTEGER:
					case BYTE:
						switch (right.type) {
							case BOOLEAN:
							case CHARACTER:
							case INTEGER:
								return left.intValue() >= right.intValue();
							case DOUBLE:
								return left.doubleValue() >= right.doubleValue();
							case BYTE:
								return left.byteValue() >= right.byteValue();
							case LONG:
								return left.longValue() >= right.longValue();
							case STRING:
								return left.intValue() >= right.intValue();
							case JAVA_OBJECT:
							case FUNCTION:
							case OBJECT:
							default:
								return false;
						}
					case DOUBLE:
						return left.doubleValue() > right.doubleValue();
					case LONG:
						return left.longValue() > right.longValue();
					case JAVA_OBJECT:
					case FUNCTION:
					case OBJECT:
					case STRING:
					default:
						return false;
				}
			case "<=":
				switch (left.type) {
					case BOOLEAN:
					case CHARACTER:
					case INTEGER:
					case BYTE:
						switch (right.type) {
							case BOOLEAN:
							case CHARACTER:
							case INTEGER:
								return left.intValue() <= right.intValue();
							case DOUBLE:
								return left.doubleValue() <= right.doubleValue();
							case BYTE:
								return left.byteValue() <= right.byteValue();
							case LONG:
								return left.longValue() <= right.longValue();
							case STRING:
								return left.intValue() <= right.intValue();
							case JAVA_OBJECT:
							case FUNCTION:
							case OBJECT:
							default:
								return false;
						}
					case DOUBLE:
						return left.doubleValue() <= right.doubleValue();
					case LONG:
						return left.longValue() <= right.longValue();
					case JAVA_OBJECT:
					case FUNCTION:
					case OBJECT:
					case STRING:
					default:
						return false;
				}

				// Logical
			case "||":
				return left.booleanValue() || right.booleanValue();
			case "&&":
				return left.booleanValue() && right.booleanValue();
			case "|!":
				return left.booleanValue() || !right.booleanValue();
			case "&!":
				return left.booleanValue() && !right.booleanValue();

			// Binary
			case "|":
				if (left.type == Type.LONG)
					return left.longValue() | right.longValue();
				if (left.type == Type.BYTE)
					return left.byteValue() | right.byteValue();
				return left.intValue() | right.intValue();
			case "&":
				if (left.type == Type.LONG)
					return left.longValue() & right.longValue();
				if (left.type == Type.BYTE)
					return left.byteValue() & right.byteValue();
				return left.intValue() & right.intValue();
			case "^":
				if (left.type == Type.LONG)
					return left.longValue() ^ right.longValue();
				if (left.type == Type.BYTE)
					return left.byteValue() ^ right.byteValue();
				return left.intValue() ^ right.intValue();
			case "<<":
				if (left.type == Type.LONG)
					return left.longValue() << right.longValue();
				if (left.type == Type.BYTE)
					return left.byteValue() << right.byteValue();
				return left.intValue() << right.intValue();
			case ">>":
				if (left.type == Type.LONG)
					return left.longValue() >> right.longValue();
				if (left.type == Type.BYTE)
					return left.byteValue() >> right.byteValue();
				return left.intValue() >> right.intValue();
			case ">>>":
				if (left.type == Type.LONG)
					return left.longValue() >>> right.longValue();
				if (left.type == Type.BYTE)
					return left.byteValue() >> right.byteValue();
				return left.intValue() >>> right.intValue();
			case "~":
				// Binary NOT (ONLY RIGHT EXPRESSION)
				switch (right.type) {
					case BOOLEAN:
					case CHARACTER:
					case DOUBLE:
					case FUNCTION:
					case INTEGER:
					case JAVA_OBJECT:
						return new Value(~right.intValue());
					case LONG:
						return new Value(~right.longValue());
					case BYTE:
						return new Value(~right.byteValue());
					case OBJECT:
					case STRING:
					default:
						return new Value(~right.stringValue().length());
				}

				// Mod
			case "%":
				if (left.type == Type.LONG)
					return left.longValue() % right.longValue();
				if (left.type == Type.BYTE)
					return left.byteValue() % right.byteValue();
				return left.intValue() % right.intValue();

			// Normal
			case "+":
				if (right.type == Type.STRING)
					return left.stringValue() + right.stringValue();
				if (right.type == Type.DOUBLE)
					return left.doubleValue() + right.doubleValue();
				switch (left.type) {
					case BOOLEAN:
						return left.intValue() + right.intValue() != 0;
					case BYTE:
						if (right.type == Type.INTEGER)
							return left.intValue() + right.intValue();
						if (right.type == Type.LONG)
							return left.longValue() + right.longValue();
					case CHARACTER:
						if (right.type == Type.LONG)
							return left.longValue() + right.longValue();
						return left.charValue() + right.charValue();
					case INTEGER:
						if (right.type == Type.LONG)
							return left.longValue() + right.longValue();
						return left.intValue() + right.intValue();
					case DOUBLE:
						return left.doubleValue() + right.doubleValue();
					case LONG:
						return left.longValue() + right.longValue();
					case JAVA_OBJECT:
					case FUNCTION:
					case OBJECT:
					case STRING:
					default:
						return left.stringValue() + right.stringValue();
				}
			case "-":
				if (right.type == Type.STRING)
					if (left.stringValue().length() < right.intValue())
						return "";
					else
						return left.stringValue().substring(0, left.stringValue().length() - right.intValue());
				if (right.type == Type.DOUBLE)
					return left.doubleValue() - right.doubleValue();
				switch (left.type) {
					case BOOLEAN:
						return left.intValue() - right.intValue() != 0;
					case BYTE:
						if (right.type == Type.INTEGER)
							return left.intValue() - right.intValue();
						if (right.type == Type.LONG)
							return left.longValue() - right.longValue();
					case CHARACTER:
						if (right.type == Type.LONG)
							return left.longValue() - right.longValue();
						return left.charValue() - right.charValue();
					case INTEGER:
						if (right.type == Type.LONG)
							return left.longValue() - right.longValue();
						return left.intValue() - right.intValue();
					case DOUBLE:
						return left.doubleValue() - right.doubleValue();
					case LONG:
						return left.longValue() - right.longValue();
					case JAVA_OBJECT:
					case FUNCTION:
					case OBJECT:
					case STRING:
					default:
						if (left.stringValue().length() < right.intValue())
							return "";
						else
							return left.stringValue().substring(0, left.stringValue().length() - right.intValue());
				}
			case "*":
				if (right.type == Type.STRING) {
					// Repeat String
					String repeat = right.stringValue();
					String ret = "";
					int count = left.intValue();
					for (int i = 0; i < count; i++) {
						ret += repeat;
					}
					return ret;
				}
				if (right.type == Type.DOUBLE)
					return left.doubleValue() * right.doubleValue();
				switch (left.type) {
					case BOOLEAN:
						return left.intValue() * right.intValue() != 0;
					case BYTE:
						if (right.type == Type.INTEGER)
							return left.intValue() * right.intValue();
						if (right.type == Type.LONG)
							return left.longValue() * right.longValue();
					case CHARACTER:
						if (right.type == Type.LONG)
							return left.longValue() * right.longValue();
						return left.charValue() * right.charValue();
					case INTEGER:
						if (right.type == Type.LONG)
							return left.longValue() * right.longValue();
						return left.intValue() * right.intValue();
					case DOUBLE:
						return left.doubleValue() * right.doubleValue();
					case LONG:
						return left.longValue() * right.longValue();
					case JAVA_OBJECT:
					case FUNCTION:
					case OBJECT:
					case STRING:
					default:
						// Repeat String
						String repeat = left.stringValue();
						String ret = "";
						int count = right.intValue();
						for (int i = 0; i < count; i++) {
							ret += repeat;
						}
						return ret;
				}
			case "/":
				if (right.type == Type.DOUBLE)
					return left.intValue() / right.doubleValue();
				switch (left.type) {
					case BOOLEAN:
						// XNOR
						boolean a = left.booleanValue();
						boolean b = right.booleanValue();

						if (!a && !b)
							return true;
						if (!a && b)
							return false;
						if (a && !b)
							return false;
						if (a && b)
							return true;
					case FUNCTION:
					case JAVA_OBJECT:
					case OBJECT:
					case CHARACTER:
					case INTEGER:
					case STRING:
						int div = right.intValue();
						if (div == 0)
							return 0;
						return (char) (left.intValue() / div);
					case DOUBLE:
						return left.doubleValue() / right.doubleValue();
					case LONG:
						long divo = right.longValue();
						if (divo == 0)
							return 0;
						return left.longValue() / divo;
					case BYTE:
						byte bivo = right.byteValue();
						if (bivo == 0)
							return 0;
						return left.byteValue() / bivo;
					default:
						div = right.intValue();
						if (div == 0)
							return 0;
						return left.stringValue().length() / div;
				}
			default:
				return null;
		}
	}

	public Object operateq(Value left, Value right) {
		switch (operator) {
			// Comparation
			case "==":
				return left.equals(right);
			case "===":
				return left.equalType(right);
			case "!=":
				return !left.equals(right);
			case ">":
				switch (left.type) {
					case BOOLEAN:
					case CHARACTER:
					case INTEGER:
						return left.intValue() > right.intValue();
					case LONG:
						return left.longValue() > right.longValue();
					case DOUBLE:
						return left.doubleValue() > right.doubleValue();
					case STRING:
						return left.stringValue().length() > right.stringValue().length();
					case FUNCTION:
					case JAVA_OBJECT:
					case OBJECT:
					default:
						return false;
				}
			case "<":
				switch (left.type) {
					case BOOLEAN:
					case CHARACTER:
					case INTEGER:
						return left.intValue() < right.intValue();
					case LONG:
						return left.longValue() < right.longValue();
					case DOUBLE:
						return left.doubleValue() < right.doubleValue();
					case STRING:
						return left.stringValue().length() < right.stringValue().length();
					case FUNCTION:
					case JAVA_OBJECT:
					case OBJECT:
					default:
						return false;
				}
			case ">=":
				switch (left.type) {
					case BOOLEAN:
					case CHARACTER:
					case INTEGER:
						return left.intValue() >= right.intValue();
					case LONG:
						return left.longValue() >= right.longValue();
					case DOUBLE:
						return left.doubleValue() >= right.doubleValue();
					case STRING:
						return left.stringValue().length() >= right.stringValue().length();
					case FUNCTION:
					case JAVA_OBJECT:
					case OBJECT:
					default:
						return false;
				}
			case "<=":
				switch (left.type) {
					case BOOLEAN:
					case CHARACTER:
					case INTEGER:
						return left.intValue() <= right.intValue();
					case LONG:
						return left.longValue() <= right.longValue();
					case DOUBLE:
						return left.doubleValue() <= right.doubleValue();
					case STRING:
						return left.stringValue().length() <= right.stringValue().length();
					case FUNCTION:
					case JAVA_OBJECT:
					case OBJECT:
					default:
						return false;
				}

				// Logical
			case "||":
				return left.booleanValue() || right.booleanValue();
			case "&&":
				return left.booleanValue() && right.booleanValue();
			case "|!":
				return left.booleanValue() || !right.booleanValue();
			case "&!":
				return left.booleanValue() && !right.booleanValue();

			// Binary
			case "|":
				if (left.type == Type.LONG)
					return left.longValue() | right.longValue();
				return left.intValue() | right.intValue();
			case "&":
				if (left.type == Type.LONG)
					return left.longValue() | right.longValue();
				return left.intValue() | right.intValue();
			case "^":
				if (left.type == Type.LONG)
					return left.longValue() ^ right.longValue();
				return left.intValue() ^ right.intValue();
			case "<<":
				if (left.type == Type.LONG)
					return left.longValue() << right.longValue();
				return left.intValue() << right.intValue();
			case ">>":
				if (left.type == Type.LONG)
					return left.longValue() >> right.longValue();
				return left.intValue() >> right.intValue();
			case ">>>":
				if (left.type == Type.LONG)
					return left.longValue() >>> right.longValue();
				return left.intValue() >>> right.intValue();
			case "~":
				// Binary NOT (ONLY RIGHT EXPRESSION)
				switch (right.type) {
					case BOOLEAN:
					case CHARACTER:
					case DOUBLE:
					case FUNCTION:
					case INTEGER:
					case JAVA_OBJECT:
						return new Value(~right.intValue());
					case LONG:
						return new Value(~right.longValue());
					case OBJECT:
					case STRING:
					default:
						return new Value(~right.stringValue().length());
				}

				// Mod
			case "%":
				if (left.type == Type.LONG)
					return left.longValue() % right.longValue();
				return left.intValue() % right.intValue();

			// Normal
			case "+":
				if (left.type == Type.STRING)
					return left.stringValue() + right.stringValue();

				switch (right.type) {
					case BOOLEAN:
						return left.longValue() + right.longValue() > 0;
					case CHARACTER:
						return (char) (left.charValue() + right.charValue());
					case DOUBLE:
						return left.doubleValue() + right.doubleValue();
					case INTEGER:
						return left.intValue() + right.intValue();
					case LONG:
						return left.longValue() + right.longValue();
					case JAVA_OBJECT:
					case FUNCTION:
					case OBJECT:
					case STRING:
						return left.stringValue() + right.stringValue();
					default:
						break;
				}
			case "-":
				switch (right.type) {
					case BOOLEAN:
						return left.intValue() - right.intValue() > 0;
					case CHARACTER:
						return (char) (left.charValue() - right.charValue());
					case DOUBLE:
						return left.doubleValue() - right.doubleValue();
					case INTEGER:
						return left.intValue() - right.intValue();
					case LONG:
						return left.longValue() - right.longValue();
					case JAVA_OBJECT:
					case FUNCTION:
					case OBJECT:
						return 0;
					case STRING:
					default:
						switch (left.type) {
							case BOOLEAN:
							case CHARACTER:
							case DOUBLE:
							case FUNCTION:
							case INTEGER:
							case JAVA_OBJECT:
								return left.stringValue().length() - right.intValue();
							case OBJECT:
							case STRING:
							default:
								return left.stringValue().length() - right.stringValue().length();
						}
				}
			case "*":
				switch (right.type) {
					case BOOLEAN:
						return left.intValue() * right.intValue() != 0;
					case CHARACTER:
						return (char) (left.intValue() * right.intValue());
					case DOUBLE:
						return left.doubleValue() * right.doubleValue();
					case INTEGER:
						return left.intValue() * right.intValue();
					case LONG:
						return left.longValue() * right.longValue();
					case JAVA_OBJECT:
					case FUNCTION:
					case OBJECT:
					default:
						return 0;
					case STRING:
						// Repeat String
						String repeat = left.stringValue();
						String ret = "";
						int count = right.intValue();
						for (int i = 0; i < count; i++) {
							ret += repeat;
						}
						return ret;
				}
			case "/":
				switch (right.type) {
					case BOOLEAN:
						// XNOR
						boolean a = left.booleanValue();
						boolean b = right.booleanValue();

						if (!a && !b)
							return true;
						if (!a && b)
							return false;
						if (a && !b)
							return false;
						if (a && b)
							return true;
					case CHARACTER:
						int div = right.intValue();
						if (div == 0)
							return 0;
						return (char) (left.intValue() / div);
					case DOUBLE:
						return left.doubleValue() / right.doubleValue();
					case INTEGER:
						div = right.intValue();
						if (div == 0)
							return 0;
						return left.intValue() / div;
					case LONG:
						long divo = right.longValue();
						if (divo == 0)
							return 0;
						return left.longValue() / divo;
					case FUNCTION:
					case JAVA_OBJECT:
					case OBJECT:
						return 0;
					case STRING:
						div = right.intValue();
						if (div == 0)
							return 0;
						return left.stringValue().length() / div;
					default:
						break;
				}

			default:
				return null;
		}
	}
}
