package graphic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graphic.model.BeeContainer;
import graphic.model.BeeGraphic;
import graphic.model.HiveGraphic;
import graphic.model.PollenFieldGraphic;
import javafx.scene.shape.Circle;
import model.Bee;
import model.Position;
import model.enumeration.Direction;

public class MapResolver {
	private BeeResolver beeResolver;
	
	private HiveGraphic hive;
	private Map<String, PollenFieldGraphic> pollenFields = new HashMap<>();
	
	private String[][] existsContainer;
	
	public MapResolver(BeeResolver beeResolver, int width, int height) {
		this.beeResolver = beeResolver;
		this.existsContainer = new String[width][height];
	}

	public void addContainers(HiveGraphic hive, PollenFieldGraphic... pollenFieldGraphics) {
		this.hive = hive;
		
		for (PollenFieldGraphic pollenField : pollenFieldGraphics)
			pollenFields.put(pollenField.getId(), pollenField);
	}
	
	public void moveBee(Bee bee, Direction direction) {
		int x = bee.getPosition().getX();
		int y = bee.getPosition().getY();
		
		int newX = x;
		int newY = y;
		
		if (direction.equals(Direction.LEFT)) {
			newX = x-1;
		} else if (direction.equals(Direction.RIGHT)) {
			newX = x+1;
		} else if (direction.equals(Direction.UP)) {
			newY = y-1;
		} else if (direction.equals(Direction.DOWN)) {
			newY = y+1;
		}
		
		bee.setPosition(newX, newY);
	}
	
	public Circle setPositionBee(String beeId, int newX, int newY) {
		BeeGraphic beeGraphic = beeResolver.getBee(beeId);
		
		if (beeGraphic.isInsideContainer()) {
			beeGraphic.getInsideContainer().removeBee(beeGraphic);
			beeGraphic.setInsideContainer(null);
		}
		
		Circle circle = beeGraphic.getCircle();
		circle.setLayoutX(newX);
		circle.setLayoutY(newY);
		
		beeGraphic.getBee().setPosition(newX, newY);
		
		if (hasContainer(beeGraphic.getBee().getPosition()))  {
			BeeContainer beeContainer = getContainer(beeGraphic.getBee().getPosition());
			beeContainer.addBee(beeGraphic);
			beeGraphic.setInsideContainer(beeContainer);
		}			 

		return circle;
	}

	public boolean hasContainer(Position position) {
		String existsAnything = existsContainer[position.getX()][position.getY()];
		
		if (existsAnything != null) return !existsAnything.equals("0");		
		
		boolean isContainer = hasOnPosition(position, hive);
		
		if (isContainer)
			existsContainer[position.getX()][position.getY()] = "hive";
		
		if (!isContainer) {
			for (BeeContainer beeContainer : pollenFields.values()) {
				if (hasOnPosition(position, beeContainer)) {
					existsContainer[position.getX()][position.getY()] = beeContainer.getId();
					isContainer = true;
					break;
				}
			}
		}
		
		return isContainer;
	}	
	
	private boolean hasOnPosition(Position position, BeeContainer beeContainer) {
		int xContainer = (int) beeContainer.getRectangle().getLayoutX();
		int yContainer = (int) beeContainer.getRectangle().getLayoutY();
		int widthContainer = (int) beeContainer.getRectangle().getWidth();
		int heightContainer = (int) beeContainer.getRectangle().getHeight();
		
		if (position.getX() >= xContainer && position.getX() < (xContainer+widthContainer)) {
			if (position.getY() >= yContainer && position.getY() < (yContainer+heightContainer)) {
				return true; 
			}
		}
		
		return false;
	}

	public HiveGraphic getHive() {
		return hive;
	}

	public BeeContainer getContainer(Position position) {
		if (hasContainer(position)) {
			String container = existsContainer[position.getX()][position.getY()];
			
			if (container.equals("hive"))
				return hive;
			else
				return pollenFields.get(container);
		}
		return null;
	}
}