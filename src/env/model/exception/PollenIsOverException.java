package model.exception;

public class PollenIsOverException extends Exception {
	private static final long serialVersionUID = 1L;

	public PollenIsOverException(String message) {
		super(message);
	}
}