package model.enumeration;

public enum PollenSupply {
	LOW, MEDIUM, HIGH;

	public String getColor() {
		switch (this) {
		case LOW:
			return "white";
		case MEDIUM:
			return "pink";
		default:
			return "red";
		}
	}
}
