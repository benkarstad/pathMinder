package pathMinder;

/**
 *
 */
public class TooManyItemsException extends RuntimeException {
	/**
	 * TODO: Add a unique serial id
	 */

	private static final long serialVersionUID = 1L;
	
	public TooManyItemsException(String message) { super(message); }
	public TooManyItemsException(String message, Throwable err) { super(message, err); }
}
