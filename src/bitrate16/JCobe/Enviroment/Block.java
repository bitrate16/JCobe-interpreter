package bitrate16.JCobe.Enviroment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bitrate16.JCobe.JCobe;
import bitrate16.JCobe.Statements.Statement;
import bitrate16.JCobe.Values.Value;

/**
 * Used for executing all Statements & storing variables
 * 
 * @author bitrate16
 *
 */
public class Block {
	// Local storage for all modified variables (For currently this context)
	public HashMap<String, Value> localStorage;
	// Interpreter instance. for your use
	public JCobe interpreter;
	// Execution listener
	public ExecutionListener executionListener;
	// Store parent for a while
	public Block parent;
	// Store list of all values, local to THIS context. They won't be updated
	// in parent after execution.
	public List<String> localValues;

	public Block() {
		this.localStorage = new HashMap<String, Value>();
		localValues = new ArrayList<String>();
	}

	/**
	 * Execute given statements in this block + store all variables
	 * 
	 * @return map of all modified values
	 * @return return value of the block with name $return
	 */
	public HashMap<String, Value> execute(Block parent, List<Statement> statements, HashMap<String, Value> arguments) {
		this.parent = parent;

		if (parent != null)
			interpreter = parent.interpreter;

		// I. Copy Values from parent (nope)
		// if (parent != null)
		// this.copyValues(parent);

		// II. Append all arguments
		this.append(arguments);

		// III. Execute all statements
		for (Statement s : statements) {

			// Execution listener
			if (executionListener != null) {
				if (executionListener.stop(s))
					break;
				if (!executionListener.accept(s))
					continue;
			}

			s.execute(this);
		}

		// Notify listener that execution is ended
		if (executionListener != null)
			executionListener.onEnd(parent);

		// IV. Clone all mismatching Values
		HashMap<String, Value> mismatch;

		if (parent != null)
			mismatch = this.compare(parent);
		else
			mismatch = copy();

		// V. Modify all parent Values
		// if (enableParentExtract)
		if (parent != null)
			this.extractValues(parent);

		return mismatch;
	}

	/**
	 * Execute given statements with given parent with given list of argument
	 * names & argument values
	 */
	public HashMap<String, Value> execute(Block parent, List<Statement> statements, List<String> argNames,
			List<Value> arguments) {
		HashMap<String, Value> args = new HashMap<String, Value>();
		for (int i = 0; i < argNames.size(); i++)
			if (i < arguments.size())
				args.put(argNames.get(i), arguments.get(i));

		return execute(parent, statements, args);
	}

	/**
	 * Executes a single statement list with no args
	 */
	public HashMap<String, Value> execute(Block parent, List<Statement> statement) {
		return execute(parent, statement, new HashMap<String, Value>());
	}

	/**
	 * Executes a single statement with no args
	 */
	public HashMap<String, Value> execute(Block parent, Statement statement) {
		ArrayList<Statement> statem = new ArrayList<Statement>();
		statem.add(statement);

		return execute(parent, statem);
	}

	// - - - - - - - - - - - - - - Utils - - - - - - - - - - - - - - -
	/** Returns Object with given name **/
	public Value getValue(String name) {
		// if parent exists && this block does not contain expected value,
		// return value from parent, else return value from here
		if (parent != null && !containsValue(name)) // Deep raiser
			return parent.getValue(name);

		Value v = localStorage.get(name);
		return v != null ? v : new Value();
	}

	/** Set's value by name **/
	public void putValue(String name, Value value) {
		Block parent = this;
		boolean inserted = false;
		// Loop while:
		// Found Block, containing value with this name (Do overwrite value)
		// Fount null Block
		while (parent != null) {
			if (parent.containsValue(name)) {
				parent.putForcibly(name, value);
				inserted = true;
				break;
			}
			parent = parent.parent;
		}
		// Put new Value to self
		if (!inserted)
			this.putForcibly(name, value);
	}

	/** Same as putValue, but first checks parent **/
	public void putParentValue(String name, Value value) {
		Block parent = this;
		boolean inserted = false;
		// Loop while:
		// Found Block, containing value with this name (Do overwrite value)
		// Fount null Block
		while ((parent = parent.parent) != null) {
			if (parent.containsValue(name)) {
				parent.putForcibly(name, value);
				inserted = true;
				break;
			}
		}
		// Put new Value to self
		if (!inserted)
			this.putForcibly(name, value);
	}

	/** Returns true if current local Storage contains value with given name **/
	public boolean containsValue(String name) {
		return localStorage.containsKey(name);
	}

