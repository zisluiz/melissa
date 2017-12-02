package graphic.model;

import javafx.scene.shape.Rectangle;
import model.PollenField;

public class PollenFieldGraphic extends BeeContainer {
	private PollenField pollenField;
	
	public PollenFieldGraphic(Rectangle rectangle, PollenField pollenField) {
		super(rectangle, pollenField.getId());
		this.pollenField = pollenField;
	}

	public PollenField getPollenField() {
		return pollenField;
	}
}