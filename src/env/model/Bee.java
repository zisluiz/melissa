package model;

public class Bee {
	private String id;
	private int age;
	private Position position = new Position();
	private int pollenCollected;

	public Bee(String id, int age) {
		this.id = id;
		this.age = age;
	}
	
	public void setPosition(int x, int y) {
		this.position.setXY(x, y);
	}
	
	public String getId() {
		return id;
	}
	
	public int getAge() {
		return age;
	}
	
	public Position getPosition() {
		return position;
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
