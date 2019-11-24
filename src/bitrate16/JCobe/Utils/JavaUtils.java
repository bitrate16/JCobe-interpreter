package bitrate16.JCobe.Utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Expressions.Expression;
import bitrate16.JCobe.Types.AbstractObject;
import bitrate16.JCobe.Types.Function;
import bitrate16.JCobe.Values.Value;

/** I don't know, what's happening here **/
public class JavaUtils {
	/** Creates Object array with given Class **/
	public static java.lang.Object newArray(Class<?> clazz, int[] dimens) {
		return Array.newInstance(clazz, dimens);
	}

	public Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Returns static Field of the Object by name
	 */
	public static java.lang.Object getStaticField(String name, Class<?> clazz) {
		Field f;
		try {
			f = clazz.getField(name);

			if (!Modifier.isStatic(f.getModifiers()))
				return null;

			return f.get(null);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns Field of the Object by name
	 */
	public static java.lang.Object getField(String name, java.lang.Object obj) {
		Field f;
		try {
			f = obj.getClass().getField(name);

			return f.get(obj);

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Executes All given Expressions into Value Array
	 */
	public static Value[] execute(List<Expression> expressions, Block b) {
		Value[] args = new Value[expressions.size()];

		for (int i = 0; i < args.length; i++) {
			args[i] = expressions.get(i).evaluate(b);
		}

		return args;
	}

	/**
	 * Executes All given Expressions into Value map
	 */
	public static HashMap<String, Value> execute(HashMap<String, Expression> expressions, Block b) {
		ArrayList<String> names = new ArrayList<String>(expressions.keySet());
		HashMap<String, Value> ret = new HashMap<String, Value>();
		for (String n : names) {
			ret.put(n, expressions.get(n).evaluate(b));
		}
		return ret;
	}

	/**
	 * Executes All given Expressions into Value List
	 */
	public static List<Value> executeList(List<Expression> expressions, Block b) {
		List<Value> args = new ArrayList<Value>();

		for (int i = 0; i < expressions.size(); i++) {
			args.add(expressions.get(i).evaluate(b));
		}

		return args;
	}

	/** Invokes Method with given name & args **/
	public static java.lang.Object invokeMethod(java.lang.Object obj, String methodName, Value[] args) {
		if (obj == null)
			return null;

		Object[] arguments = toArray(args);

		// try to get Class
		Class<?> clazz = obj.getClass();

		if (args.length == 0) {
			// Try to create new Object
			try {
				obj = clazz.getMethod(methodName).invoke(obj);
				return obj;
			} catch (Exception e) {
				return null;
			}
		}

		int try_number = 0;
		// Loop while not created && superclass != null
		while (try_number != 2) { // Loop for different class types

			// Class storage
			Class<?>[] classes = new Class<?>[args.length];

			while ((classes = getSuperClasses(classes, args, try_number != 0)) != null) {
				// Try to create new Object
				try {
					Object ret = clazz.getMethod(methodName, classes).invoke(obj, arguments);
					return ret;
				} catch (Exception e) {
					continue;
				}
			}
			try_number++;
		}
		return null;
	}

	/** Invokes static Method with given name & args **/
	public static java.lang.Object invokeStaticMethod(String className, String methodName, Value[] args) {
		// try to get Class
		Class<?> clazz;
		try {
			clazz = Class.forName(className);
		} catch (Exception e) {
			return null;
		}

		return invokeStaticMethod(clazz, methodName, args);
	}

	/** Invokes static Method with given name & args **/
	public static java.lang.Object invokeStaticMethod(Class<?> clazz, String methodName, Value[] args) {
		Object[] arguments = toArray(args);

		if (args.length == 0) {
			// Try to create new Object
			try {
				Object obj = clazz.getMethod(methodName).invoke(null);
				return obj;
			} catch (Exception e) {
				return null;
			}
		}

		int try_number = 0;
		// Loop while not created && superclass != null
		while (try_number != 2) {

			// Class storage
			Class<?>[] classes = new Class<?>[args.length];

			while ((classes = getSuperClasses(classes, args, try_number != 0)) != null) {
				// Try to create new Object
				try {
					Object obj = clazz.getMethod(methodName, classes).invoke(null, arguments);
					return obj;
				} catch (Exception e) {
					continue;
				}
			}
			try_number++;
		}
		return null;
	}

	/** Returns values Array **/
	public static java.lang.Object[] toArray(Value[] arr) {
		java.lang.Object[] ret = new java.lang.Object[arr.length];
		for (int i = 0; i < ret.length; i++)
			ret[i] = arr[i].value;
		return ret;
	}

	/** Creates new Object **/
	public static java.lang.Object newInstance(String className, Value[] args) {
		Object[] arguments = toArray(args);

		// try to get Class
		Class<?> clazz;
		try {
			clazz = Class.forName(className);
		} catch (Exception e) {
			return null;
		}

		if (args.length == 0) {
			// Try to create new Object
			try {
				Object obj = clazz.getConstructor().newInstance();
				return obj;
			} catch (Exception e) {
				return null;
			}
		}

		int try_number = 0;
		// Loop while not created && superclass != null
		while (try_number != 2) {

			// Class storage
			Class<?>[] classes = new Class<?>[args.length];

			while ((classes = getSuperClasses(classes, args, try_number != 0)) != null) {
				// Try to create new Object
				try {
					Object obj = clazz.getConstructor(classes).newInstance(arguments);
					return obj;
				} catch (Exception e) {
					continue;
				}
			}
			try_number++;
		}
		return null;
	}

	/**
	 * Get's all SuperClasses. primitiveClasses - true = JCobe objects interpret
	 * as Object
	 **/
	private static Class<?>[] getSuperClasses(Class<?>[] classes, Value[] args, boolean primitiveClasses) {
		Class<?>[] temp = copyClasses(classes);

		for (int i = 0; i < args.length; i++) {
			switch (args[i].type) {
				case BOOLEAN:
					classes[i] = boolean.class;
					break;
				case CHARACTER:
					classes[i] = char.class;
					break;
				case DOUBLE:
					classes[i] = double.class;
					break;
				case FUNCTION:
					if (primitiveClasses)
						classes[i] = Object.class;
					else
						classes[i] = Function.class;
					break;
				case OBJECT:
					if (primitiveClasses)
						classes[i] = Object.class;
					else
						classes[i] = AbstractObject.class;
					break;
				case INTEGER:
					classes[i] = int.class;
					break;
				case LONG:
					classes[i] = long.class;
					break;
				case STRING:
					classes[i] = String.class;
					break;
				default:
					if (args[i].value == null)
						classes[i] = java.lang.Object.class;
					else if (classes[i] == null) // No class
						classes[i] = args[i].value.getClass();
					else { // Contains class, get parent
						classes[i] = classes[i].getSuperclass();

						// no parent
						if (classes[i] == null || classes[i] == Object.class)
							return null;
					}
					break;
			}
		}

		// Check if nothing changed
		if (compareClasses(classes, temp))
			return null;

		return classes;
	}

	/** Does compare classes **/
	private static boolean compareClasses(Class<?>[] c1, Class<?>[] c2) {
		for (int i = 0; i < c1.length; i++)
			if (c1[i] != c2[i])
				return false;
		return true;
	}

	/** Does copy classes **/
	private static Class<?>[] copyClasses(Class<?>[] c) {
		Class<?>[] ret = new Class<?>[c.length];
		for (int i = 0; i < c.length; i++)
			ret[i] = c[i];
		return ret;
	}

	/** Returns all classes of given objects **/
	public static Class<?>[] getClasses(Value[] args) {
		Class<?>[] classes = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			switch (args[i].type) {
				case BOOLEAN:
					classes[i] = boolean.class;
					break;
				case CHARACTER:
					classes[i] = char.class;
					break;
				case DOUBLE:
					classes[i] = double.class;
					break;
				case FUNCTION:
					classes[i] = Function.class;
					break;
				case INTEGER:
					classes[i] = int.class;
					break;
				case LONG:
					classes[i] = long.class;
					break;
				case OBJECT:
					classes[i] = bitrate16.JCobe.Types.CodeObject.class;
					break;
				case STRING:
					classes[i] = String.class;
					break;
				default:
					if (args[i].value == null)
						classes[i] = java.lang.Object.class;
					else
						classes[i] = args[i].value.getClass();
					break;
			}
		}
		return classes;
	}

	/** Returns map value by given key. **/
	public static Value getMapValue(HashMap<String, Value> map, String key) {
		Value v = map.get(key);
		if (v == null)
			return new Value();
		return v;
	}

	/** List to value List **/
	public static List<Value> listToValueList(List<?> list) {
		List<Value> l = new ArrayList<Value>();
		for (Object o : list) {
			l.add(new Value(o));
		}
		return l;
	}

	/** Returns all Values from Map **/
	public List<Value> getValues(HashMap<String, Value> values) {
		List<Value> ret = new ArrayList<Value>();
		ArrayList<String> names = new ArrayList<String>();
		for (String n : names)
			ret.add(values.get(n));
		return ret;
	}

	/** cast Object into given class **/
	public static Object cast(String className, Object value) {
		try {
			Class<?> clazz = Class.forName(className);

			return clazz.cast(value);
		} catch (Exception e) {
			return null;
		}
	}

	/** Put's all Objects into value array **/
	public static Value[] objectsToValue(Object[] args) {
		Value[] ret = new Value[args.length];
		for (int i = 0; i < args.length; i++)
			ret[i] = new Value(args[i]);
		return ret;
	}

	/** Prints all hash map **/
	public static void printHashMap(HashMap<String, ?> map) {
		ArrayList<String> names = new ArrayList<String>(map.keySet());
		for (String n : names) {
			System.out.print(n + " : ");
			System.out.println(map.get(n));
		}
	}
}
