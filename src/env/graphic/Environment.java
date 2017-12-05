package graphic;

import java.util.List;

import artifact.Parameters;
import graphic.model.BeeContainer;
import graphic.model.BeeGraphic;
import graphic.model.PollenFieldGraphic;
import javafx.application.Application;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import model.Bee;
import model.Hive;
import model.Larva;
import model.PollenField;
import model.Position;
import model.RandomUtils;
import model.enumeration.Direction;
import model.enumeration.HoneySupply;
import model.enumeration.PollenSupply;
import model.exception.CannotCollectOnThisPositionException;
import model.exception.CannotDepositOnThisPositionException;
import model.exception.InsufficientHoneyException;
import model.exception.InsufficientPollenException;
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
	private int extTemperature;
	private BeeResolver beeResolver;
	private PollenFieldResolver pollenFieldResolver;
	private MapResolver mapResolver;
	private List<PollenField> pollenFields;
	
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
		int hiveMaxX = hiveX + (int) mapResolver.getHive().getRectangle().getWidth() - 1;
		int hiveMaxY = hiveY + (int) mapResolver.getHive().getRectangle().getHeight() - 1;
		int x = RandomUtils.getRandom(hiveX, hiveMaxX);
		int y = RandomUtils.getRandom(hiveY, hiveMaxY);
		
		//System.out.println("Random position: ("+hiveX+","+hiveMaxX+"),("+hiveY+","+hiveMaxY+") randomized: ("+x+","+y+")");
		
		bee.setPosition(x, y);
		beeResolver.createBee(bee, x, y);
		BeeGraphic beeGraphic = beeResolver.getBee(bee.getId());
		beeGraphic.setInsideContainer(mapResolver.getHive());
		mapResolver.getHive().addBee(beeGraphic);
	}
	
	public Position getBeePos(String beeId) {
		BeeGraphic beeGraphic = beeResolver.getBee(beeId);
		if(beeGraphic == null)
			return null;
		Bee bee = beeGraphic.getBee();
		return bee.getPosition();
	}

	public void moveBee(String beeId, Direction direction) throws MovimentOutOfBoundsException, InvalidMovimentException {
		validateMoviment(beeId, direction);
//		System.out.println("Moving bee "+beeId+" to "+direction.toString());
		BeeGraphic beeGraphic = beeResolver.getBee(beeId);
		Bee bee = beeGraphic.getBee();
		
		boolean beforeInsideContainer = mapResolver.hasContainer(bee.getPosition());
		
		mapResolver.moveBee(bee, direction);

		postMoviment(beeGraphic, bee, beforeInsideContainer);		
	}
	
	public void moveBee(String beeId, int x, int y) throws MovimentOutOfBoundsException, InvalidMovimentException {
		validateMoviment(x ,y);
		BeeGraphic beeGraphic = beeResolver.getBee(beeId);
		Bee bee = beeGraphic.getBee();
		
		boolean beforeInsideContainer = mapResolver.hasContainer(bee.getPosition());
		
		bee.setPosition(x, y);

		postMoviment(beeGraphic, bee, beforeInsideContainer);
	}		

	private void postMoviment(BeeGraphic beeGraphic, Bee bee, boolean beforeInsideContainer) {
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
				
				beeGraphic.getLabel().setX(bee.getPosition().getX());
				beeGraphic.getLabel().setY(bee.getPosition().getY());				
				
				if (removeNodeFinal) {
					EnvironmentApplication.instance.removeBee(beeGraphic.getCircle());
					EnvironmentApplication.instance.removeBee(beeGraphic.getLabel());
				}
				
				if (addeNodeFinal) {
					EnvironmentApplication.instance.addBee(beeGraphic.getCircle());
					EnvironmentApplication.instance.addBee(beeGraphic.getLabel());
				}
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
		
		validateMoviment(x, y);
	}

	private void validateMoviment(int x, int y) throws MovimentOutOfBoundsException {
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
		JavaFXConcurrent.getInstance().addUpdate(new Runnable() {
			@Override
			public void run() {			
				EnvironmentApplication.getInstance().updateLarvaCount();
			}
		});
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
	
	public void setPollenStart(int ammount) {
		Hive.getInstance().setPollen(ammount);
	}
	
	public void registerBee(String beeId, String role) {
		System.out.println("Registering bee "+beeId+" to role "+role);
		Hive hive = Hive.getInstance();
		Bee bee = hive.createBee(beeId, role );
		EnvironmentApplication map = EnvironmentApplication.getInstance();
		addBee(bee);
		
		JavaFXConcurrent.getInstance().addUpdate(new Runnable() {
			@Override
			public void run() {			
				map.updateBeeCount();		
			}
		});			
	}
	
	public void changeRole(String beeId, String role) {
		System.out.println("Changing bee role, bee: "+beeId+", to role:"+role);
		Bee bee = beeResolver.getBee(beeId).getBee();
		Hive.getInstance().changeRole(bee, role);
		
		JavaFXConcurrent.getInstance().addUpdate(new Runnable() {
			@Override
			public void run() {			
				EnvironmentApplication.getInstance().updateBeeCount();		
			}
		});			
	}
	
	public void unRegisterBee(String beeId) {
		System.out.println("Unregistering bee "+beeId);
		BeeGraphic beeGraphic = beeResolver.getBee(beeId);
		Bee bee = beeGraphic.getBee();
		
		Hive.getInstance().removeBeeRole(bee.getRole(), bee);
		beeResolver.removeBee(beeId);
		
		if (beeGraphic.isInsideContainer()) {
			beeGraphic.getInsideContainer().removeBee(beeGraphic);
		}
			
		JavaFXConcurrent.getInstance().addUpdate(new Runnable() {
			@Override
			public void run() {			
				EnvironmentApplication map = EnvironmentApplication.getInstance();
				map.removeBee(beeGraphic.getCircle());
				map.removeBee(beeGraphic.getLabel());
				map.updateBeeCount();		
			}
		});			
	}	

	public void changeDay(int newDay) {
		JavaFXConcurrent.getInstance().addUpdate(new Runnable() {
			@Override
			public void run() {			
				EnvironmentApplication.getInstance().updateDay(newDay);
			}
		});		
	}

	public void setIntTemp(int newTemp) {
		Hive.getInstance().setTemperature(newTemp);
		JavaFXConcurrent.getInstance().addUpdate(new Runnable() {
			@Override
			public void run() {			
				EnvironmentApplication.getInstance().updateIntTemperature(newTemp);
			}
		});		
	}

	public void changeExtTemp(int newTemp) {
		this.extTemperature = newTemp;
		JavaFXConcurrent.getInstance().addUpdate(new Runnable() {
			@Override
			public void run() {			
				EnvironmentApplication.getInstance().updateExtTemp(newTemp);
			}
		});		
	}

	public void launchGraphicApplication(int width, int height, List<PollenField> pollenFields) {
		this.width = width;
		this.height = height;
		this.mapResolver = new MapResolver(beeResolver, width, height);
		this.pollenFields = pollenFields;
		
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
				BeeGraphic beeGraphic = beeResolver.getBee(beeId);
				if (beeGraphic.isInsideContainer()) {
					EnvironmentApplication.getInstance().removeBee(circle);
					EnvironmentApplication.getInstance().removeBee(beeGraphic.getLabel());
				}
				else {
					EnvironmentApplication.getInstance().addBee(circle);
					EnvironmentApplication.getInstance().addBee(beeGraphic.getLabel());
				}
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
		if(pollenFieldId == null)
			throw new CannotCollectOnThisPositionException("Bee isn't inside pollen field!");
		
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
		
		updatePollenField(pollenField, statusBefore);
	}

	private void updatePollenField(PollenField pollenField, PollenSupply statusBefore) {
		PollenSupply statusAfter = pollenField.getStatus();
		
		if (!statusBefore.equals(statusAfter)) {
			EnvironmentApplication.getInstance().updatePollenFieldStatus(pollenField.getId());
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
			throw new NoPollenCollectedException("Not one pollen is collected!");
		
		HoneySupply statusBefore = Hive.getInstance().getStatus();
		Hive.getInstance().addPollen(ammount);
		HoneySupply statusAfter = Hive.getInstance().getStatus();
		
		updateHoney(statusBefore, statusAfter);
	}

	public void updateHoney(HoneySupply statusBefore, HoneySupply statusAfter) {
		if (!statusBefore.equals(statusAfter)) {
			JavaFXConcurrent.getInstance().addUpdate(new Runnable() {
				@Override
				public void run() {	
					EnvironmentApplication.getInstance().updateHoneyStatus(Hive.getInstance().getStatus());
				}
			});
		}
	}
	
	public void processPollen(int ammount) throws InsufficientPollenException {
		HoneySupply statusBefore = Hive.getInstance().getStatus();
		
		Hive.getInstance().subPollen(ammount);
		Hive.getInstance().addHoney(ammount);
		
		HoneySupply statusAfter = Hive.getInstance().getStatus();
		
		updateHoney(statusBefore, statusAfter);
	}
	
	public Position getPosition(String beeId) {
		return beeResolver.getBee(beeId).getBee().getPosition();
	}

	public String getMatchingPollenFieldId(Position beePos) {
		String pollenFieldId = null;
		
		for (int i = 1; i <= pollenFieldResolver.getNumberPollenFields(); i++){
			String name = "pollenField"+i;
			Rectangle rec = pollenFieldResolver.getPollenField(name).getRectangle();
			
			if (beePos.getX() >= rec.getLayoutX() && beePos.getX() <= (rec.getLayoutX() + rec.getWidth()))
				if(beePos.getY() >= rec.getLayoutY() && beePos.getY() <= (rec.getLayoutY() + rec.getHeight()))
					pollenFieldId = name;
		}
		return pollenFieldId;
	}
	
	public int getExtTemperature() {
		return extTemperature;
	}

	public void eat(int ammount) throws InsufficientHoneyException {
		Hive.getInstance().subHoney(ammount);
	}
	
	public List<PollenField> getPollenFields() {
		return pollenFields;
	}

	public void incrementPollenFields() {
		for (PollenField pollenField : pollenFields) {
			PollenSupply statusBefore = pollenField.getStatus();
			
			pollenField.addPollenAmmount(Parameters.DAILY_POLLEN_AMMOUNT_INCREASE);
			
			updatePollenField(pollenField, statusBefore);
		}		
	}

	public Larva feedLarva() throws InsufficientHoneyException {
		HoneySupply statusBefore = Hive.getInstance().getStatus();
		
		Larva larva = Hive.getInstance().feedLarva();
		
		HoneySupply statusAfter = Hive.getInstance().getStatus();
		updateHoney(statusBefore, statusAfter);

		return larva;
	}

	public void removeLarva(Larva larva) {
		Hive.getInstance().removeLarva(larva);
		EnvironmentApplication.getInstance().updateLarvaCount();
	}
}