package bitrate16.JCobe.Types;

import java.util.List;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Utils.StringUtils;
import bitrate16.JCobe.Values.Value;

/**
 * Array Implementation for JCobe Script
 * 
 * @author bitrate16
 *
 */
public class ArrayObject extends AbstractObject {
	private List<Value> array;

	public ArrayObject(List<Value> array) {
		this.array = array;
	}

	@Override
	public Value getValue(String name) {
		if (StringUtils.isPositiveNumber(name)) {
			int index = StringUtils.parseNumber(name);
			if (index >= array.size())
				return new Value();

			Value v = array.get(index);
			return v;
		}
		return new Value();
	}

	@Override
	public Value setValue(String name, Value v) {
		if (StringUtils.isPositiveNumber(name)) {
			int index = StringUtils.parseNumber(name);
			if (index >= array.size()) {
				// Resize
				int deltaSize = index - array.size();
				for (int i = 0; i < deltaSize - 1; i++)
					array.add(new Value());
				array.add(v);
				return new Value();
			}

			return array.set(index, v);
		}
		return new Value();
	}

	@Override
	public Value getProperty(String name) {
		if (name.equals("length")) // Get Array length
			return new Value(array.size());
		if (name.equals("delete")) // Delete element by Index
			return new Value(new Function() {
				@Override
				public Value call(Block b, Value... args) {
					if (args.length < 1)
						return new Value();

					int index = args[0].intValue();
					if (index < 0 || index >= array.size())
						return new Value();

					return array.remove(index);
				}
			});
		if (name.equals("clear")) // Clean all
			return new Value(new Function() {
				@Override
				public Value call(Block b, Value... args) {
					array.clear();
					return new Value();
				}
			});
		if (name.equals("add")) { // Add new element into array
			return new Value(new Function() {
				@Override
				public Value call(Block b, Value... args) {
					if (args.length < 1)
						return new Value();

					array.add(args[0]);

					return array.get(array.size() - 1);
				}
			});
		}
		if (name.equals("fill")) { // Fill N cells with value (or null)
			return new Value(new Function() {
				@Override
				public Value call(Block b, Value... args) {
					if (args.length < 1)
						return new Value();

					int N = args[0].intValue();

					Value add = args.length < 2 ? new Value() : args[1];

					// Copy value N times
					for (int i = 0; i < N; i++)
						array.add(add.evaluate(b));

					return new Value(1);
				}
			});
		}
		return new Value();
	}

	@Override
	public Value setProperty(String name, Value v) {
		return new Value();
	}

	@Override
	public String toString() {
		return array.toString();
	}
}