	/** Copies all variables from given context Block into this **/
	public void copyValues(Block b) {
		ArrayList<String> nameIter = new ArrayList<String>(b.localStorage.keySet());

		for (int i = 0; i < nameIter.size(); i++)
			this.localStorage.put(nameIter.get(i), b.localStorage.get(nameIter.get(i)));
	}

	/**
	 * Overwrite all variables, containing in given Block by variables in
	 * current Block. Overwrite only if <code>this.value != block.value.</code>
	 * 
	 * <code><pre>
	 * $name - Value name
	 * 
	 * if(this.contains($name) && block.contains($name))
	 *		block.put(this.get($name))
	 * </pre></code>
	 **/
	public void extractValues(Block b) {
		ArrayList<String> nameIter = new ArrayList<String>(this.localStorage.keySet());

		for (int i = 0; i < nameIter.size(); i++) {
			String name = nameIter.get(i);

			// Prevent from adding local values
			if (!localValues.contains(name))
				if (this.containsValue(name) && b.containsValue(name))
					if (!this.getValue(name).equals(b.getValue(name)))
						// System.out.println("Replace '" + name + "' from '" +
						// b.getValue(name).asString() + "' to '"
						// + this.getValue(name).asString() + "'");
						b.putValue(name, this.getValue(name));
		}

		// Make parent Extract values too
		if (b.parent != null)
			// System.out.println("Extracting parent");
			b.extractValues(b.parent);
	}

	/** Does copy all values from given Block **/
	public void copy(Block b) {
		ArrayList<String> nameIter = new ArrayList<String>(b.localStorage.keySet());

		for (int i = 0; i < nameIter.size(); i++) {
			String name = nameIter.get(i);
			this.putValue(name, b.getValue(name));
		}
	}

	/**
	 * Compares all values in this & given Value.
	 * 
	 * @return HashMap with all mismatching values
	 */
	public HashMap<String, Value> compare(Block b) {
		ArrayList<String> nameIter = new ArrayList<String>(localStorage.keySet());

		HashMap<String, Value> mismatch = new HashMap<String, Value>();

		for (int i = 0; i < nameIter.size(); i++) {
			String name = nameIter.get(i);
			Value v = this.getValue(name);
			if (!b.containsValue(name) || !b.getValue(name).equals(v))
				mismatch.put(name, v);
		}

		return mismatch;
	}

	/** Does add new Values into current context **/
	public void append(HashMap<String, Value> values) {
		this.localStorage.putAll(values);
	}

	/** Does creating full copy of the values **/
	public HashMap<String, Value> copy() {
		HashMap<String, Value> copy = new HashMap<String, Value>();

		ArrayList<String> nameIter = new ArrayList<String>(this.localStorage.keySet());

		for (String s : nameIter)
			copy.put(s, this.getValue(s));

		return copy;
	}

	/**
	 * Returns true if current local storage or full parent hierarchy contains
	 * key
	 **/
	public boolean contains(String key) {
		if (this.containsValue(key))
			return true;
		if (parent == null)
			return false;

		return parent.contains(key);
	}

	// Listener
	public static class ExecutionListener {
		/** Return true, if accept statement. false if break execution **/
		public boolean stop(Statement s) {
			return false;
		}

		/** Return true, if accept statement to execute **/
		public boolean accept(Statement s) {
			return true;
		}

		/** When Statements execution is ended **/
		public void onEnd(Block parent) {
		}
	}

	/**
	 * Creates full copy of the context^ values, parent, execution listeners
	 **/
	public Block duplicate() {
		Block copy = new Block();
		copy.copy(this);
		copy.parent = parent;
		copy.executionListener = executionListener;
		return copy;
	}

	/**
	 * Forcibly (принудительно) put value into current block, not touch parent
	 **/
	public void putForcibly(String name, Value value) {
		// Mark this value as local to this context
		localValues.add(name);
		this.localStorage.put(name, value);
	}

	/**
	 * Forcibly (принудительно) put all values into current block, not touch
	 * parent
	 **/
	public void putAllForcibly(HashMap<String, Value> values) {
		// Mark this value as local to this context
		localValues.addAll(new ArrayList<String>(values.keySet()));
		this.localStorage.putAll(values);
	}

	/**
	 * Overriden by CodeObject. Returns CodeObject's context, else request
	 * parent to do this
	 **/
	public Value getObjectContext() {
		if (parent != null)
			return parent.getObjectContext();
		return null;
	}
}
