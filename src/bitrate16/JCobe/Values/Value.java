package bitrate16.JCobe.Values;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Expressions.Expression;

public class Value implements Expression {
	// GlobalType definition
	public enum Type {
		INTEGER, LONG, DOUBLE, CHARACTER, BOOLEAN, STRING, OBJECT, JAVA_OBJECT, FUNCTION, BYTE
	}

	// Value type
	public Type type;

	// Value in Object
	public Object value;

	public Value(Object value) {
		setValue(value);
	}

	// Empty value
	public Value() {
		setValue(null);
	}

	/**
	 * Called when Interpreter needs to get Value value
	 */
	@Override
	public Value evaluate(Block b) {
		return new Value(value);
	}

	// Set's value & assign type
	public void setValue(Object value) {
		// Assign it now
		this.value = value;

		// Do check for java objects
		if (value instanceof java.lang.Integer) {
			type = Type.INTEGER;
		} else if (value instanceof java.lang.Byte) {
			type = Type.BYTE;
		} else if (value instanceof java.lang.Long) {
			type = Type.LONG;
		} else if (value instanceof java.lang.Double) {
			type = Type.DOUBLE;
		} else if (value instanceof java.lang.Boolean) {
			type = Type.BOOLEAN;
		} else if (value instanceof java.lang.Character) {
			type = Type.CHARACTER;
		} else if (value instanceof java.lang.String) {
			type = Type.STRING;
		} else
		// Do check for JCobe types
		if (value instanceof bitrate16.JCobe.Types.AbstractObject) {
			type = Type.OBJECT;
		} else if (value instanceof bitrate16.JCobe.Types.Function) {
			type = Type.FUNCTION;
		}
		// Java Object
		else {
			this.type = Type.JAVA_OBJECT;
		}
	}

	/**
	 * Does cast values
	 */
	// From * to Integer
	public int intValue() {
		if (value == null) {
			return 0;
		} else
		// Do check for java objects
		if (value instanceof java.lang.Long) {
			return new Long((long) value).intValue();
		} else if (value instanceof java.lang.Integer) {
			return (int) value;
		} else if (value instanceof java.lang.Byte) {
			return (int) (byte) value;
		} else if (value instanceof java.lang.Double) {
			return (int) (double) value;
		} else if (value instanceof java.lang.Boolean) {
			return ((boolean) value ? 1 : 0);
		} else if (value instanceof java.lang.Character) {
			return (int) (char) value;
		} else if (value instanceof java.lang.String) {
			return stringValue().length();
		} else
		// Do check for JCobe types
		if (value instanceof bitrate16.JCobe.Types.AbstractObject) {
			return 0;
		} else if (value instanceof bitrate16.JCobe.Types.Function) {
			return 0;
		}
		// Java Object
		else {
			return 0;
		}
	}

	// Form * to Long
	public long longValue() {
		if (type == Type.LONG)
			return (long) value;
		return intValue() + 0L;
	}

	// From * to Double
	public double doubleValue() {
		if (value == null) {
			return 0;
		} else
		// Do check for java objects
		if (value instanceof java.lang.Long) {
			return new Long((long) value).intValue();
		} else if (value instanceof java.lang.Integer) {
			return (int) value;
		} else if (value instanceof java.lang.Byte) {
			return (int) (byte) value + 0.0;
		} else if (value instanceof java.lang.Double) {
			return (double) value;
		} else if (value instanceof java.lang.Boolean) {
			return ((boolean) value ? 1 : 0);
		} else if (value instanceof java.lang.Character) {
			return (int) (char) value;
		} else if (value instanceof java.lang.String) {
			return stringValue().length();
		} else
		// Do check for JCobe types
		if (value instanceof bitrate16.JCobe.Types.AbstractObject
				|| value.getClass().getName().contains(bitrate16.JCobe.Types.AbstractObject.class.getName())) {
			return 0;
		} else if (value instanceof bitrate16.JCobe.Types.Function) {
			return 0;
		}
		// Java Object
		else {
			return 0;
		}
	}

