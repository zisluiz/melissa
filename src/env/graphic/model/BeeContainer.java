package graphic.model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.shape.Rectangle;

public abstract class BeeContainer {
	private Rectangle rectangle;
	private String id;
	private List<BeeGraphic> bees = new ArrayList<>();
	
	public BeeContainer(Rectangle rectangle, String id) {
		super();
		this.rectangle = rectangle;
		this.id = id;
	}
	
	public void addBee(BeeGraphic bee) {
		bees.add(bee);
	}
	
	public void removeBee(BeeGraphic bee) {
		bees.remove(bee);
	}	
	
	public String getId() {
		return id;
	}
	
	public Rectangle getRectangle() {
		return rectangle;
	}
}
