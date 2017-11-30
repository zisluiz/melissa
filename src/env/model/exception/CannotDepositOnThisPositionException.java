package model.exception;

public class CannotDepositOnThisPositionException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public CannotDepositOnThisPositionException(String message) {
		super(message);
	}
}
