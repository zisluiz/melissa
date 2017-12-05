package graphic.test;

import artifact.Parameters;
import graphic.Environment;
import graphic.JavaFXConcurrent;
import model.Position;
import model.RandomUtils;
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
		instance.launchGraphicApplication(800, 600, Parameters.makePollenFields());

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < 1000; i++) {
			final int id = i;
			
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
			
			Thread t1 = new Thread(new Runnable() {
				public void run() {
					String beeId = "bee"+id;
					instance.registerBee(beeId, "exploradora");
					instance.setPosition(beeId, 761+RandomUtils.getRandom(0, 30), 449);

					for (int z = 0; z < 3; z++) {
						for (int y = 0; y < 180; y++) {

							JavaFXConcurrent.getInstance().addUpdate(new Runnable() {
								@Override
								public void run() {
									try {
										instance.moveBee(beeId, Direction.UP);
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
						Position position = instance.getPosition(beeId);
						System.out.println("X: " + position.getX() + ", Y: " + position.getY());

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						try {
							instance.collect("pollenField4", beeId);
						} catch (PollenIsOverException | NoLongerPollenFieldException
								| CannotCollectOnThisPositionException e) {
							e.printStackTrace();
						}

						for (int y = 0; y < 180; y++) {

							JavaFXConcurrent.getInstance().addUpdate(new Runnable() {
								@Override
								public void run() {
									try {
										instance.moveBee(beeId, Direction.DOWN);
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
							instance.delivery(beeId);
						} catch (CannotDepositOnThisPositionException | NoLongerHiveException
								| NoPollenCollectedException e) {
							e.printStackTrace();
						}
						;

						position = instance.getPosition(beeId);
						System.out.println("X: " + position.getX() + ", Y: " + position.getY());
					}
				}
			});
			t1.start();
		}

	}
}
