package model.exception;

public class NoLongerHiveException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public NoLongerHiveException(String message) {
		super(message);
	}
}
