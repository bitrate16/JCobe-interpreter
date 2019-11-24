package bitrate16.JCobe.Syntax;

public class Token {
	// Type
	public static enum TokenType {
		// PreProcessing
		NUMBER, INTEGER, LONG, BYTE, DOUBLE, KEYWORD, STRING, CHAR, BOOLEAN, DELIMITER, EOF,
		// PostProcessing
		OPERATOR, // operating tokens
		// Setter
		SET, // , // '='
		// Syntax
		// SEMICOLON, // ';'
		// COMMA, // ','
		// // Bracket type
		// ROUND_BRACKET_OPEN, // '('
		// ROUND_BRACKET_CLOSE, // ')'
		// SQUARE_BRACKET_OPEN, // '['
		// SQUARE_BRACKET_CLOSE, // ']'
		// FIGURAL_BRACKET_OPEN, // '{'
		// FIGURAL_BRACKET_CLOSE// , // '}'
		// // TRIANGLE_BRACKER_OPEN, // '<'
		// // TRIANGLE_BRACKER_CLOSE // '<'
		NOT // '!'
	}

	// Tokens String
	public String token;
	// Token Type
	public TokenType type;

	public Token(String token, TokenType type) {
		this.token = token;
		this.type = type;
	}

	// TO qualified string
	public String toString() {
		return type + " " + token;
	}
}
