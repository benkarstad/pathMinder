package pathMinder;

/**
 *
 */
public class TooManyItemsException extends RuntimeException {
	public TooManyItemsException(String message) { super(message); }
	public TooManyItemsException(String message, Throwable err) { super(message, err); }
}
