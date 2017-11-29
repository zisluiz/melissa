package graphic.test;

import graphic.Environment;
import javafx.application.Platform;
import model.enumeration.Direction;
import model.exception.InvalidMovimentException;
import model.exception.MovimentOutOfBoundsException;

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
		instance.setPosition("1", 790, 280);

		for (int y = 0; y < 20; y++) {
			Platform.runLater(new Runnable() {
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
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}

	}
}
