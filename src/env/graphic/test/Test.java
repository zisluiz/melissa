package graphic.test;

import graphic.Environment;
import model.JavaFXConcurrent;
import model.Position;
import model.enumeration.Direction;
import model.exception.CannotCollectOnThisPositionException;
import model.exception.CannotDepositOnThisPositionException;
import model.exception.InvalidMovimentException;
import model.exception.MovimentOutOfBoundsException;
import model.exception.NoLongerHiveException;
import model.exception.NoLongerPollenFieldException;
import model.exception.NoPollenCollectedException;
import model.exception.PollenIsOverException;

public class Test {
	public static void main(String[] args) {
		Environment instance = Environment.getInstance();
		instance.launchGraphicApplication(800, 600);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		instance.registerBee("1", "worker", 10);
		instance.setPosition("1", 790, 449);

		for (int z = 0; z < 3; z++) {
			for (int y = 0; y < 180; y++) {
				
				JavaFXConcurrent.getInstance().addUpdate(new Runnable() {
					@Override
					public void run() {
						try {
							instance.moveBee("1", Direction.UP);
						} catch (MovimentOutOfBoundsException | InvalidMovimentException e) {
							e.printStackTrace();
						}
					}
				});
	
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}			
			}
			Position position = instance.getPosition("1");
			System.out.println("X: "+position.getX()+", Y: "+position.getY());
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}		
			
			try {
				instance.collect("pollenField4", "1");
			} catch (PollenIsOverException | NoLongerPollenFieldException | CannotCollectOnThisPositionException e) {
				e.printStackTrace();
			}
			
			for (int y = 0; y < 180; y++) {
				
				JavaFXConcurrent.getInstance().addUpdate(new Runnable() {
					@Override
					public void run() {
						try {
							instance.moveBee("1", Direction.DOWN);
						} catch (MovimentOutOfBoundsException | InvalidMovimentException e) {
							e.printStackTrace();
						}
					}
				});
	
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}			
			}	
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}				
			
			try {
				instance.delivery("1");
			} catch (CannotDepositOnThisPositionException | NoLongerHiveException | NoPollenCollectedException e) {
				e.printStackTrace();
			};
		
			position = instance.getPosition("1");
			System.out.println("X: "+position.getX()+", Y: "+position.getY());		
		}
	}
}
