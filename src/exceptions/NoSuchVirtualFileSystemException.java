package exceptions;

public class NoSuchVirtualFileSystemException extends RuntimeException{

	private static final long serialVersionUID = 5423957078793975204L;
	
	public NoSuchVirtualFileSystemException() {super();}
	public NoSuchVirtualFileSystemException(String message) {super(message);}
}
