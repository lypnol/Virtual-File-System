package exceptions;

public class NoSuchFileOrDirectoryException extends RuntimeException{

	private static final long serialVersionUID = -640991011347995335L;

	public NoSuchFileOrDirectoryException() {super();}
	public NoSuchFileOrDirectoryException(String message) {super(message);}
}
