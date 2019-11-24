package bitrate16.JCobe.Syntax;

import java.util.ArrayList;

import bitrate16.JCobe.Exceptions.TokenizerException;
import bitrate16.JCobe.Syntax.Token.TokenType;

/**
 * Does parsing source code to token sequence
 * 
 * @author bitrate16
 *
 */
public class Tokenizer {

	// Main parser class
	public static ArrayList<Token> tokenize(String code) {
		// I do not trust that printer!
		code = code.trim();

		ArrayList<Token> tokens = new ArrayList<Token>();

		int index = 0;
		String token = "";

		while (index < code.length()) {
			// Skip all WhiteChars
			while (isWhite(code.charAt(index))) {
				index++;
			}

			// First Check - Delimiters
			if (isDelimiter(code.charAt(index))) {
				switch (code.charAt(index)) {
					case '\'': {
						// Found Character
						index++;
						while (index < code.length()) {
							// If not Escaped && Closed
							// TODO Here may be bug: '\\'
							if (code.charAt(index) == '\'' && code.charAt(index == 0 ? 0 : index - 1) != '\\')
								break;
							token += code.charAt(index);
							index++;
						}
						index++;

						// Do formatting
						// 1. Replace '\\' by '\'
						token = token.replace("\\\\", "\\");
						// 2. Replace '\'' by '''
						token = token.replace("\\'", "\'");
						// 3. Replace '\"' by '"'
						token = token.replace("\\\"", "\"");
						// 4. Replace '\n' by 'n'
						token = token.replace("\\n", "\n");
						// 5. Replace '\r' by 'r'
						token = token.replace("\\r", "\r");
						// 6. Replace '\t' by 't'
						token = token.replace("\\t", "\t");
						// 6. Replace '\b' by 'b'
						token = token.replace("\\b", "\b");
						// 6. Replace '\f' by 'f'
						token = token.replace("\\f", "\f");

						if (token.length() != 1)
							if (token.startsWith("\\u")) // Unicode character
								if (token.length() == 6) {
									token = (char) Integer.parseInt(token.substring(2), 16) + "";
								} else
									throw new TokenizerException("Invalid character '" + token + "\'");
							else
								throw new TokenizerException("Invalid character '" + token + "\'");

						tokens.add(new Token(token, TokenType.CHAR));
						token = "";
						continue;
					}
					case '\"': {
						// Found String
						index++;
						while (index < code.length()) {
							// If not Escaped && Closed
							// TODO Here may be bug: '\\'
							// XXX: Add parser for '\u0000' Unicade characters
							if (code.charAt(index) == '\"' && code.charAt(index == 0 ? 0 : index - 1) != '\\')
								break;
							token += code.charAt(index);
							index++;
						}
						index++;

						// Do formatting
						// 1. Replace '\\' by '\'
						token = token.replace("\\\\", "\\");
						// 2. Replace '\'' by '''
						token = token.replace("\\'", "\'");
						// 3. Replace '\"' by '"'
						token = token.replace("\\\"", "\"");
						// 4. Replace '\n' by 'n'
						token = token.replace("\\n", "\n");
						// 5. Replace '\r' by 'r'
						token = token.replace("\\r", "\r");
						// 6. Replace '\t' by 't'
						token = token.replace("\\t", "\t");
						// 6. Replace '\b' by 'b'
						token = token.replace("\\b", "\b");
						// 6. Replace '\f' by 'f'
						token = token.replace("\\f", "\f");

						tokens.add(new Token(token, TokenType.STRING));
						token = "";
						continue;
					}
					case '.': {
						// Checking for double number
						token += code.charAt(index++);
						// 2 Ways: I. '19.', II. '19.19'
						// I.
						if (index < code.length()) {
							if (isDigit(code.charAt(index))) {
								while (index < code.length()) {
									if (!isAlphaOrDigit(code.charAt(index)))
										break;
									token += code.charAt(index++);
								}
								tokens.add(new Token(token, TokenType.NUMBER));
								token = "";
								continue;
							}
						}
						tokens.add(new Token(token, TokenType.DELIMITER));
						token = "";
						continue;
					}
					default:
						tokens.add(new Token(code.charAt(index) + "", TokenType.DELIMITER));
						index++;
						continue;
				}
			} else if (isAlpha(code.charAt(index))) {
				token += code.charAt(index++);
				while (index < code.length()) {
					if (!isAlphaOrDigit(code.charAt(index)))
						break;
					token += code.charAt(index++);
				}
				if (token.equals("true") || token.equals("false"))
					tokens.add(new Token(token, TokenType.BOOLEAN));
				else
					tokens.add(new Token(token, TokenType.KEYWORD));
				token = "";
			} else if (isDigit(code.charAt(index))) {
				token += code.charAt(index++);
				while (index < code.length()) {
					if (!isAlphaOrDigit(code.charAt(index)))
						break;
					token += code.charAt(index++);
				}

				// Checking for double
				if (index < code.length()) {
					if (code.charAt(index) == '.') {
						token += code.charAt(index++);
						// 2 Ways: I. '19.', II. '19.19'
						// II.
						if (index < code.length()) {
							if (isDigit(code.charAt(index))) {
								while (index < code.length()) {
									if (!isAlphaOrDigit(code.charAt(index)))
										break;
									token += code.charAt(index++);
								}
							}
						}
						tokens.add(new Token(token, TokenType.NUMBER));
					} else
						tokens.add(new Token(token, TokenType.NUMBER));
				} else
					tokens.add(new Token(token, TokenType.NUMBER));
				token = "";
			} else
				index++;
		}

		return tokens;
	}

	// Util: is whitespace
	private static boolean isWhite(char c) {
		return c == ' ' || c == '\n' || c == '\r' || c == '\t';
	}

	// Util: is Alpha char
	private static boolean isAlpha(char c) {
		return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('а' <= c && c <= 'я') || ('а' <= c && c <= 'я')
				|| c == '_';
	}

	// Util: is numeric character
	private static boolean isDigit(char c) {
		return '0' <= c && c <= '9';
	}

	// Util: is numeric || is alpha
	private static boolean isAlphaOrDigit(char c) {
		return isAlpha(c) || isDigit(c);
	}

	private static final String DELIMITERS = "|&!;:?%.,/*+-=(){}[]<>^'\"~";

	// Util: is delimiter character
	private static boolean isDelimiter(char c) {
		return DELIMITERS.indexOf(c) != -1;
	}

}
