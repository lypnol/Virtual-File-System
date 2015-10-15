package exceptions;

public class NoSuchFileException extends RuntimeException {
	private static final long serialVersionUID = -7230366238279506158L;

	public NoSuchFileException() {super();}
	public NoSuchFileException(String message) {super(message);}
}
