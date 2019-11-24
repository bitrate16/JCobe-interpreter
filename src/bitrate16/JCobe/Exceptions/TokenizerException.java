package bitrate16.JCobe.Exceptions;

/**
 * Thrown when Tokenizer could not parse
 * 
 * @author bitrate16
 *
 */
public class TokenizerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TokenizerException(String message) {
		super(message);
	}
}