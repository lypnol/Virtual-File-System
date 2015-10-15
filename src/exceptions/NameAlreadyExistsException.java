package exceptions;

public class NameAlreadyExistsException extends RuntimeException  {
	private static final long serialVersionUID = -5570566660937228907L;
	
	public NameAlreadyExistsException() {super();}
	public NameAlreadyExistsException(String message) {super(message);}
	public NameAlreadyExistsException(Throwable cause) { super(cause); }
}
