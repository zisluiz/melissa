package model.enumeration;

import javafx.scene.paint.Color;

public enum PollenSupply {
	OVER, LOW, MEDIUM, HIGH;
	
	private static Color colorBlack = Color.web("black", 1);
	private static Color colorWhite = Color.web("white", 1);
	private static Color colorPink = Color.web("pink", 1);
	private static Color colorRed = Color.web("red", 1);

	public Color getColor() {
		switch (this) {
		case OVER:
			return colorBlack;		
		case LOW:
			return colorWhite;
		case MEDIUM:
			return colorPink;
		default:
			return colorRed;
		}
	}
}
