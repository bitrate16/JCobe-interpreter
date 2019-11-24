package bitrate16.JCobe.Syntax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bitrate16.JCobe.Exceptions.ParserException;
import bitrate16.JCobe.Statements.*;
import bitrate16.JCobe.Expressions.*;
import bitrate16.JCobe.Expressions.RPNExpression.Atom;
import bitrate16.JCobe.Syntax.Token.TokenType;
import bitrate16.JCobe.Types.StructurePrototype;
import bitrate16.JCobe.Utils.StringUtils;
import bitrate16.JCobe.Values.Value;

/**
 * Generate AST (Abstract Syntax Tree)
 * 
 * @author bitrate16
 *
 */
// ¯\_(ツ)_/¯
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
		if (match("if")) {
			// Match IF
			consume("(");
			Expression expr = expression();
			consume(")");

			List<Statement> ifBody = parseBlock();
			List<Statement> elseBody = null;

			// Check for Else
			if (match("else")) {
				elseBody = parseBlock();
			}

			return new IfElseStatement(expr, ifBody, elseBody);

		} else if (match("while")) {
			// Match WHILE
			consume("(");
			Expression condition = expression();
			consume(")");

			List<Statement> body = parseBlock();

			return new WhileStatement(condition, body);

		} else if (match("for")) {
			// Match FOR
			Statement initialization;
			Expression termination;
			Statement increment;
			consume("(");
			// If no Statement
			if (match(";"))
				initialization = new EmptyStatement();
			else {
				initialization = minStatement();
				consume(";");
			}

			// If no Expression
			if (match(";"))
				termination = new Value(true);
			else {
				termination = expression();
				consume(";");
			}

			// If no Statement
			if (match(")"))
				increment = new EmptyStatement();
			else {
				increment = minStatement();
				consume(")");
			}

			List<Statement> body = parseBlock();

			return new ForStatement(initialization, termination, increment, body);

		} else if (match("switch")) {
			// Match SWITCH/CASE
			List<CaseStatement> cases = new ArrayList<CaseStatement>();

			consume("(");
			Expression condition = expression();
			consume(")");

			consume("{");

			while (match("case")) {
				Expression caze = expression();
				consume(":");
				List<Statement> body = parseBlock();

				cases.add(new CaseStatement(caze, body));
			}
			consume("}");

			return new SwitchStatement(condition, cases);

		} else if (match("function")) {
			// Match FUNCTION
			return new FunctionDeclarationStatement(consume(TokenType.KEYWORD).token, parseArguments(), parseBlock());

		} else if (match("struct")) {
			// Object declaration
			HashMap<String, Expression> objectValues = new HashMap<String, Expression>();

			String name = consume(TokenType.KEYWORD).token;

			List<String> superStructures = new ArrayList<String>();
			// Add extendible structures
			if (match(":")) {
				// Parse all super-structs
				while (match(TokenType.KEYWORD)) {
					superStructures.add(last(1).token);
					match(",");
				}
			}

			consume("{");

			if (!match("}")) {
				String valueName = consume(TokenType.KEYWORD).token;
				consume(":");
				Expression value = expression();
				objectValues.put(valueName, value);

				while (match(",")) {
					valueName = consume(TokenType.KEYWORD).token;
					consume(":");
					value = expression();
					objectValues.put(valueName, value);
				}
				consume("}");
			}

			return new StructurePrototype(name, superStructures, objectValues);
		} else if (match("var")) {
			// Match VARIABLE DECLARATION
			// Parse all declarations
			List<Statement> statements = new ArrayList<Statement>();

			while (match(TokenType.KEYWORD)) {
				putBack(1);
				String name = consume(TokenType.KEYWORD).token;

				Expression value = null;
				if (match(TokenType.SET))
					value = expression();

				match(",");
				statements.add(new VariableDeclarationStatement(name, value));
			}
			consume(";");

			return new MultipleStatement(statements);

		} else if (match("return")) {
			// Match RETURN
			if (match(";"))
				return new ReturnStatement(null);
			ReturnStatement rs = new ReturnStatement(expression());
			consume(";");

			return rs;

		} else if (match("break")) {
			// Match BREAK
			consume(";");

			return new BreakStatement();

		} else if (match("continue")) {
			// Match BREAK
			consume(";");

			return new ContinueStatement();

		} else if (match(TokenType.KEYWORD)) {
			// Match KEYWORRD | OBJECT REFERENCE | VARIABLE DECLARATION

			putBack(1);
			// I. Parse as Expression
			Expression line = expression();

			if (match("=")) {
				// II. Value
				Expression value = expression();
				consume(";");

				// II. Get Expression Type
				if (line instanceof ObjectReferenceExpression) {
					// Assign as variable
					ObjectReferenceExpression e = (ObjectReferenceExpression) line;

					return new ObjectReferenceAssignStatement(e, value);
				} else if (line instanceof ObjectElementReferenceExpression) {
					// Assign as Object subObject
					ObjectElementReferenceExpression e = (ObjectElementReferenceExpression) line;

					return new ArrayElementReferenceAssignStatement(e, value);
				} else {
					// Function call
					// Just Assign value to result of return
					return new RawValueAssingStatement(line, value);
				}
			} else if (match("+=") || match("-=") || match("*=") || match("/=")) {
				// Increment with set

				String operator = last(1).token.charAt(0) + "";

				// II. Value
				Expression value = expression();
				consume(";");

				// II. Get Expression Type
				if (line instanceof ObjectReferenceExpression) {
					// Assign as variable
					ObjectReferenceExpression e = (ObjectReferenceExpression) line;

					value = new OperatorExpression(e, value, operator);

					return new ObjectReferenceAssignStatement(e, value);
				} else if (line instanceof ObjectElementReferenceExpression) {
					// Assign as Object subObject
					ObjectElementReferenceExpression e = (ObjectElementReferenceExpression) line;

					value = new OperatorExpression(e, value, operator);

					return new ArrayElementReferenceAssignStatement(e, value);
				} else {
					// Function call
					// Just Assign value to result of return
					return new RawValueAssingStatement(line, value);
				}
			} else {
				consume(";");
				return new ExpressionStatement(line);
			}
		}
		if (match("++") || match("--")) {
			// Pre-Increment\Decrement
			putBack(1);

			Expression exp = expression();
			consume(";");

			return new ExpressionStatement(exp);
		} else {
			// Could not parse statement (Error | EOF)
			return null;
		}
		// return null;
	}

	/**
	 * Does parsing a single statement (for loops) (null if end)
	 */
	public Statement minStatement() {
		if (match("var")) {
			// Match VARIABLE DECLARATION
			// Parse all declarations
			List<Statement> statements = new ArrayList<Statement>();

			while (match(TokenType.KEYWORD)) {
				putBack(1);
				String name = consume(TokenType.KEYWORD).token;

				Expression value = null;
				if (match(TokenType.SET))
					value = expression();

				match(",");
				statements.add(new VariableDeclarationStatement(name, value));
			}

			return new MultipleStatement(statements);

		} else if (match(TokenType.KEYWORD)) {
			// Match KEYWORRD | OBJECT REFERENCE | VARIABLE DECLARATION

			putBack(1);
			// I. Parse as Expression
			Expression line = expression();

			if (match("=")) {
				// II. Value
				Expression value = expression();

				// II. Get Expression Type
				if (line instanceof ObjectReferenceExpression) {
					// Assign as variable
					ObjectReferenceExpression e = (ObjectReferenceExpression) line;

					return new ObjectReferenceAssignStatement(e, value);
				} else if (line instanceof ObjectElementReferenceExpression) {
					// Assign as Object subObject
					ObjectElementReferenceExpression e = (ObjectElementReferenceExpression) line;

					return new ArrayElementReferenceAssignStatement(e, value);
				} else {
					// Function call
					// Just Assign value to result of return
					return new RawValueAssingStatement(line, value);
				}
			} else if (match("+=") || match("-=") || match("*=") || match("/=")) {
				// Increment with set

				String operator = last(1).token.charAt(0) + "";

				// II. Value
				Expression value = expression();

				// II. Get Expression Type
				if (line instanceof ObjectReferenceExpression) {
					// Assign as variable
					ObjectReferenceExpression e = (ObjectReferenceExpression) line;

					value = new OperatorExpression(e, value, operator);

					return new ObjectReferenceAssignStatement(e, value);
				} else if (line instanceof ObjectElementReferenceExpression) {
					// Assign as Object subObject
					ObjectElementReferenceExpression e = (ObjectElementReferenceExpression) line;

					value = new OperatorExpression(e, value, operator);

					return new ArrayElementReferenceAssignStatement(e, value);
				} else {
					// Function call
					// Just Assign value to result of return
					return new RawValueAssingStatement(line, value);
				}
			} else {
				return new ExpressionStatement(line);
			}
		}
		if (match("++") || match("--")) {
			// Pre-Increment\Decrement
			putBack(1);

			Expression exp = expression();

			return new ExpressionStatement(exp);
		} else {
			// Could not parse statement (Error | EOF)
			return new EmptyStatement();
		}
		// return null;
	}

	/**
	 * Does parsing block to statement list
	 */
	public List<Statement> parseBlock() {
		if (match("{")) {
			// { <body> }

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

			Parser parser = new Parser();

			parser.set(body);

			// Parse body
			return parser.parse();
		} else {
			// <single statement>
			List<Statement> body = new ArrayList<Statement>();
			body.add(statement());
			return body;
		}
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

	/** Parse Atom expression **/
	public Expression atom() {
		Expression expression;
		if (match("--"))
			expression = new PreDecremenetExpression(atomic());
		else if (match("++"))
			expression = new PreIncrementExpression(atomic());
		else
			expression = atomic();

		if (match("--"))
			expression = new PostDecremenetExpression(expression);
		else if (match("++"))
			expression = new PostIncrementExpression(expression);
		return expression;
	}

	/**
	 * Builds an Operation Expression
	 */
	public Expression operator() {
		List<Atom> expression = new ArrayList<Atom>();
		Expression temp = atom();
		expression.add(new Atom(temp));

		if (match(TokenType.OPERATOR))
			do {
				expression.add(new Atom(last(1).token));
				expression.add(new Atom(atom()));
			} while (match(TokenType.OPERATOR));
		else
			return temp;
		return new RPNExpression(expression);
	}

	/**
	 * Builds an Operation Expression
	 */
	public Expression operatorq() {
		Expression expression;
		if (match("--"))
			expression = new PreDecremenetExpression(atomic());
		else if (match("++"))
			expression = new PreIncrementExpression(atomic());
		else
			expression = atomic();

		if (match("--"))
			expression = new PostDecremenetExpression(expression);
		else if (match("++"))
			expression = new PostIncrementExpression(expression);

		// Keep building Expressions while contains operations
		while (match(TokenType.OPERATOR)) {
			// Parse operator
			String operator = last(1).token;
			// Get next Expression
			Expression right;

			if (match("--"))
				right = new PreDecremenetExpression(atomic());
			else if (match("++"))
				right = new PreIncrementExpression(atomic());
			else
				right = atomic();

			if (match("--"))
				right = new PostDecremenetExpression(expression);
			else if (match("++"))
				right = new PostIncrementExpression(expression);

			expression = new OperatorExpression(expression, right, operator);
		}
		return expression;
	}

	/**
	 * Hightest-Level Expression. Simple type
	 */
	public Expression atomic() {
		// Result expression to return
		Expression result = new Value();

		if (matchFull("{", TokenType.DELIMITER)) {
			// Object declaration
			HashMap<String, Expression> objectValues = new HashMap<String, Expression>();

			if (!match("}")) {
				String valueName = consume(TokenType.KEYWORD).token;
				consume(":");
				Expression value = expression();
				objectValues.put(valueName, value);

				while (match(",")) {
					valueName = consume(TokenType.KEYWORD).token;
					consume(":");
					value = expression();
					objectValues.put(valueName, value);
				}
				consume("}");
			}

			result = new JCobeObjectExpression(objectValues);
		} else if (matchFull("[", TokenType.DELIMITER)) {
			// Linear array declaration
			List<Expression> arrayValues = new ArrayList<Expression>();

			if (!match("]")) {
				Expression value = expression();
				arrayValues.add(value);

				while (match(",")) {
					value = expression();
					arrayValues.add(value);
				}
				consume("]");
			}

			result = new JCobeLinearArrayExpression(arrayValues);
		} else if (match("struct")) {
			// Object declaration
			HashMap<String, Expression> objectValues = new HashMap<String, Expression>();

			List<String> superStructures = new ArrayList<String>();
			// Add extendible structures
			if (match(":")) {
				// Parse all super-structs
				while (match(TokenType.KEYWORD)) {
					superStructures.add(last(1).token);
					match(",");
				}
			}

			consume("{");

			if (!match("}")) {
				String valueName = consume(TokenType.KEYWORD).token;
				consume(":");
				Expression value = expression();
				objectValues.put(valueName, value);

				while (match(",")) {
					valueName = consume(TokenType.KEYWORD).token;
					consume(":");
					value = expression();
					objectValues.put(valueName, value);
				}
				consume("}");
			}

			return new StructurePrototype(null, superStructures, objectValues);
		} else if (match("function")) {

			// New Function declaration

			List<String> argumentNames = parseArguments();

			List<Statement> body = parseBlock();

			// Create new CodeFunction & add to Statements
			result = new FunctionExpression(argumentNames, body);

		} else if (matchFull("null", TokenType.KEYWORD)) {
			// Declared Keyword
			return new Value();
		} else if (match(TokenType.KEYWORD)) {
			// 'something.'

			String name = last(1).token;
			Expression ret = new ObjectReferenceExpression(null, name);

			result = ret;

		} else if (match(TokenType.NUMBER)) {
			// Number (integer)
			result = new Value(Integer.parseInt(last(1).token));
		} else if (match(TokenType.DOUBLE)) {
			// Number (double)
			result = new Value(Double.parseDouble(last(1).token));
		} else if (match(TokenType.INTEGER)) {
			// Number (int)
			result = new Value(Integer.parseInt(last(1).token));
		} else if (match(TokenType.LONG)) {
			// Number (long)
			result = new Value(StringUtils.parseLong(last(1).token));
		} else if (match(TokenType.BYTE)) {
			// Number (byte)
			result = new Value(StringUtils.parseByte(last(1).token));
		} else if (match(TokenType.STRING)) {
			// String
			Expression ret = new Value(last(1).token);

			result = ret;

		} else if (match(TokenType.CHAR)) {
			// Character
			result = new Value(last(1).token.charAt(0));
		} else if (match(TokenType.BOOLEAN)) {
			// Boolean
			result = new Value(last(1).token.equals("true"));
		} else if (match("(")) {
			// Parse Expression in braces
			Expression expression = expression();
			// Check if Braces are closed
			consume(")");

			result = expression;
		} else if (match("-")) {
			// Negative value
			result = new NegativeValueExpression(expression());
		} else if (match("~")) {
			// Negative value
			result = new OperatorExpression(new Value(), expression(), "~");
		} else

		// Match structure: '<exp> ? <exp> : <exp>'
		if (match("?")) {
			// Ternary operator
			Expression ret1 = expression();
			consume(":");
			Expression ret2 = expression();
			result = new TernaryIfExpression(result, ret1, ret2);
		}

		else if (match("!"))
			result = new NotValueExpression(expression());

		result = parseObjectReference(result);

		return result;
	}

	/** Returns Object reference **/
	public Expression parseObjectReference(Expression expression) {
		// Check for Object
		String name = null;
		while (match("(") || match("[") || match(".", TokenType.KEYWORD)) {
			putBack(1);

			if (match("[")) {
				// Array reference
				putBack(1);

				expression = new ObjectElementReferenceExpression(expression, parseArrayExpression());
				continue;
			} else if (match(TokenType.KEYWORD)) {
				name = last(1).token;

				expression = new ObjectReferenceExpression(expression, name);
				continue;
			} else if (match("(")) {
				// Function Call
				putBack(1);

				expression = new FunctionReferenceExpression(expression, parseArgumentExpressions());
				continue;
			} else
				break;
		}

		return expression;
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
	@SuppressWarnings("unused")
	private ArrayList<Expression> parseArrayExpressions() {
		ArrayList<Expression> exp = new ArrayList<Expression>();

		while (match("[")) {
			exp.add(expression());
			consume("]");
		}

		return exp;
	}

	/**
	 * Does parsing array reference block '[1]'
	 */
	private Expression parseArrayExpression() {
		consume("[");
		Expression exp = expression();
		consume("]");
		return exp;
	}

	/**
	 * Checks for matching (ignoring string & char)
	 */
	public boolean match(TokenType type) {
		if (get(0) == null)
			return false;
		TokenType typ = get(0).type;
		if (typ != type)
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
		TokenType typ = get(1).type;
		if (typ == TokenType.STRING || typ == TokenType.CHAR)
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
		TokenType typ = get(0).type;
		if (typ == TokenType.STRING || typ == TokenType.CHAR)
			return false;
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
		TokenType typ = get(0).type;
		if (typ == TokenType.STRING || typ == TokenType.CHAR)
			return false;
		typ = get(1).type;
		if (typ == TokenType.STRING || typ == TokenType.CHAR)
			return false;
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
	 * Checks for match by String - WORD name value (ignoring string & char)
	 */
	public boolean match(String token) {
		if (get(0) == null)
			return false;
		// if (get(0).type != TokenType.KEYWORD)
		// return false;
		TokenType typ = get(0).type;
		if (typ == TokenType.STRING || typ == TokenType.CHAR)
			return false;
		if (!get(0).token.equals(token))
			return false;
		position++;
		return true;
	}

	/**
	 * Checks for match by String - value & it's type
	 */
	public boolean matchFull(String token, TokenType type) {
		if (get(0) == null)
			return false;
		// if (get(0).type != TokenType.KEYWORD)
		// return false;
		if (!get(0).token.equals(token) || get(0).type != type)
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
			throw new ParserException(tokens, position, type);
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
			throw new ParserException(tokens, position, token);
		return last(1);
	}

	/**
	 * Returns Token, offset from the End
	 */
	public Token last(int offset) {
		return tokens.get(position - offset);
	}
}
