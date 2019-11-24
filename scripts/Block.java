package bitrate16.JCobe.Enviroment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bitrate16.JCobe.Statements.ReturnStatement;
import bitrate16.JCobe.Statements.Statement;
import bitrate16.JCobe.Types.RawClass;
import bitrate16.JCobe.Values.Value;

/**
 * Used for executing all Statements & storing variables
 * 
 * @author bitrate16
 *
 */
// XXX: Add multiple executions support & value storage
public class Block {
	// Parent block
	public Block parent;

	// Local variables (and functions) (for current context depth)
	public HashMap<String, Value> localVariables;

	// Local CLassCloader
	public List<RawClass> classLoader;

	public Block() {
		localVariables = new HashMap<String, Value>();
		classLoader = new ArrayList<RawClass>();
	}

	/**
	 * Execute given statements in this block + store all variables
	 * 
	 * @return map of all modified values
	 * @return return value of the block with name $return
	 */
	public HashMap<String, Value> execute(Block parent, List<Statement> statements, HashMap<String, Value> arguments) {
		// -1. Temp values
		ArrayList<String> argumentNames = new ArrayList<String>(arguments.keySet());
		// Backup of parent arguments
		HashMap<String, Value> backup = new HashMap<String, Value>();

		// 0. Backup parent variables
		if (parent != null) {
			for (int i = 0; i < argumentNames.size(); i++) {
				String argName = argumentNames.get(i);

				// Backup value if exist in parent block
				if (parent.localVariables.containsKey(argName)) {
					backup.put(argName, parent.localVariables.get(argName));
				}

				// Put new argument value
				parent.localVariables.put(argName, arguments.get(argName));
			}
		}

		if (parent != null) {
			// 0.5. Create copy of all parent variables
			ArrayList<String> names = new ArrayList<String>(parent.localVariables.keySet());
			for (int i = 0; i < names.size(); i++) {
				this.localVariables.put(names.get(i), parent.localVariables.get(names.get(i)));
			}

			// 0.7. Create classes for classLoader
			this.classLoader.addAll(parent.classLoader);
		}

		Value ret = null;

		// 1. Execute statements (in current context)
		if (statements != null)
			for (int i = 0; i < statements.size(); i++) {
				Statement s = statements.get(i);

				// Check for return
				if (s instanceof ReturnStatement) {
					ReturnStatement rs = (ReturnStatement) s;
					if (rs.ret != null)
						ret = rs.ret.evaluate(this);
					break;
				}
				statements.get(i).execute(this);
			}

		if (parent != null) {
			// 2. Add all modified variables into Map
			HashMap<String, Value> modified = new HashMap<String, Value>();
			ArrayList<String> variablesNames = new ArrayList<String>(this.localVariables.keySet());

			for (int i = 0; i < variablesNames.size(); i++) {
				// If parent not contains or value in parent mismatch to local
				// value
				if (!parent.localVariables.containsKey(variablesNames.get(i))
						|| !parent.getValue(variablesNames.get(i)).value
								.equals(localVariables.get(variablesNames.get(i)).value)) {
					modified.put(variablesNames.get(i), this.localVariables.get(variablesNames.get(i)));
				}
			}

			// Add returned value
			if (ret != null)
				modified.put("$return", ret);

			// 2. Restore all parent variables XXX
			for (int i = 0; i < argumentNames.size(); i++) {
				String argName = argumentNames.get(i);

				// Remove, then put backup
				parent.localVariables.remove(argName);

				if (backup.containsKey(argName))
					parent.localVariables.put(argName, backup.get(argName));
			}

			ArrayList<String> modifiedNames = new ArrayList<String>(modified.keySet());
			// 3. Update Modified values
			for (int i = 0; i < modifiedNames.size(); i++) {
				if (parent.localVariables.containsKey(modifiedNames.get(i))) {
					parent.localVariables.put(modifiedNames.get(i), this.localVariables.get(modifiedNames.get(i)));
				}
			}

			return modified;
		} else {
			if (ret != null)
				localVariables.put("$return", ret);
			return this.localVariables;
		}
	}

	/**
	 * Execute given statements with given parent with given list of argument
	 * names & argument values
	 */
	public HashMap<String, Value> execute(Block parent, List<Statement> statements, List<String> argNames,
			List<Value> arguments) {
		HashMap<String, Value> args = new HashMap<String, Value>();
		for (int i = 0; i < argNames.size(); i++)
			if (i >= arguments.size())
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

	/**
	 * Util: returns local to context variable by name
	 */
	public Value getValue(String name) {
		Value v = this.localVariables.get(name);
		if (v != null)
			return v;
		return new Value(null);
	}

	/**
	 * Clean Context (release)
	 */
	public void clean() {
		localVariables = new HashMap<String, Value>();
		parent = null;
	}
}
