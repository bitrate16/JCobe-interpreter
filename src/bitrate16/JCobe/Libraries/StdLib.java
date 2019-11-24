package bitrate16.JCobe.Libraries;

import java.util.Arrays;
import java.util.HashMap;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Types.AbstractObject;
import bitrate16.JCobe.Types.Function;
import bitrate16.JCobe.Types.Library;
import bitrate16.JCobe.Types.ObjectImpl;
import bitrate16.JCobe.Types.ProxyObject;
import bitrate16.JCobe.Types.StaticJavaObject;
import bitrate16.JCobe.Utils.FileUtils;
import bitrate16.JCobe.Utils.JavaUtils;
import bitrate16.JCobe.Values.Value;

/**
 * Standart Library
 * 
 * @author bitrate16
 *
 */
public class StdLib implements Library {

	@Override
	public HashMap<String, AbstractObject> getObjects() {
		HashMap<String, AbstractObject> objects = new HashMap<String, AbstractObject>();

		objects.put("Java", new ObjectImpl() {
			@Override
			public java.lang.Object getObjectValue(String name) {
				return null;
			}

			@Override
			public java.lang.Object setObjectValue(String name, Value v) {
				return null;
			}

			@Override
			public java.lang.Object getObjectProperty(String name) {
				switch (name) {
					// Create new Instance of java class
					// $0 - classPath, ... - arguments
					case "newInstance":
						return new Function() {
							@Override
							public Value call(Block b, Value... args) {
								if (args.length < 1)
									return new Value();

								return new Value(JavaUtils.newInstance( //
										args[0].stringValue(), //
										Arrays.copyOfRange(args, 1, args.length)));
							}
						};
					// Create new Proxy Instance of java class
					// $0 - classPath
					case "newProxy":
						return new Function() {
							@Override
							public Value call(Block b, Value... args) {
								if (args.length < 1)
									return new Value();

								Class<?> clazz;
								try {
									clazz = Class.forName(args[0].stringValue());
								} catch (Exception e) {
									return new Value();
								}

								return new Value(new ProxyObject(clazz));
							}
						};
					// Cast given Object into given class
					// $0 - classPath, $1 - object
					case "cast":
						return new Function() {
							@Override
							public Value call(Block b, Value... args) {
								if (args.length < 2)
									return new Value();
								try {
									return new Value(JavaUtils.cast(args[0].stringValue(), args[1].value));
								} catch (Exception e) {
									return new Value();
								}
							}
						};
					// Create new Instance of java array
					// $0 - classPath, ... - dimensions
					case "newArray":
						return new Function() {
							@Override
							public Value call(Block b, Value... args) {
								if (args.length < 2)
									return new Value();

								try {
									// Clone dimensions
									int[] dimens = new int[args.length - 1];
									for (int i = 0; i < dimens.length; i++)
										dimens[i] = args[i + 1].intValue();

									return new Value(// new JavaObject(
											JavaUtils.newArray(Class.forName(args[0].stringValue()), dimens)
									// )
									);
								} catch (Exception e) {
									return new Value();
								}
							}
						};
					// Create new Variable with Static reference
					// $0 - classPath
					case "newStatic":
						return new Function() {
							@Override
							public Value call(Block b, Value... args) {
								if (args.length < 1)
									return new Value();

								try {
									Value v = new Value(new StaticJavaObject(Class.forName(args[0].stringValue())));
//									System.out.println(v);
									return v;
								} catch (Exception e) { 
									return new Value();
								}
							}
						};
					// Executes script from path
					// $0 - path
					case "loadScript":
						return new Function() {
							@Override
							public Value call(Block b, Value... args) {
								if (args.length < 1)
									return new Value(false);

								String file = FileUtils.read(args[0].stringValue());
								if (file == null || b.interpreter == null)
									return new Value(false);
//System.err.println("Loading Script: " + args[0].stringValue());
								b.interpreter.execute(file);
								return new Value(true);
							}
						};
				}
				return null;
			}

			@Override
			public java.lang.Object setObjectProperty(String name, Value v) {
				return null;
			}
		});
		return objects;
	}

	@Override
	public HashMap<String, Function> getFunctions() {
		HashMap<String, Function> functions = new HashMap<String, Function>();
		return functions;
	}
}
