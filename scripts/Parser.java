package bitrate16.JCobe.Syntax;

import java.util.ArrayList;
import java.util.List;

import bitrate16.JCobe.Exceptions.ParserException;
import bitrate16.JCobe.Expressions.ArrayReferenceExpression;
import bitrate16.JCobe.Expressions.CodeFunctionInvocationExpression;
import bitrate16.JCobe.Expressions.CodeFunctionObjectExpression;
import bitrate16.JCobe.Expressions.Expression;
import bitrate16.JCobe.Expressions.JCobeObjectArrayInitializeExpression;
import bitrate16.JCobe.Expressions.JCobeObjectInitializeExpression;
import bitrate16.JCobe.Expressions.JavaObjectArrayInitializeExpression;
import bitrate16.JCobe.Expressions.JavaObjectInitializeExpression;
import bitrate16.JCobe.Expressions.ObjectReferenceException;
import bitrate16.JCobe.Expressions.OperatorExpression;
import bitrate16.JCobe.Statements.Statement;
import bitrate16.JCobe.Syntax.Token.TokenType;
import bitrate16.JCobe.Values.Value;

/**
 * Generate AST (Abstract Syntax Tree)
 * 
 * @author bitrate16
 *
 */
public class Parser {
	// List of all parsed tokens
	public List<Token> tokens;
	// Current token
	public int position;

	public Parser(List<Token> tokens) {
		set(tokens);
	}

	public Parser() {
	}

	/**
	 * Set new tokens for parsing
	 */
	public void set(List<Token> tokens) {
		this.tokens = tokens;
		this.position = 0;
	}

	/**
	 * Does parsing all code to list of Statements
	 */
	public ArrayList<Statement> parse() {
		ArrayList<Statement> statements = new ArrayList<Statement>();

		// --- Parser goes here ---
		Statement temp = statement();

		while (temp != null) {
			statements.add(temp);
			temp = statement();
		}

		return statements;
	}

	/**
	 * Does parsing a single statement (null if end)
	 */
	public Statement statement() {
		// XXX: Complete
		if (match("if")) {
			// Match IF
		} else if (match("while")) {
			// Match WHILE
		} else if (match("for")) {
			// Match FOR
		} else if (match("switch")) {
			// Match SWITCH/CASE
		} else if (match("object")) {
			// Match OBJECT CLASS DECLARATION
		} else if (match("constructor")) {
			// Match CONSTRUCTOR
		} else if (match("function")) {
			// Match FUNCTION
		} else if (match("return")) {
			// Match RETURN
		} else if (match("var")) {
			// Match KEYWORRD | OBJECT REFERENCE | VARIABLE DECLARATION
		} else {
			// Could not parse statement (Error | EOF)
			return null;
		}
		return null;
	}

	/**
	 * Does parsing block to statement list
	 */
	public ArrayList<Statement> parseBlock() {
		consume("{");

		Parser parser = new Parser();
		// Calculate body bounds
		int index = position;
		int braces = 1;

		while (index < tokens.size()) {
			if (tokens.get(index).token.equals("{"))
				braces++;
			if (tokens.get(index).token.equals("}"))
				braces--;
			if (braces == 0)
				break;
			index++;
		}

		List<Token> body = tokens.subList(position, index);

		position = index;

		consume("}");

		parser.set(body);

		// Parse body
		return parser.parse();
	}

	/**
	 * Does parsing arguments block to String list
	 */
	public ArrayList<String> parseArguments() {
		consume("(");

		ArrayList<String> args = new ArrayList<String>();

		if (!match(")")) {
			args.add(consume(TokenType.KEYWORD).token);

			while (match(",")) {
				args.add(consume(TokenType.KEYWORD).token);
			}

			consume(")");
		}

		return args;
	}

	// - - - - - - - - - - - Primitive expressions - - - - - - - - - - - - -

	/**
	 * Parses a single Expression
	 */
	public Expression expression() {
		return operator();
	}

