package graphic;

import graphic.model.BeeContainer;
import graphic.model.BeeGraphic;
import graphic.model.PollenFieldGraphic;
import javafx.application.Application;
import javafx.scene.shape.Circle;
import model.Bee;
import model.Hive;
import model.JavaFXConcurrent;
import model.PollenField;
import model.Position;
import model.RandomUtils;
import model.enumeration.Direction;
import model.enumeration.HoneySupply;
import model.enumeration.PollenSupply;
import model.exception.CannotCollectOnThisPositionException;
import model.exception.CannotDepositOnThisPositionException;
import model.exception.InvalidMovimentException;
import model.exception.MovimentOutOfBoundsException;
import model.exception.NoLongerHiveException;
import model.exception.NoLongerPollenFieldException;
import model.exception.NoPollenCollectedException;
import model.exception.PollenIsOverException;

public class Environment {
	private static Environment instance = null;
	private int width;
	private int height;
	private BeeResolver beeResolver;
	private PollenFieldResolver pollenFieldResolver;
	private MapResolver mapResolver;
	
	private Environment() {
		System.out.println("Creating BeeEnvironment");
		this.beeResolver = new BeeResolver();
		this.pollenFieldResolver = new PollenFieldResolver();		
	}

	public static Environment getInstance() {
		if (instance == null)
			instance = new Environment();
		
		return instance;
	}
	
	private void addBee(Bee bee) {
		int hiveX = (int) mapResolver.getHive().getRectangle().getLayoutX();
		int hiveY = (int) mapResolver.getHive().getRectangle().getLayoutY();
		int hiveMaxX = hiveX + (int) mapResolver.getHive().getRectangle().getWidth();
		int hiveMaxY = hiveY + (int) mapResolver.getHive().getRectangle().getHeight();
		int x = RandomUtils.getRandom(hiveX, hiveMaxX);
		int y = RandomUtils.getRandom(hiveY, hiveMaxY);
		
		//System.out.println("Random position: ("+hiveX+","+hiveMaxX+"),("+hiveY+","+hiveMaxY+") randomized: ("+x+","+y+")");
		
		bee.setPosition(x, y);
		beeResolver.createBee(bee, x, y);
		//root.getChildren().add(beeGraphic);
	}

	public void moveBee(String beeId, Direction direction) throws MovimentOutOfBoundsException, InvalidMovimentException {
		validateMoviment(beeId, direction);
//		System.out.println("Moving bee "+beeId+" to "+direction.toString());
		BeeGraphic beeGraphic = beeResolver.getBee(beeId);
		Bee bee = beeGraphic.getBee();
		
		boolean beforeInsideContainer = mapResolver.hasContainer(bee.getPosition());
		
		mapResolver.moveBee(bee, direction);

		boolean afterInsideContainer = mapResolver.hasContainer(bee.getPosition());
		boolean removeNode = false;
		boolean addNode = false;
		
		
		if (!beforeInsideContainer && afterInsideContainer) {
			BeeContainer beeContainer = mapResolver.getContainer(bee.getPosition());
			beeGraphic.setInsideContainer(beeContainer);
			beeContainer.addBee(beeGraphic);
			
//			System.out.println("Removing node "+beeId);
			removeNode = true;
		} else if (beforeInsideContainer && !afterInsideContainer) {
			BeeContainer beeContainer = beeGraphic.getInsideContainer();
			beeGraphic.setInsideContainer(null);
			beeContainer.removeBee(beeGraphic);
			
//			System.out.println("Adding node "+beeId);
			addNode = true;
		}
		
		final boolean removeNodeFinal = removeNode;
		final boolean addeNodeFinal = addNode;
		
		JavaFXConcurrent.getInstance().addUpdate(new Runnable() {
			
			@Override
			public void run() {
				beeGraphic.getCircle().setLayoutX(bee.getPosition().getX());
				beeGraphic.getCircle().setLayoutY(bee.getPosition().getY());
				
				if (removeNodeFinal)
					EnvironmentApplication.instance.removeBee(beeGraphic.getCircle());
				
				if (addeNodeFinal)
					EnvironmentApplication.instance.addBee(beeGraphic.getCircle());				
			}
		});		
	}

