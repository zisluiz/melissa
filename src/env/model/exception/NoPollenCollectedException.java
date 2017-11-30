package model.exception;

public class NoPollenCollectedException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public NoPollenCollectedException(String message) {
		super(message);
	}
}