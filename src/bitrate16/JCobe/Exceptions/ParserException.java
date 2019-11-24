package bitrate16.JCobe.Exceptions;

import java.util.List;

import bitrate16.JCobe.Syntax.Token;
import bitrate16.JCobe.Syntax.Token.TokenType;

/**
 * Thrown when parser could not parse
 * 
 * @author bitrate16
 *
 */
public class ParserException extends RuntimeException {
	private static final long serialVersionUID = -1566144607930250938L;

	private String message = "";

	/** Constructs error statement **/
	public ParserException(List<Token> tokens, int position, String expected) {
		String error = "Error near: '";
		// Bounds
		int WIDTH = 11;

		int start = Math.max(0, position - WIDTH / 2);
		int end = Math.min(tokens.size(), position + WIDTH / 2);

		for (int i = start; i < end; i++) {
			if (i == position)
				error += "__" + tokens.get(i).token + "__ ";
			else
				error += tokens.get(i).token + " ";
		}
		if(position >= end)
			error += "__";
		this.message = error.trim() + "' '" + expected + "'" + " expected";
	}

	/** Constructs error statement **/
	public ParserException(List<Token> tokens, int position, TokenType expected) {
		String error = "Error near: '";
		// Bounds
		int WIDTH = 11;

		int start = Math.max(0, position - WIDTH / 2);
		int end = Math.min(tokens.size(), position + WIDTH / 2);

		for (int i = start; i < end; i++) {
			if (i == position)
				error += "__" + tokens.get(i).token + "__ ";
			else
				error += tokens.get(i).token + " ";
		}
		if(position >= end)
			error += "__";
		this.message = error.trim() + "' '" + expected + "'" + " expected";
	}

	public ParserException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}