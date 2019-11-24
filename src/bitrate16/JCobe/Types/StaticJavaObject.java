package bitrate16.JCobe.Types;

import java.lang.reflect.Field;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Utils.JavaUtils;
import bitrate16.JCobe.Utils.StringUtils;
import bitrate16.JCobe.Values.Value;

/**
 * Object for doing static references to a class
 * 
 * @author bitrate16
 *
 */
public class StaticJavaObject extends AbstractObject {

	private Class<?> clazz;

	public StaticJavaObject(Class<?> clazz) {
		this.clazz = clazz;
	}

	// For Arrays
	// [..
	@Override
	public Value getValue(String name) {
		if (StringUtils.isPositiveNumber(name)) {
			int index = StringUtils.parseNumber(name);
			Field f = getField(name);
			try {
				java.lang.Object value = f.get(null);
				return new Value(// new JavaObject(
						new Value(value).getArrayElement(index)// )
				);
			} catch (Exception e) {
			}
		}
		return new Value();
	}

	@Override
	public Value setValue(String name, Value v) {
		if (StringUtils.isPositiveNumber(name)) {
			int index = StringUtils.parseNumber(name);
			// Set Field Value
			Field f = getField(name);
			try {
				java.lang.Object value = f.get(null);
				return new Value(new Value(value).setArrayElement(v.value, index));
			} catch (Exception e) {
			}
		}
		return new Value();
	}
	// ..]

	@Override
	public Value getProperty(final String name) {
		if (clazz == null)
			return new Value();

		// I. Get field
		Field f = getField(name);
		if (f == null) {
			// II. Get Method (Function-invoker)
			return new Value(new Function() {
				@Override
				public Value call(Block b, Value... args) {
					return new Value(// new JavaObject(
							JavaUtils.invokeStaticMethod(clazz, name, args)// )
					);
				}
			});
		}

		try {
			return new Value(// new JavaObject(
					f.get(null)// )
			);
		} catch (Exception e) {
			return new Value();
		}
	}

	@Override
	public Value setProperty(String name, Value v) {
		if (clazz == null)
			return new Value();

		// Set Field Value
		Field f = getField(name);
		if (f == null)
			return new Value();

		try {
			f.set(v.value, null);
			return v;
		} catch (Exception e) {
			return new Value();
		}
	}

	public Field getField(String name) {
		try {
			return clazz.getField(name);
		} catch (Exception e) {
			return null;
		}
	}
}