	/**
	 * Builds an Operation Expression
	 */
	public Expression operator() {
		Expression expression = atomic();

		// Keep building Expressions while contains operations
		while (match(TokenType.OPERATOR)) {
			// Parse operator
			String operator = last(1).token;
			// Get next Expression
			Expression right = atomic();
			expression = new OperatorExpression(expression, operator, right);
		}
		return expression;
	}

	/**
	 * Hightest-Level Expression. Simple type
	 */
	public Expression atomic() {
		if (match("new", TokenType.KEYWORD)) {

			// New JCobe Object creation
			String name = last(1).token;

			// Check for Array type
			if (match("[")) {
				putBack(1);

				List<Expression> dimensions = parseArrayExpressions();

				return new JCobeObjectArrayInitializeExpression(name, dimensions);

			} else {
				ArrayList<Expression> arguments = parseArgumentExpressions();

				// Create new CodeFunction & add to Statements
				return new JCobeObjectInitializeExpression(name, arguments);
			}

		} else if (match("java")) {

			// New Java Object creation

			// Parse full class name (path)
			ArrayList<String> classPath = new ArrayList<String>();

			classPath.add(consume(TokenType.KEYWORD).token);

			while (match("."))
				classPath.add(consume(TokenType.KEYWORD).token);

			if (match("[")) {
				putBack(1);

				List<Expression> dimensions = parseArrayExpressions();

				return new JavaObjectArrayInitializeExpression(classPath, dimensions);
			} else {

				ArrayList<Expression> arguments = parseArgumentExpressions();

				return new JavaObjectInitializeExpression(classPath, arguments);
			}

		} else if (match("function")) {

			// New Function declaration

			ArrayList<String> argumentNames = parseArguments();

			ArrayList<Statement> body = parseBlock();

			// Create new CodeFunction & add to Statements
			return new CodeFunctionObjectExpression(argumentNames, body);

		} else if (match("null")) {
			// Declared Keyword
			return new Value();
		} else if (match(TokenType.KEYWORD)) {

			putBack(1);

			// Start constructing Object 'myobject.insideObject.someFUnc()'
			ArrayList<String> objectPath = new ArrayList<String>();

			objectPath.add(consume(TokenType.KEYWORD).token);

			while (match("."))
				objectPath.add(consume(TokenType.KEYWORD).token);

			// Match all types of references (array, function, e.t.c.)
			if (match("(")) {
				// function
				putBack(1);

				ArrayList<Expression> argExpr = parseArgumentExpressions();

				return new CodeFunctionInvocationExpression(objectPath, argExpr);
			} else if (match("[")) {
				// Array reference
				putBack(1);

				ArrayList<Expression> argExpr = parseArrayExpressions();

				return new ArrayReferenceExpression(objectPath, argExpr);
			} else {
				return new ObjectReferenceException(objectPath);
			}

		} else if (match(TokenType.NUMBER)) {
			// Number (integer)
			return new Value(Integer.parseInt(last(1).token));
		} else if (match(TokenType.DOUBLE)) {
			// Number (double)
			return new Value(Double.parseDouble(last(1).token));
		} else if (match(TokenType.INTEGER)) {
			// Number (int)
			return new Value(Integer.parseInt(last(1).token));
		} else if (match(TokenType.STRING)) {
			// String
			return new Value(last(1).token);
		} else if (match(TokenType.CHAR)) {
			// Character
			return new Value(last(1).token.charAt(0));
		} else if (match(TokenType.BOOLEAN)) {
			// Boolean
			return new Value(last(1).token.equals("true"));
		} else if (match("(")) {
			// Parse Expression in braces
			Expression expression = expression();
			// Check if Braces are closed
			consume(")");
			return expression;
		} else
			return new Value(null);
	}

