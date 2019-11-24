package bitrate16.JCobe;

import java.util.ArrayList;
import java.util.HashMap;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Enviroment.Block.ExecutionListener;
import bitrate16.JCobe.Libraries.StdLib;
import bitrate16.JCobe.Statements.ArrayElementReferenceAssignStatement;
import bitrate16.JCobe.Statements.FunctionDeclarationStatement;
import bitrate16.JCobe.Statements.ObjectReferenceAssignStatement;
import bitrate16.JCobe.Statements.Statement;
import bitrate16.JCobe.Statements.VariableDeclarationStatement;
import bitrate16.JCobe.Syntax.Parser;
import bitrate16.JCobe.Syntax.PostProcessor;
import bitrate16.JCobe.Syntax.PreProcessor;
import bitrate16.JCobe.Syntax.Token;
import bitrate16.JCobe.Syntax.Tokenizer;
import bitrate16.JCobe.Types.Function;
import bitrate16.JCobe.Types.Library;
import bitrate16.JCobe.Values.Value;
import bitrate16.JCobe.Values.Value.Type;

public class JCobe {
	// Костыль. IStatic Accessible instance
	public static JCobe instance;
	// Root block
	private Block rootContext;
	// Parser for code
	Parser parser = new Parser();

	public JCobe() {
		rootContext = new Block();
		rootContext.interpreter = this;
		instance = this;

		loadSharedLibraries();
	}

	/** Loads all Default Shared libraries **/
	private void loadSharedLibraries() {
		// StdLib
		loadLibrary(new StdLib());
	}

	/** Does load Library from class **/
	public void loadLibrary(Library l) {
		HashMap<String, bitrate16.JCobe.Types.AbstractObject> objects = l.getObjects();
		HashMap<String, bitrate16.JCobe.Types.Function> functions = l.getFunctions();

		ArrayList<String> names = null;
		if (objects != null) {
			names = new ArrayList<String>(objects.keySet());
			for (String n : names)
				addObject(n, objects.get(n));
		}

		if (functions != null) {
			names = new ArrayList<String>(functions.keySet());
			for (String n : names)
				addFunction(n, functions.get(n));
		}
	}

	/**
	 * Executes given code
	 */
	public void execute(String code) {

		// 1. Pre-process
		code = PreProcessor.preprocess(code);

		// 2. Generate Tokens
		ArrayList<Token> tokens = Tokenizer.tokenize(code);

		tokens = PostProcessor.postprocess(tokens);

		// 3. Set up Parser
		parser.set(tokens);

		// 4. Parse to Statements
		ArrayList<Statement> statements = parser.parse();

		// 5. Execute
		rootContext.executionListener = null;
		rootContext.execute(null, statements);
		rootContext.localStorage.remove("$return"); // remove

//		System.out.println("Variables:");
//		ArrayList<String> names = new ArrayList<String>(rootContext.localStorage.keySet());
//
//		for (String n : names) {
//			Value v = rootContext.localStorage.get(n);
//			System.out.println(n + " :: " + v.asString());
//		}
	}

	/**
	 * Load code. no static statements execution, only declarations.
	 */
	public void process(String code) {

		// 1. Pre-process
		code = PreProcessor.preprocess(code);

		// 2. Generate Tokens
		ArrayList<Token> tokens = Tokenizer.tokenize(code);

		tokens = PostProcessor.postprocess(tokens);

		// 3. Set up Parser
		parser.set(tokens);

		// 4. Parse to Statements
		ArrayList<Statement> statements = parser.parse();

		// 5. Execute (only declarations)
		rootContext.executionListener = new ExecutionListener() {
			@Override
			public boolean accept(Statement s) {
				if (s instanceof FunctionDeclarationStatement)
					return true;
				if (s instanceof VariableDeclarationStatement)
					return true;
				if (s instanceof ObjectReferenceAssignStatement)
					return true;
				if (s instanceof ArrayElementReferenceAssignStatement)
					return true;
				return false;
			}
		};
		rootContext.execute(null, statements);
		rootContext.localStorage.remove("$return"); // remove
	}

	// - - - - - - - - - - - - - - - Utils - - - - - - - - - - - - - - -
	/**
	 * Declare a new Function in Global context
	 */
	public void addFunction(String name, Function function) {
		addValue(name, function);
	}

	/**
	 * Declare an Object in Global
	 */
	public void addObject(String name, bitrate16.JCobe.Types.AbstractObject obj) {
		addValue(name, obj);
	}

	/**
	 * Adds a new value into Global context
	 */
	public void addValue(String name, Object value) {
		rootContext.putValue(name, new Value(value));
	}

	/**
	 * Returns Value by name
	 */
	public Object getValue(String name) {
		Value v = rootContext.getValue(name);
		if (v == null)
			return null;
		return v.value;
	}

	/**
	 * Return Object by name
	 */
	public bitrate16.JCobe.Types.AbstractObject getObject(String name) {
		Value v = rootContext.getValue(name);
		if (v == null || v.type != Type.OBJECT)
			return null;
		return (bitrate16.JCobe.Types.AbstractObject) v.value;
	}

	/**
	 * Return Function by name
	 */
	public Function getFunction(String name) {
		Value v = rootContext.getValue(name);
		if (v == null || v.type != Type.FUNCTION)
			return null;
		return (Function) v.value;
	}

	/**
	 * Optional
	 */
	public Value get(String name) {
		return rootContext.getValue(name);
	}

	public void set(String name, Value v) {
		rootContext.putValue(name, v);
	}

	public Block getRootContext() {
		return rootContext;
	}

	/** Executes Function by given name, args **/
	public Object executeFunction(String name, Object... args) {
		return this.executeFunction(getFunction(name), args);
	}

	/** Executes given Function with args **/
	public Object executeFunction(Function f, Object... args) {
		if (f == null)
			return null;

		// Create all Values array
		Value[] arguments = new Value[args.length];
		for (int i = 0; i < args.length; i++)
			arguments[i] = new Value(args[i]);

		return f.call(rootContext, arguments);
	}
}
