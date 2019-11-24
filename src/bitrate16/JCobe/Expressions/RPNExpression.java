package bitrate16.JCobe.Expressions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Expressions.RPNExpression.Atom.AtomType;
import bitrate16.JCobe.Values.Value;

/**
 * Reverse Polish notation, bitch!
 * 
 * @author bitrate16
 *
 */
public class RPNExpression implements Expression {

	private List<Atom> atoms;

	// Store result RPN expression for not lost time
	public List<Atom> resultRPN;
	// Store result expression
	public Expression result;

	public RPNExpression(List<Atom> atoms) {
		this.atoms = atoms;
		// System.err.println(atoms);
	}

	@Override
	public Value evaluate(Block b) {
		// System.err.println("Evaluating RPN");
		if (result != null)
			return result.evaluate(b);

		if (resultRPN == null) {
			// Build Expression
			resultRPN = infixToRPN(atoms);
			// System.err.println(resultRPN);
		}

		result = evalExp(resultRPN);
		// Shitten parse
		return result.evaluate(b);
	}

	/** Operator element type **/
	public static class Atom {
		// Types
		public static enum AtomType {
			OPERATOR, EXPRESSION
		}

		// Current type
		public AtomType type;
		// Current operation
		public String operator;
		// Current Expression
		public Expression expression;

		public Atom(String operator) {
			this.type = AtomType.OPERATOR;
			this.operator = operator;
		}

		public Atom(Expression exp) {
			this.type = AtomType.EXPRESSION;
			this.expression = exp;
		}

		@Override
		public String toString() {
			if (type == AtomType.OPERATOR)
				return operator;
			return "exp";
		}
	}

	// - - - - - - - - - - - - - - - Part I. Infix to RPN - - - - - - - - - - -
	// - - - - //
	// Associativity constants for operators
	private static final int LEFT_ASSOC = 0;
	private static final int RIGHT_ASSOC = 1;

	// Supported operators
	private static final Map<String, int[]> OPERATORS = new HashMap<String, int[]>();
	static {
		// Map<"token", []{precendence, associativity}>
		OPERATORS.put("||", new int[] { 5, LEFT_ASSOC });
		OPERATORS.put("&&", new int[] { 6, LEFT_ASSOC });

		OPERATORS.put("|", new int[] { 7, LEFT_ASSOC });
		OPERATORS.put("^", new int[] { 8, LEFT_ASSOC });
		OPERATORS.put("&", new int[] { 9, LEFT_ASSOC });

		OPERATORS.put("==", new int[] { 10, LEFT_ASSOC });
		OPERATORS.put("!=", new int[] { 10, LEFT_ASSOC });
		OPERATORS.put("===", new int[] { 10, LEFT_ASSOC });

		OPERATORS.put(">=", new int[] { 11, LEFT_ASSOC });
		OPERATORS.put(">", new int[] { 11, LEFT_ASSOC });
		OPERATORS.put("<", new int[] { 11, LEFT_ASSOC });
		OPERATORS.put("<=", new int[] { 11, LEFT_ASSOC });

		OPERATORS.put(">>", new int[] { 12, LEFT_ASSOC });
		OPERATORS.put("<<", new int[] { 12, LEFT_ASSOC });
		OPERATORS.put(">>>", new int[] { 12, LEFT_ASSOC });

		OPERATORS.put("+", new int[] { 13, LEFT_ASSOC });
		OPERATORS.put("-", new int[] { 13, LEFT_ASSOC });
		OPERATORS.put("%", new int[] { 13, LEFT_ASSOC });

		OPERATORS.put("*", new int[] { 14, LEFT_ASSOC });
		OPERATORS.put("/", new int[] { 14, LEFT_ASSOC });
	}

	/**
	 * Test if a certain is an operator .
	 * 
	 * @param token
	 *            The token to be tested .
	 * @return True if token is an operator . Otherwise False .
	 */
	private static boolean isOperator(Atom atom) {
		return OPERATORS.containsKey(atom.operator);
	}

	/**
	 * Test the associativity of a certain operator token .
	 * 
	 * @param token
	 *            The token to be tested (needs to operator).
	 * @param type
	 *            LEFT_ASSOC or RIGHT_ASSOC
	 * @return True if the tokenType equals the input parameter type .
	 */
	private static boolean isAssociative(Atom atom, int type) {
		if (!isOperator(atom)) {
			throw new IllegalArgumentException("Invalid atom: " + atom);
		}
		if (OPERATORS.get(atom.operator)[1] == type) {
			return true;
		}
		return false;
	}

	/**
	 * Compare precendece of two operators.
	 * 
	 * @param token1
	 *            The first operator .
	 * @param token2
	 *            The second operator .
	 * @return A negative number if token1 has a smaller precedence than token2,
	 *         0 if the precendences of the two tokens are equal, a positive
	 *         number otherwise.
	 */
	private static final int cmpPrecedence(Atom atom1, Atom atom2) {
		if (!isOperator(atom1) || !isOperator(atom2)) {
			throw new IllegalArgumentException("Invalid atoms: " + atom1 + " " + atom2);
		}
		return OPERATORS.get(atom1.operator)[0] - OPERATORS.get(atom2.operator)[0];
	}

	public static List<Atom> infixToRPN(List<Atom> atoms) {
		List<Atom> out = new ArrayList<Atom>();
		Stack<Atom> stack = new Stack<Atom>();
		// For all the input tokens [S1] read the next token [S2]
		for (Atom atom : atoms) {
			if (isOperator(atom)) {
				// If token is an operator (x) [S3]
				while (!stack.empty() && isOperator(stack.peek())) {
					// [S4]
					if ((isAssociative(atom, LEFT_ASSOC) && cmpPrecedence(atom, stack.peek()) <= 0)
							|| (isAssociative(atom, RIGHT_ASSOC) && cmpPrecedence(atom, stack.peek()) < 0)) {
						out.add(stack.pop()); // [S5] [S6]
						continue;
					}
					break;
				}
				// Push the new operator on the stack [S7]
				stack.push(atom);
			} else if ("(".equals(atom.operator)) {
				stack.push(atom); // [S8]
			} else if (")".equals(atom.operator)) {
				// [S9]
				while (!stack.empty() && !"(".equals(stack.peek().operator)) {
					out.add(stack.pop()); // [S10]
				}
				stack.pop(); // [S11]
			} else {
				out.add(atom); // [S12]
			}
		}
		while (!stack.empty()) {
			out.add(stack.pop()); // [S13]
		}
		return out;
	}

	/** Does parsing RPN into expression **/
	private Expression evalExp(List<Atom> atoms) {
		LinkedList<Expression> stack = new LinkedList<Expression>();
		for (Atom a : atoms) {
			if (a.type == AtomType.OPERATOR) {
				// System.err.println("Expression: exp " + a.operator + " exp");
				Expression e1 = stack.pop();
				Expression e2 = stack.pop();
				// 'e2 <operator> e1' cuz stack if flipped
				stack.push(new OperatorExpression(e2, e1, a.operator));
			} else {
				stack.push(a.expression);
			}
		}
		// System.err.println("Result: " + stack.peek());
		return stack.pop();
	}
}
