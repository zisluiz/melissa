package model;

import artifact.Parameters;
import model.enumeration.PollenSupply;
import model.exception.PollenIsOverException;

public class PollenField {
	private String id;
	private int maxAmmount;
	private int ammount;
	private Position position;
	private int width;
	private int height;

	public PollenField(String id, Position position, int width, int height, int maxAmmount, int ammount) {
		this.id = id;
		this.position = position;
		this.width = width;
		this.height = height;
		this.maxAmmount = maxAmmount;
		this.ammount = ammount;
	}
	
	public PollenSupply getStatus() {
		if (getAmmount() < maxAmmount * Parameters.POLLEN_SUPPLY_LOW)
			return PollenSupply.LOW;
		else if (getAmmount() < maxAmmount * Parameters.POLLEN_SUPPLY_MEDIUM)
			return PollenSupply.MEDIUM;
		else
			return PollenSupply.HIGH;
	}

	synchronized public int getAmmount() {
		return ammount;
	}

	public int getMaxAmmount() {
		return maxAmmount;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public String getId() {
		return id;
	}

	public int collect() throws PollenIsOverException {
		if (getAmmount() >= Parameters.AMMOUNT_BEE_COLLECT_POLLEN) {
			setAmmount(getAmmount() - Parameters.AMMOUNT_BEE_COLLECT_POLLEN);
			return Parameters.AMMOUNT_BEE_COLLECT_POLLEN;
		} else
			throw new PollenIsOverException("Pollen is over on field "+id);
	}

	private void setAmmount(int ammount) {
		this.ammount = ammount;
	}

	public void addPollenAmmount(int dailyPollenAmmountIncrease) {
		int newAmmount = getAmmount() + dailyPollenAmmountIncrease;
		
		if (newAmmount <= maxAmmount)
			setAmmount(newAmmount);
	}
}