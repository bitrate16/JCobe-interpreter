package bitrate16.JCobe.Types;

import java.lang.reflect.Field;
import java.lang.Object;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Utils.JavaUtils;
import bitrate16.JCobe.Utils.StringUtils;
import bitrate16.JCobe.Values.Value;

public class JavaObject extends AbstractObject {

	public Object object; // Source Object

	public JavaObject(Object object) {
		this.object = object;
	}

	// For Arrays
	// [..
	@Override
	public Value getValue(String name) {
		if (StringUtils.isPositiveNumber(name)) {
			int index = StringUtils.parseNumber(name);
			return new Value(new Value(object).getArrayElement(index));
		}
		return new Value();
	}

	@Override
	public Value setValue(String name, Value v) {
		if (StringUtils.isPositiveNumber(name)) {
			int index = StringUtils.parseNumber(name);
			return new Value(new Value(object).setArrayElement(v.value, index));
		}
		return new Value();
	}
	// ..]

	@Override
	public Value getProperty(final String name) {
		// Return Object by keyword
		if ("object".equals(name))
			return new Value(object);
		// Return type
		if ("type".equals(name))
			return new Value(new Value(object).type);

		if (object == null)
			return new Value();

		// I. Get field
		Field f = getField(name);
		if (f == null) {
			// II. Get Method (Function-invoker)
			return new Value(new Function() {
				@Override
				public Value call(Block b, Value... args) {
					return new Value(JavaUtils.invokeMethod(object, name, args));
				}
			});
		}

		try {
			return new Value(f.get(object));
		} catch (Exception e) {
			return new Value();
		}
	}

	@Override
	public Value setProperty(String name, Value v) {
		if (object == null)
			return new Value();

		// Set Field Value
		Field f = getField(name);
		if (f == null)
			return new Value();

		try {
			f.set(v.value, object);
			return v;
		} catch (Exception e) {
			return new Value();
		}
	}

	public Field getField(String name) {
		try {
			return object.getClass().getField(name);
		} catch (Exception e) {
			return null;
		}
	}
}
