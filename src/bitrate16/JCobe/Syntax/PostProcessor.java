package bitrate16.JCobe.Syntax;

import java.util.ArrayList;

import bitrate16.JCobe.Syntax.Token.TokenType;
import bitrate16.JCobe.Utils.StringUtils;

/**
 * Does PostProcessing the code
 * 
 * @author bitrate16
 *
 */
public class PostProcessor {
	public static ArrayList<Token> postprocess(ArrayList<Token> tokens) {
		ArrayList<Token> toks = new ArrayList<Token>();
		// {
		// @Override
		// public boolean add(Token object) {
		// StackTraceElement[] ste = new Throwable().getStackTrace();
		// for (int i = 2; i < 4; i++) {
		// System.err.println(ste[i].getMethodName() + " " +
		// ste[i].getLineNumber());
		// }
		// System.err.println("Token: " + object);
		// return super.add(object);
		// }
		// };

		int index = 0;
		while (index < tokens.size()) {
			switch (tokens.get(index).type) {
				// Delimiters
				case DELIMITER: {
					switch (tokens.get(index).token) {
						// Operators
						case "+":
							// Increment
							if (index + 1 < tokens.size() && tokens.get(index + 1).token.equals("+")) {
								toks.add(new Token("++", TokenType.DELIMITER));
								index += 2;
								continue;
							}
						case "-":
							// Decrement
							if (index + 1 < tokens.size() && tokens.get(index + 1).token.equals("-")) {
								toks.add(new Token("--", TokenType.DELIMITER));
								index += 2;
								continue;
							}

							// Positive\Negative number
							// Check for previous token
							if (index == 0 || tokens.get(index - 1).token.matches("[)+\\-/*%&|^\\]};:?=]"))
								if (index + 1 < tokens.size() && tokens.get(index + 1).type == TokenType.NUMBER) {
									String number = tokens.get(index + 1).token;
									String operator = tokens.get(index).token;
									// Match type
									if (number.contains("."))
										toks.add(new Token(operator + number, TokenType.DOUBLE));
									else {
										if (StringUtils.isLong(number))
											toks.add(new Token(operator + number, TokenType.LONG));
										else if (StringUtils.isByte(number))
											toks.add(new Token(operator + number, TokenType.BYTE));
										else
											toks.add(new Token(operator + number, TokenType.INTEGER));
									}
									index += 2;
									continue;
								}
							if (index + 1 < tokens.size() && tokens.get(index + 1).token.equals("=")) {
								toks.add(new Token(tokens.get(index).token + "=", TokenType.DELIMITER));

								index += 2;
								continue;
							}
						case "*":
							if (index + 1 < tokens.size() && tokens.get(index + 1).token.equals("=")) {
								toks.add(new Token(tokens.get(index).token + "=", TokenType.DELIMITER));

								index += 2;
								continue;
							}
						case "/":
							if (index + 1 < tokens.size() && tokens.get(index + 1).token.equals("=")) {
								toks.add(new Token(tokens.get(index).token + "=", TokenType.DELIMITER));

								index += 2;
								continue;
							}
						case "%":
							if (index + 1 < tokens.size() && tokens.get(index + 1).token.equals("=")) {
								toks.add(new Token(tokens.get(index).token + "=", TokenType.DELIMITER));

								index += 2;
								continue;
							}
						case "^":
							toks.add(new Token(tokens.get(index++).token, TokenType.OPERATOR));
							continue;

						// Comparation
						case ">":
							// '>='
							if (index + 1 < tokens.size() && tokens.get(index + 1).token == "=") {
								toks.add(new Token(">=", TokenType.OPERATOR));
								index += 2;
								continue;
							}

							// '>>'
							if (index + 1 < tokens.size() && tokens.get(index + 1).token == ">") {
								// '>>>'
								if (index + 2 < tokens.size() && tokens.get(index + 2).token == ">") {
									toks.add(new Token(">>>", TokenType.OPERATOR));
									index += 3;
									continue;
								}
								// Else
								toks.add(new Token(">>", TokenType.OPERATOR));
								index += 2;
								continue;
							}

							// '>'
							toks.add(new Token(">", TokenType.OPERATOR));
							index++;
							continue;

						case "<":
							// '<='
							if (index + 1 < tokens.size() && tokens.get(index + 1).token == "=") {
								toks.add(new Token("<=", TokenType.OPERATOR));
								index += 2;
								continue;
							}

							// '<<'
							if (index + 1 < tokens.size() && tokens.get(index + 1).token == "<") {
								toks.add(new Token("<<", TokenType.OPERATOR));
								index += 2;
								continue;
							}

							// '<'
							toks.add(new Token("<", TokenType.OPERATOR));
							index++;
							continue;

						case "!":
							// '!='
							if (index + 1 < tokens.size() && tokens.get(index + 1).token.equals("=")) {
								toks.add(new Token("!=", TokenType.OPERATOR));
								index += 2;
								continue;
							}

							// '!'
							toks.add(new Token("!", TokenType.NOT));
							index++;
							continue;

						case "=":
							// '=='
							if (index + 1 < tokens.size() && tokens.get(index + 1).token.equals("=")) {
								// '==='
								if (index + 2 < tokens.size() && tokens.get(index + 2).token.equals("=")) {
									toks.add(new Token("===", TokenType.OPERATOR));
									index += 3;
									continue;
								}
								// Else
								toks.add(new Token("==", TokenType.OPERATOR));
								index += 2;
								continue;
							}
							toks.add(new Token("=", TokenType.SET));
							index++;
							continue;

						case "|":
							// '||'
							if (index + 1 < tokens.size() && tokens.get(index + 1).token.equals("|")) {
								toks.add(new Token("||", TokenType.OPERATOR));
								index += 2;
								continue;
							}

							// '|!'
							if (index + 1 < tokens.size() && tokens.get(index + 1).token.equals("!")) {
								toks.add(new Token("|!", TokenType.OPERATOR));
								index += 2;
								continue;
							}
							toks.add(new Token("|", TokenType.OPERATOR));
							index++;
							continue;

						case "&":
							// '&&'
							if (index + 1 < tokens.size() && tokens.get(index + 1).token.equals("&")) {
								toks.add(new Token("&&", TokenType.OPERATOR));
								index += 2;
								continue;
							}

							// '&!'
							if (index + 1 < tokens.size() && tokens.get(index + 1).token.equals("!")) {
								toks.add(new Token("&!", TokenType.OPERATOR));
								index += 2;
								continue;
							}
							toks.add(new Token("&", TokenType.OPERATOR));
							index++;
							continue;

						// case "~":
						// toks.add(new Token("~", TokenType.OPERATOR));
						// index++;
						// continue;

						default:
							toks.add(tokens.get(index));
							index++;
							continue;
					}
				}
				case NUMBER:
					String number = tokens.get(index).token;
					// Match type
					if (number.contains("."))
						toks.add(new Token(number, TokenType.DOUBLE));
					else {
						if (StringUtils.isLong(number))
							toks.add(new Token(number, TokenType.LONG));
						else if (StringUtils.isByte(number))
							toks.add(new Token(number, TokenType.BYTE));
						else
							toks.add(new Token(number, TokenType.INTEGER));
					}
					index++;
					continue;

				default:
					toks.add(tokens.get(index));
					index++;
					continue;
			}
		}

		return toks;
	}
}
