package graphic;

import graphic.model.BeeContainer;
import graphic.model.BeeGraphic;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.shape.Circle;
import model.Bee;
import model.Hive;
import model.PollenField;
import model.RandomUtils;
import model.enumeration.Direction;
import model.enumeration.HoneySupply;
import model.enumeration.PollenSupply;
import model.exception.InvalidMovimentException;
import model.exception.MovimentOutOfBoundsException;
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
		mapResolver.addBee(bee.getId(), x, y);
		Circle beeGraphic = beeResolver.createBee(bee, x, y);
		//root.getChildren().add(beeGraphic);
	}

	synchronized public void moveBee(String beeId, Direction direction) throws MovimentOutOfBoundsException, InvalidMovimentException {
		validateMoviment(beeId, direction);
		//System.out.println("Moving bee "+beeId+" to "+direction.toString());
		BeeGraphic beeGraphic = beeResolver.getBee(beeId);
		Bee bee = beeGraphic.getBee();
		
		boolean beforeInsideContainer = mapResolver.hasContainer(bee.getX(), bee.getY());
		
		mapResolver.moveBee(beeId, direction);
		
		boolean afterInsideContainer = mapResolver.hasContainer(bee.getX(), bee.getY());
		
		if (!beforeInsideContainer && afterInsideContainer) {
			BeeContainer beeContainer = mapResolver.getContainer(bee.getX(), bee.getY());
			beeGraphic.setInsideContainer(beeContainer);
			beeContainer.addBee(beeGraphic);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {			
					EnvironmentApplication.instance.removeNode(beeGraphic.getCircle());
				}
			});
		} else if (beforeInsideContainer && !afterInsideContainer) {
			BeeContainer beeContainer = beeGraphic.getInsideContainer();
			beeGraphic.setInsideContainer(null);
			beeContainer.removeBee(beeGraphic);
			
			Platform.runLater(new Runnable() {
				@Override
				public void run() {			
					EnvironmentApplication.instance.addNode(beeGraphic.getCircle());
				}
			});
		}
	}

	private void validateMoviment(String beeId, Direction direction) throws MovimentOutOfBoundsException, InvalidMovimentException {
		BeeGraphic beeGraphic = beeResolver.getBee(beeId);
		int x = beeGraphic.getBee().getX(), y = beeGraphic.getBee().getY();
		
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
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				EnvironmentApplication map = EnvironmentApplication.waitForStartUpTest();
				map.updateLarvaCount();
				
			}
		});		
	}

	public void setHoneyStart(int ammount) {
		Hive.getInstance().setHoney(ammount);
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				EnvironmentApplication map = EnvironmentApplication.waitForStartUpTest();
				map.updateHoneyStatus();
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
		
		EnvironmentApplication map = EnvironmentApplication.waitForStartUpTest();
		addBee(bee);
		map.updateBeeCount();		
		
//		Platform.runLater(new Runnable() {
//			@Override
//			public void run() {
//				BeeEnvironment map = BeeEnvironment.waitForStartUpTest();
//				map.addBee(bee);
//				map.updateBeeCount();
//			}
//		});		
	}

	public void changeDay(int newDay) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				EnvironmentApplication map = EnvironmentApplication.waitForStartUpTest();
				map.updateDay(newDay);
			}
		});				
	}

	public void launchGraphicApplication(int width, int height) {
		this.width = width;
		this.height = height;
		this.mapResolver = new MapResolver(beeResolver, pollenFieldResolver, width, height);
		
		new Thread(() -> {
			Application.launch(EnvironmentApplication.class, width+"", height+"");
		}).start();		
	}

	public void setPosition(String beeId, int x, int y) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				System.out.println("Setting position bee: "+beeId+" x: "+x+" y: "+y);
				EnvironmentApplication map = EnvironmentApplication.waitForStartUpTest();
				map.addNode(mapResolver.setPositionBee(beeId, x, y));
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

	synchronized public void collect(String pollenFieldId, String beeId) throws PollenIsOverException {
		PollenField pollenField = pollenFieldResolver.getPollenField(pollenFieldId).getPollenField();
		PollenSupply statusBefore = pollenField.getStatus();
		
		int ammount = pollenField.collect();
		beeResolver.getBee(beeId).getBee().setPollenCollected(ammount);
		
		PollenSupply statusAfter = pollenField.getStatus();
		
		if (!statusBefore.equals(statusAfter)) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					EnvironmentApplication map = EnvironmentApplication.waitForStartUpTest();
					map.updatePollenFieldStatus(pollenFieldId);
				}
			});	
		}
	}

	public void delivery(String beeId) {
		HoneySupply statusBefore = Hive.getInstance().getStatus();
		
		int ammount = beeResolver.getBee(beeId).getBee().takePollenCollected();
		Hive.getInstance().addHoney(ammount);
		
		HoneySupply statusAfter = Hive.getInstance().getStatus();
		
		if (!statusBefore.equals(statusAfter)) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					EnvironmentApplication map = EnvironmentApplication.waitForStartUpTest();
					map.updateHoneyStatus();;
				}
			});	
		}
	}
}