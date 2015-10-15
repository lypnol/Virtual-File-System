package exceptions;

public class SizeNotAllowedException extends RuntimeException {
	private static final long serialVersionUID = 2066290327255654732L;
	
	public SizeNotAllowedException(){ super(); }
	public SizeNotAllowedException(String message){ super(message); }
}
