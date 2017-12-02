package model.enumeration;

import javafx.scene.paint.Color;

public enum PollenSupply {
	LOW, MEDIUM, HIGH;
	
	private static Color colorBlack = Color.web("black", 1);
	private static Color colorPink = Color.web("pink", 1);
	private static Color colorRed = Color.web("red", 1);

	public Color getColor() {
		switch (this) {
		case LOW:
			return colorBlack;
		case MEDIUM:
			return colorPink;
		default:
			return colorRed;
		}
	}
}