	// - - - - - - - - - - - - - - - UTILS - - - - - - - - - - - - - - - - -
	/**
	 * Does parsing next tokens to list of arguments
	 */
	public ArrayList<Expression> parseArgumentExpressions() {
		ArrayList<Expression> exp = new ArrayList<Expression>();

		consume("(");

		if (!match(")")) {
			exp.add(expression());

			while (match(","))
				exp.add(expression());

			consume(")");
		}

		return exp;
	}

	/**
	 * Does parsing all array reference block '[1][value]...'
	 */
	private ArrayList<Expression> parseArrayExpressions() {
		ArrayList<Expression> exp = new ArrayList<Expression>();

		while (match("[")) {
			exp.add(expression());
			consume("]");
		}

		return exp;
	}

	/**
	 * Checks for matching
	 */
	public boolean match(TokenType type) {
		if (get(0) == null)
			return false;
		if (get(0).type != type)
			return false;
		position++;
		return true;
	}

	/**
	 * Match by 2 Tokens
	 */
	public boolean match(TokenType type1, TokenType type2) {
		if (get(0) == null || get(1) == null)
			return false;
		if (get(0).type != type1)
			return false;
		if (get(1).type != type2)
			return false;
		position += 2;
		return true;
	}

	/**
	 * Match by 3 Tokens
	 */
	public boolean match(TokenType type1, TokenType type2, TokenType type3) {
		if (get(0) == null || get(1) == null || get(2) == null)
			return false;
		if (get(0).type != type1)
			return false;
		if (get(1).type != type2)
			return false;
		if (get(2).type != type2)
			return false;
		position += 3;
		return true;
	}

	/**
	 * Match by Token, String
	 */
	public boolean match(TokenType type1, String token2) {
		if (get(0) == null || get(1) == null)
			return false;
		if (get(0).type != type1)
			return false;
		// if (get(1).type != TokenType.KEYWORD)
		// return false;
		if (!get(1).token.equals(token2))
			return false;
		position += 2;
		return true;
	}

	/**
	 * Match by String, Token
	 */
	public boolean match(String token1, TokenType type2) {
		if (get(0) == null || get(1) == null)
			return false;
		// if (get(0).type != TokenType.KEYWORD)
		// return false;
		if (!get(0).token.equals(token1))
			return false;
		if (get(1).type != type2)
			return false;
		position += 2;
		return true;
	}

	/**
	 * Match by 2 Tokens
	 */
	public boolean match(String token1, String token2) {
		if (get(0) == null || get(1) == null)
			return false;
		// if (get(0).type != TokenType.KEYWORD)
		// return false;
		if (!get(0).token.equals(token1))
			return false;
		// if (get(1).type != TokenType.KEYWORD)
		// return false;
		if (!get(1).token.equals(token2))
			return false;
		position += 2;
		return true;
	}

	/**
	 * Checks for match by String - WORD name value
	 */
	public boolean match(String token) {
		if (get(0) == null)
			return false;
		// if (get(0).type != TokenType.KEYWORD)
		// return false;
		if (!get(0).token.equals(token))
			return false;
		position++;
		return true;
	}

	/**
	 * Gets Token by offset
	 */
	public Token get(int offset) {
		if (position + offset >= tokens.size()) {
			return new Token("", TokenType.EOF);
		}
		return tokens.get(offset + position);
	}

	/**
	 * Consumes the next Token if it is given type
	 */
	public Token consume(TokenType type) {
		if (get(0) == null || get(0).type != type)
			throw new ParserException("Expected " + type);
		return tokens.get(position++);
	}

	/**
	 * Moves position back by offset
	 */
	public Token putBack(int offset) {
		if (position - offset < 0) {
			position = 0;
			return tokens.get(0);
		}
		position -= offset;
		return get(0);
	}

	/**
	 * Consumes the next Word token if it is with given name
	 */
	public Token consume(String token) {
		if (!match(token))
			throw new ParserException("Expected " + token);
		return last(1);
	}

	/**
	 * Returns Token, offset from the End
	 */
	public Token last(int offset) {
		return tokens.get(position - offset);
	}
}
