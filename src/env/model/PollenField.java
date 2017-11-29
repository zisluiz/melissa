package model;

import model.enumeration.PollenSupply;
import model.exception.PollenIsOverException;

public class PollenField {
	private String id;
	private int maxAmmount;
	private int ammount;

	public PollenField(String id, int maxAmmount, int ammount) {
		this.id = id;
		this.maxAmmount = maxAmmount;
		this.ammount = ammount;
	}
	
	public PollenSupply getStatus() {
		if (ammount < maxAmmount * 0.3)
			return PollenSupply.LOW;
		else if (ammount < maxAmmount * 0.6)
			return PollenSupply.MEDIUM;
		else
			return PollenSupply.HIGH;
	}

	public int getAmmount() {
		return ammount;
	}

	public int getMaxAmmount() {
		return maxAmmount;
	}
	
	public String getId() {
		return id;
	}

	public int collect() throws PollenIsOverException {
		if (ammount >= 10) {
			ammount = ammount - 10;
			return 10;
		} else
			throw new PollenIsOverException("Pollen is over on field "+id);
	}
}