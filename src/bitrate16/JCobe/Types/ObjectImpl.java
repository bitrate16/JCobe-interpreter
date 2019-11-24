package bitrate16.JCobe.Types;

import bitrate16.JCobe.Values.Value;

public class ObjectImpl extends AbstractObject {

	@Override
	public final Value getValue(String name) {
		return new Value(getObjectValue(name));
	}

	@Override
	public final Value setValue(String name, Value v) {
		return new Value(setObjectValue(name, v));
	}

	@Override
	public final Value getProperty(String name) {
		return new Value(getObjectProperty(name));
	}

	@Override
	public final Value setProperty(String name, Value v) {
		return new Value(setObjectProperty(name, v));
	}

	// Implementation
	// [..
	public java.lang.Object getObjectValue(String name) {
		return null;
	}

	public java.lang.Object setObjectValue(String name, Value v) {
		return null;
	}

	public java.lang.Object getObjectProperty(String name) {
		return null;
	}

	public java.lang.Object setObjectProperty(String name, Value v) {
		return null;
	}
	// ..]
}