	// From * to Character
	public char charValue() {
		if (type == Type.CHARACTER)
			return (char) value;
		return (char) intValue();
	}

	// From * to Byte
	public byte byteValue() {
		if (type == Type.BYTE)
			return (byte) value;
		return (byte) intValue();
	}

	// From * to Boolean
	public boolean booleanValue() {
		if (type == Type.BOOLEAN)
			return (boolean) value;
		return intValue() != 0;
	}

	// From * to String
	public String stringValue() {
		return "" + value;
	}
	// ..]

	/** Checks if given Value is equal to this. Includes Type comparation **/
	public boolean equals(Value v) {
		if (this.type != v.type)
			return false;

		if (this.value == null && v.value == null)
			return true;

		if (this.value == null)
			return false;

		if (v.value == null)
			return false;

		return this.value.equals(v.value);
	}

	/** Checks if given Value type is equal to this **/
	public boolean equalType(Value v) {
		return this.type == v.type;
	}

	/** Returns Arrey type Dimensions count **/
	public int getArrayDimensionsCount() {
		String name = getClass().getName();

		return name.length() - name.replace("[", "").length();
	}

	/** Returns Object in Array Type by given indices **/
	public Object getArrayElement(int... dimens) {
		// Pre-Check
		if (getArrayDimensionsCount() < dimens.length)
			return null;

		// Get Object by specified location in array
		try {
			// Temp array
			Object[] tempArray = (Object[]) value;

			// Loop and get all SubArrays by their indices
			for (int i = 0; i < dimens.length; i++) {
				// Check out of bounds
				if (dimens[i] < 0 || dimens[i] >= tempArray.length)
					return null;

				// Get current SubArray
				if (i == dimens.length - 1)
					return tempArray[dimens[i]];
				else
					tempArray = (Object[]) tempArray[dimens[i]];
			}

			// Return this Object
			return tempArray;
		} catch (Exception e) {
			return null;
		}
	}

	/** Set's Array Type element by given Indices **/
	public Object setArrayElement(Object value, int... dimens) {
		// Pre-Check
		if (getArrayDimensionsCount() < dimens.length)
			return null;

		// Get Object by specified location in array
		try {
			// Temp array
			Object[] tempArray = (Object[]) value;

			// Loop and get all SubArrays by their indices
			for (int i = 0; i < dimens.length; i++) {
				// Check out of bounds
				if (dimens[i] < 0 || dimens[i] >= tempArray.length)
					return null;

				// Get current SubArray
				if (i == dimens.length - 1) {
					tempArray[dimens[i]] = value;
					return value;
				} else
					tempArray = (Object[]) tempArray[dimens[i]];
			}

			// Return this Object
			return value;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return stringValue();
	}

	public String asString() {
		return type + " " + value;
	}

	/** From positive to negative, from negative to positive **/
	public Value negative() {
		switch (type) {
			case BOOLEAN:
				return new Value(!booleanValue());
			case CHARACTER:
				return new Value(charValue());
			case DOUBLE:
				return new Value(-doubleValue());
			case INTEGER:
				return new Value(-intValue());
			case LONG:
				return new Value(-longValue());
			case BYTE:
				return new Value(-byteValue());
			case OBJECT:
			case FUNCTION:
			case JAVA_OBJECT:
			case STRING:
				return new Value(new StringBuilder(stringValue()).reverse().toString());
			default:
				return new Value(0);
		}
	}

	public boolean hardEquals(Value v) {
		switch (type) {
			case DOUBLE:
				return doubleValue() == v.doubleValue();
			case INTEGER:
			case CHARACTER:
			case BOOLEAN:
				return intValue() == v.intValue();
			case LONG:
				return longValue() == v.longValue();
			case BYTE:
				return byteValue() == v.byteValue();
			case FUNCTION:
			case STRING:
			case OBJECT:
			default:
			case JAVA_OBJECT:
				return value == v.value;
		}
	}
}
