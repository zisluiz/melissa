package model;

import java.util.Random;

public class RandomUtils {
	public static int getRandom(int min, int max) {
		Random r = new Random();
		return r.ints(min, (max + 1)).findFirst().getAsInt();
	}
}