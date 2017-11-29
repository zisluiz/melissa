package model.exception;

public class MovimentOutOfBoundsException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public MovimentOutOfBoundsException(String message) {
		super(message);
	}
}