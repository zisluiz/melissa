package graphic.model;

import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import model.Bee;

public class BeeGraphic {
	private Circle circle;
	private Text label;
	private Bee bee;
	private BeeContainer insideContainer;

	public BeeGraphic(Circle circle, Text label, Bee bee) {
		this.circle = circle;
		this.label = label;
		this.bee = bee;
	}

	public Bee getBee() {
		return bee;
	}
	
	public Text getLabel() {
		return label;
	}

	public Circle getCircle() {
		return circle;
	}

	public BeeContainer getInsideContainer() {
		return insideContainer;
	}

	public void setInsideContainer(BeeContainer insideContainer) {
		this.insideContainer = insideContainer;
	}

	public boolean isInsideContainer() {
		return insideContainer != null;
	}

}
