package bitrate16.JCobe.Types;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import bitrate16.JCobe.JCobe;
import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Utils.JavaUtils;
import bitrate16.JCobe.Values.Value;
import bitrate16.JCobe.Values.Value.Type;

/**
 * Proxy object - class for handling events/methods from interfaces/abstract
 * objects
 * 
 * @author bitrate16
 *
 */
public class ProxyObject extends ObjectImpl {
	private Class<?> clazz;
	// Map all overriden Methods
	private HashMap<String, Value> overridenMethods;
	// Instance
	public Object instance;

	public ProxyObject(Class<?> clazz) {
		this.clazz = clazz;
		this.overridenMethods = new HashMap<String, Value>();

		init();
	}

	private void init() {
		// Setting up Proxy instance

		// Add invocation Impl instance
		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
				String name = m.getName();

				Value impl = overridenMethods.get(name);
				if (impl == null || impl.value == null || impl.type != Type.FUNCTION)
					return null;

				Function f = (Function) impl.value;

				// Arguments to Value array
				Value[] arguments = JavaUtils.objectsToValue(args);

				return f.call(JCobe.instance.getRootContext(), arguments).value;
			}
		};

		// Create new Proxy instance & assign to variable
		instance = clazz.cast(Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] { clazz }, handler));
	}

	@Override
	public Object getObjectProperty(final String name) {
		// Return Object by keyword
		if ("object".equals(name))
			return instance;

		// I. Get field
		Field f = getField(name);
		if (f == null) {
			// // I.5. Check if contains overriding method
			// if (this.overridenMethods.containsKey(name)) {
			// Value v = this.overridenMethods.get(name);
			// if (v == null)
			// return null;
			// return v.value;
			// }

			// II. Get Method (Function-invoker)
			return new Function() {
				@Override
				public Value call(Block b, Value... args) {
					return new Value(JavaUtils.invokeMethod(instance, name, args));
				}
			};
		}

		try {
			return f.get(instance);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Object setObjectProperty(String name, Value v) {
		// Set Field Value
		Field f = getField(name);

		try {
			f.set(v.value, instance);
			return v.value;
		} catch (Exception e) {
			// Set listener
			this.overridenMethods.put(name, v);
			return v.value;
		}
	}

	private Field getField(String name) {
		try {
			return instance.getClass().getField(name);
		} catch (Exception e) {
			return null;
		}
	}
}
