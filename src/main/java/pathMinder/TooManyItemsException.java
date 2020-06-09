package pathMinder;

/**
 * Thrown to indicate that a Container cannot accept the supplied items due to
 * constraints on the quantity that it can contain.
 */
public class TooManyItemsException extends ContainerException {
	/**
	 * TODO: Add a unique serial id
	 */

	private static final long serialVersionUID = 1L;

	public TooManyItemsException(String message) { super(message); }
	public TooManyItemsException(Throwable cause) { super(cause); }
	public TooManyItemsException(String message, Throwable err) { super(message, err); }
}
