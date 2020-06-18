package pathMinder.core;

/**
 * Signals that the requested operation could not be performed by a container.
 * This is the general class of exceptions produced internally by Containers.
 */
public class ContainerException extends RuntimeException {
	public ContainerException(String message) { super(message); }
	public ContainerException(Throwable cause) { super(cause); }
	public ContainerException(String message, Throwable err) { super(message, err); }
}
