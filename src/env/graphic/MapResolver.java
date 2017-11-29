package graphic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import graphic.model.BeeContainer;
import graphic.model.BeeGraphic;
import graphic.model.HiveGraphic;
import graphic.model.PollenFieldGraphic;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import model.enumeration.Direction;

public class MapResolver {
	private List<String> map[][];
	private BeeResolver beeResolver;
	private PollenFieldResolver pollenFieldResolver;
	
	private HiveGraphic hive;
	private List<PollenFieldGraphic> pollenFields;
	
	public MapResolver(BeeResolver beeResolver, PollenFieldResolver pollenFieldResolver, int width, int height) {
		this.map = new ArrayList[width][height];
		this.beeResolver = beeResolver;
		this.pollenFieldResolver = pollenFieldResolver;
	}

	public void addContainers(HiveGraphic hive, PollenFieldGraphic... pollenFields) {
		this.hive = hive;
		this.pollenFields = Arrays.asList(pollenFields);
		
		addContainer(hive);
		for (BeeContainer beeContainer : pollenFields)
			addContainer(beeContainer);
	}
	
	private void addContainer(BeeContainer beeContainer) {
		Rectangle rectangle = beeContainer.getRectangle();
		
		for (int x = (int) rectangle.getLayoutX(); x <= (int) (rectangle.getLayoutX() + rectangle.getWidth()); x++) {
			for (int y = (int) rectangle.getLayoutY(); y <= (int) (rectangle.getLayoutY() + rectangle.getHeight()); y++) {
				addMap(x, y, beeContainer.getId());
			}			
		}
		
/*		for (int x = 0; x < map.length; x++) {
			for (int y = 0; y < map[0].length; y++) {
				System.out.print((map[x][y] != null ? map[x][y].toString() : "0")+",");
			}		
			System.out.println("");
		}*/
	}	

	public void addBee(String beeId, int x, int y) {
		addMap(x, y, beeId);
	}

	public void moveBee(String beeId, Direction direction) {
		BeeGraphic bee = beeResolver.getBee(beeId);
		Circle circle = bee.getCircle();
		int x = (int) circle.getLayoutX();
		int y = (int) circle.getLayoutY();
		removeBee(x, y, beeId);
		
		if (direction.equals(Direction.LEFT)) {
			addBee(beeId, x-1, y);
			circle.setLayoutX(x-1);
		} else if (direction.equals(Direction.RIGHT)) {
			addBee(beeId, x+1, y);
			circle.setLayoutX(x+1);
		} else if (direction.equals(Direction.UP)) {
			addBee(beeId, x, y-1);
			circle.setLayoutY(y-1);	
		} else if (direction.equals(Direction.DOWN)) {
			addBee(beeId, x, y+1);	
			circle.setLayoutY(y+1);	
		}
		
		bee.getBee().setPosition((int) circle.getLayoutX(), (int) circle.getLayoutY());
	}
	
	private void removeBee(int x, int y, String beeId) {
		map[x][y].remove(beeId);
	}

	private void addMap(int x, int y, String id) {
		List<String> list = map[x][y];
		
		if (list == null) {
			list = new ArrayList<>();
			map[x][y] = list;
		}
		
		list.add(id);
	}

	public Circle setPositionBee(String beeId, int newX, int newY) {
		BeeGraphic beeGraphic = beeResolver.getBee(beeId);
		removeBee(beeGraphic.getBee().getX(), beeGraphic.getBee().getY(), beeId);
		
		Circle circle = beeGraphic.getCircle();
		circle.setLayoutX(newX);
		circle.setLayoutY(newY);
		
		addBee(beeId, newX, newY);
		beeGraphic.getBee().setPosition(newX, newY);
		
		return circle;
	}

	public boolean hasContainer(int x, int y) {
//		System.out.println("has container? x: "+x+" y: "+y);
//		System.out.println("Hive:");
		
//		for (int xx = (int) hive.getRectangle().getLayoutX(); xx <= ((int) hive.getRectangle().getLayoutX() + (int) hive.getRectangle().getWidth()); xx++) {
//			for (int yy = (int) hive.getRectangle().getLayoutY(); yy < ((int) hive.getRectangle().getLayoutY() + (int) hive.getRectangle().getHeight()); yy++) {
//				System.out.print((map[xx][yy] != null ? map[xx][yy].toString() : "0")+",");
//			}		
//			System.out.println("");
//		}		
		
		if (map[x][y] == null) return false;
		
		boolean isContainer = map[x][y].contains(hive.getId());
		
		if (!isContainer) {
//			System.out.println("Containers:");
			for (BeeContainer beeContainer : pollenFields) {
//				System.out.println(beeContainer.getId());
				
/*				for (int xx = (int) beeContainer.getRectangle().getLayoutX(); xx <= ((int) beeContainer.getRectangle().getLayoutX() + (int) beeContainer.getRectangle().getWidth()); xx++) {
					for (int yy = (int) beeContainer.getRectangle().getLayoutY(); yy < ((int) beeContainer.getRectangle().getLayoutY() + (int) beeContainer.getRectangle().getHeight()); yy++) {
						System.out.print((map[xx][yy] != null ? map[xx][yy].toString() : "0")+",");
					}		
					System.out.println("");
				}	*/			
				
				if (map[x][y].contains(beeContainer.getId())) {
					isContainer = true;
					break;
				}
			}
		}
		
		//System.out.println("result hasContainer: "+isContainer);
		return isContainer;
	}	
	
	public HiveGraphic getHive() {
		return hive;
	}

	public BeeContainer getContainer(int x, int y) {
		List<String> elements = map[x][y];
		for (String container : elements) {
			if (container.equals("hive"))
				return hive;
			else if (container.startsWith("pollenField"))
				return pollenFieldResolver.getPollenField(container);
		}
		return null;
	}
}