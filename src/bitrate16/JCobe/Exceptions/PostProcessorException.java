package bitrate16.JCobe.Exceptions;

/**
 * Thrown when post-processing errors is caused
 * 
 * @author bitrate16
 *
 */
public class PostProcessorException extends RuntimeException {
	private static final long serialVersionUID = 2408671349719690427L;

	public PostProcessorException(String message) {
		super(message);
	}
}