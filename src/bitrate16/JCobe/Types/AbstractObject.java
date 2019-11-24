package bitrate16.JCobe.Types;

import bitrate16.JCobe.Values.Value;

/**
 * Abstract Object class. used for storing all values in type of HashMap;
 * 
 * <pre>
 * [$expression] - returns some Object from this Object
 * </pre>
 * 
 * @author bitrate16
 *
 */
public abstract class AbstractObject {
	/** Reference by [$name] **/
	public abstract Value getValue(String name);

	/** Reference by [$name] **/
	public abstract Value setValue(String name, Value v);

	/** Reference by self.$name **/
	public abstract Value getProperty(String name);

	/** Reference by self.$name **/
	public abstract Value setProperty(String name, Value v);
}
