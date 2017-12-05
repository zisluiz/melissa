package graphic.test;

import artifact.Parameters;
import graphic.Environment;

public class Test2 {
	public static void main(String[] args) {
		Environment instance = Environment.getInstance();
		instance.launchGraphicApplication(800, 600, Parameters.makePollenFields());

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		
		String beeId = "bee"+1;
		instance.registerBee(beeId, "exploradora");
		instance.setPosition(beeId, 500, 400);

	}
}
