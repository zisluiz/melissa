package graphic;

import java.util.HashMap;
import java.util.Map;

import graphic.model.BeeGraphic;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.Bee;

public class BeeResolver {
	private Map<String, BeeGraphic> beeCircles = new HashMap<String, BeeGraphic>();
	private static Font font = new Font(14);
	private static Color colorWhite = Color.web("white", 1);

	public Circle createBee(Bee bee, int positionX, int positionY) {
		Circle circle = new Circle(4, Color.web("rgb(255,128,35)", 1));
		circle.setLayoutX(positionX);
		circle.setLayoutY(positionY);
		
		Text labelTime = new Text(positionX, positionY, bee.getId());
		labelTime.setFill(colorWhite);
		labelTime.setFont(font);
		
		beeCircles.put(bee.getId(), new BeeGraphic(circle, labelTime, bee));
		return circle;
	}

	public BeeGraphic getBee(String beeId) {
		return beeCircles.get(beeId);
	}

	public void removeBee(String beeId) {
		beeCircles.remove(beeId);
	}
}
