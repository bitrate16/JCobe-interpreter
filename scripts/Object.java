package bitrate16.JCobe.Types;

import bitrate16.JCobe.Values.Value;

/**
 * Dynamical Object that contains methods & variables & e.t.c.
 * 
 * @author bitrate16
 *
 */
public abstract class Object {

	public Object() {
	}

	/**
	 * Invoke a function in a object
	 */
	public abstract Value callFunction(String name, Value... args);

	/**
	 * Get a variable from object
	 */
	public abstract Value getVariable(String name);

	public Object clone() {
		final Object reference = this;

		return new Object() {
			@Override
			public Value callFunction(String name, Value... args) {
				return reference.callFunction(name, args);
			}

			@Override
			public Value getVariable(String name) {
				return reference.getVariable(name);
			}
		};
	}
}
