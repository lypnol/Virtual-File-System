package exceptions;

public class NoFreeSpaceLeftException extends RuntimeException {
	private static final long serialVersionUID = 4769968171209910531L;
	
	public NoFreeSpaceLeftException(){ super(); }
	public NoFreeSpaceLeftException(String message){ super(message); }
}
