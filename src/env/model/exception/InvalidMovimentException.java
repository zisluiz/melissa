package model.exception;

public class InvalidMovimentException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidMovimentException(String message) {
		super(message);
	}
}