package graphic.model;

import javafx.scene.shape.Circle;
import model.Bee;

public class BeeGraphic {
	private Circle circle;
	private Bee bee;
	private BeeContainer insideContainer;

	public BeeGraphic(Circle circle, Bee bee) {
		this.circle = circle;
		this.bee = bee;
	}

	public Bee getBee() {
		return bee;
	}

	public Circle getCircle() {
		return circle;
	}

	synchronized public BeeContainer getInsideContainer() {
		return insideContainer;
	}

	synchronized public void setInsideContainer(BeeContainer insideContainer) {
		this.insideContainer = insideContainer;
	}

	synchronized public boolean isInsideContainer() {
		return insideContainer != null;
	}

}
