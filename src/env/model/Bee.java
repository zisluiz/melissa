package model;

public class Bee {
	private String id;
	private int age;
	private int x;
	private int y;
	private int pollenCollected;

	public Bee(String id, int age) {
		this.id = id;
		this.age = age;
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public String getId() {
		return id;
	}
	
	public int getAge() {
		return age;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	public void setPollenCollected(int ammount) {
		this.pollenCollected = ammount;
	}
	
	public int takePollenCollected() {
		int ammount = pollenCollected;
		pollenCollected = 0;
		return ammount;
	}
}
