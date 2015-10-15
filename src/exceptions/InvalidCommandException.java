package exceptions;

public class InvalidCommandException extends RuntimeException{

	private static final long serialVersionUID = -1214465426254876504L;

	public InvalidCommandException() {super();}
	public InvalidCommandException(String message) {super(message);}
}
