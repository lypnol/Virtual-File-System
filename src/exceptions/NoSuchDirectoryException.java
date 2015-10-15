package exceptions;

public class NoSuchDirectoryException extends RuntimeException{
	private static final long serialVersionUID = 787720929763850619L;
	
	public NoSuchDirectoryException() {super();}
	public NoSuchDirectoryException(String message) {super(message);}
}
