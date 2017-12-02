package graphic.model;

import javafx.scene.shape.Rectangle;
import model.PollenField;
import model.Position;

public class PollenFieldGraphic extends BeeContainer {
	private PollenField pollenField;
	
	public PollenFieldGraphic(Rectangle rectangle, String id, int ammount, int maxAmmount) {
		super(rectangle, id);
		this.pollenField = new PollenField(id, ammount, maxAmmount);
	}

	public PollenField getPollenField() {
		return pollenField;
	}
}