	private void validateMoviment(String beeId, Direction direction) throws MovimentOutOfBoundsException, InvalidMovimentException {
		BeeGraphic beeGraphic = beeResolver.getBee(beeId);
		int x = beeGraphic.getBee().getPosition().getX(), y = beeGraphic.getBee().getPosition().getY();
		
		if (direction.equals(Direction.LEFT))
			x--;
		else if (direction.equals(Direction.RIGHT))
			x++;
		else if (direction.equals(Direction.UP))
			y--;		
		else if (direction.equals(Direction.DOWN))
			y++;
		
		if (x < 0 || x >= width)
			throw new MovimentOutOfBoundsException("Moving to invalid x ("+x+") coordinate.");
		else if (y < 0 || y >= height)
			throw new MovimentOutOfBoundsException("Moving to invalid y ("+y+") coordinate.");	
		
/*		if (beeGraphic.isInsideContainer() && mapResolver.hasContainer(x,y)) {
			throw new InvalidMovimentException("Cannot move inside a container.");
		}*/
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void createLarva() {
		Hive.getInstance().createLarva();
		EnvironmentApplication.getInstance().updateLarvaCount();
	}

	public void setHoneyStart(int ammount) {
		Hive.getInstance().setHoney(ammount);
		JavaFXConcurrent.getInstance().addUpdate(new Runnable() {
			@Override
			public void run() {	
				EnvironmentApplication.getInstance().updateHoneyStatus(Hive.getInstance().getStatus());
			}
		});
	}

	public void registerBee(String beeId, String type, int age) {
		System.out.println("Registering bee "+beeId);
		Hive hive = Hive.getInstance();
		Bee bee = null;
		
		if (type.equals("feeder")) {
			bee = hive.createFeeder(beeId, age);
		} else if (type.equals("sentinel")) {
			bee = hive.createSentinel(beeId, age);
		} else if (type.equals("worker")) {
			bee = hive.createWorker(beeId, age);
		} else if (type.equals("queen")) {
			bee = hive.createQueen(beeId, age);
		}
		
		EnvironmentApplication map = EnvironmentApplication.getInstance();
		addBee(bee);
		map.updateBeeCount();		
	}

	public void changeDay(int newDay) {
		EnvironmentApplication.getInstance().updateDay(newDay);
	}

	public void launchGraphicApplication(int width, int height) {
		this.width = width;
		this.height = height;
		this.mapResolver = new MapResolver(beeResolver, width, height);
		
		new Thread(() -> {
			Application.launch(EnvironmentApplication.class, width+"", height+"");
		}).start();		
	}

	public void setPosition(String beeId, int x, int y) {
//		System.out.println("Setting position bee: "+beeId+" x: "+x+" y: "+y);
		Circle circle = mapResolver.setPositionBee(beeId, x, y);
		
		JavaFXConcurrent.getInstance().addUpdate(new Runnable() {
			@Override
			public void run() {	
				if (beeResolver.getBee(beeId).isInsideContainer())
					EnvironmentApplication.getInstance().removeBee(circle);
				else
					EnvironmentApplication.getInstance().addBee(circle);
			}
		});
	}

	public BeeResolver getBeeResolver() {
		return beeResolver;
	}
	
	public MapResolver getMapResolver() {
		return mapResolver;
	}
	
	public PollenFieldResolver getPollenFieldResolver() {
		return pollenFieldResolver;
	}

	public void collect(String pollenFieldId, String beeId) throws PollenIsOverException, NoLongerPollenFieldException, CannotCollectOnThisPositionException {
//		System.out.println("Beee "+beeId+" trying to collect on field "+pollenFieldId);
		BeeGraphic beeGraphic = beeResolver.getBee(beeId);
		PollenFieldGraphic pollenFieldGraphic = pollenFieldResolver.getPollenField(pollenFieldId);
		
		if (!beeGraphic.isInsideContainer())
			throw new CannotCollectOnThisPositionException("Bee isn't inside pollen field!");
		else if (!beeGraphic.getInsideContainer().equals(pollenFieldGraphic)) 
			throw new NoLongerPollenFieldException("Bee isn't on this pollen field!");
		
//		System.out.println("Beee "+beeId+" is collecting on field "+pollenFieldId);
		
		PollenField pollenField = pollenFieldGraphic.getPollenField();
		PollenSupply statusBefore = pollenField.getStatus();
		
		int ammount = pollenField.collect();
		beeGraphic.getBee().setPollenCollected(ammount);
		
		PollenSupply statusAfter = pollenField.getStatus();
		
		if (!statusBefore.equals(statusAfter)) {
			EnvironmentApplication.getInstance().updatePollenFieldStatus(pollenFieldId);
		}
	}

	public void delivery(String beeId) throws CannotDepositOnThisPositionException, NoLongerHiveException, NoPollenCollectedException {
//		System.out.println("Beee "+beeId+" delivering on hive");
		BeeGraphic beeGraphic = beeResolver.getBee(beeId);
		
		if (!beeGraphic.isInsideContainer())
			throw new CannotDepositOnThisPositionException("Bee isn't inside field!");
		else if (!beeGraphic.getInsideContainer().equals(mapResolver.getHive())) 
			throw new NoLongerHiveException("Bee isn't on Hive!");
		
		int ammount = beeResolver.getBee(beeId).getBee().takePollenCollected();
		
		if (ammount <= 0)
			throw new NoPollenCollectedException("No one pollen is collected!");
		
		HoneySupply statusBefore = Hive.getInstance().getStatus();
		
		
		Hive.getInstance().addHoney(ammount);
		
		HoneySupply statusAfter = Hive.getInstance().getStatus();
		
		if (!statusBefore.equals(statusAfter)) {
			JavaFXConcurrent.getInstance().addUpdate(new Runnable() {
				@Override
				public void run() {	
					EnvironmentApplication.getInstance().updateHoneyStatus(Hive.getInstance().getStatus());
				}
			});
		}
	}

	public Position getPosition(String beeId) {
		return beeResolver.getBee(beeId).getBee().getPosition();
	}
}