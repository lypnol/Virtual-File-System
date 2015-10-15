package exceptions;

public class VFSOperationFailException extends RuntimeException{
	private static final long serialVersionUID = -1323532597762569770L;
	
	public VFSOperationFailException() {super();}
	public VFSOperationFailException(String message) {super(message);}
}
