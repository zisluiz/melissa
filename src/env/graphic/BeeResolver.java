package graphic;

import java.util.HashMap;
import java.util.Map;

import graphic.model.BeeGraphic;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.Bee;

public class BeeResolver {
	private Map<String, BeeGraphic> beeCircles = new HashMap<String, BeeGraphic>();

	public Circle createBee(Bee bee, int positionX, int positionY) {
		Circle circle = new Circle(4, Color.web("rgb(255,128,35)", 1));
		circle.setLayoutX(positionX);
		circle.setLayoutY(positionY);
		
		beeCircles.put(bee.getId(), new BeeGraphic(circle, bee));
		return circle;
	}

	public BeeGraphic getBee(String beeId) {
		return beeCircles.get(beeId);
	}

	public void removeBee(String beeId) {
		beeCircles.remove(beeId);
	}
}
