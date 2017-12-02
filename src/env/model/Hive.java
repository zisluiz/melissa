package model;

import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.geom.Rectangle;

import artifact.Parameters;
import model.enumeration.HoneySupply;
import model.exception.InsufficientHoneyException;
import model.exception.InsufficientPollenException;

public class Hive {
	private static Hive instance;
	private static int maxHoney = Parameters.MAX_HIVE_HONEY;

	private int temperature;

	private List<Bee> queens = new ArrayList<>();
	private List<Bee> feeders = new ArrayList<>();
	private List<Bee> sentinels = new ArrayList<>();
	private List<Bee> workers = new ArrayList<>();
	private List<Larva> larvas = new ArrayList<>();

	private int honey;
	private int pollen;
	
	private Rectangle hive;

	private Hive() {
	}
	
	public HoneySupply getStatus() {
		if (honey < maxHoney * Parameters.HIVE_SUPPLY_EMPTY)
			return HoneySupply.EMPTY;
		else if (honey < maxHoney * Parameters.HIVE_SUPPLY_LOW)
			return HoneySupply.LOW;
		else if (honey < maxHoney * Parameters.HIVE_SUPPLY_MEDIUM)
			return HoneySupply.MEDIUM;
		else if (honey < maxHoney * Parameters.HIVE_SUPPLY_HIGH)
			return HoneySupply.HIGH;
		else
			return HoneySupply.FULL;
	}
	
	public static Hive getInstance() {
		if (instance == null) 
			instance = new Hive();
		
		return instance;
	}
	
	public Rectangle getHiveRect() {
		return hive;
	}
	
	public void setHiveRect(Rectangle rect) {
		hive = rect;
	}
	
	public void setHiveRect(int x, int y, int width, int heigth) {
		hive = new Rectangle(x, y, width, heigth);
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public List<Bee> getFeeders() {
		return feeders;
	}
	
	public List<Larva> getLarvas() {
		return larvas;
	}
	
	public int getHoney() {
		return honey;
	}
	
	public void setHoney(int honey) {
		this.honey = honey;
	}	
	
	public int getPollen() {
		return pollen;
	}
	
	public void setPollen(int pollen) {
		this.pollen = pollen;
	}
	
	public List<Bee> getQueens() {
		return queens;
	}
	public List<Bee> getSentinels() {
		return sentinels;
	}
	
	public List<Bee> getWorkers() {
		return workers;
	}

	public void createLarva() {
		larvas.add(new Larva(0));
	}

	public Bee createFeeder(String id) {
		Bee bee = new Bee(id);
		feeders.add(bee);
		return bee;
	}

	public Bee createSentinel(String id) {
		Bee bee = new Bee(id);
		sentinels.add(bee);
		return bee;
	}

	public Bee createWorker(String id) {
		Bee bee = new Bee(id);
		workers.add(bee);
		return bee;
	}

	public Bee createQueen(String id) {
		Bee bee = new Bee(id);
		queens.add(bee);
		return bee;
	}

	synchronized public void addHoney(int ammount) {
		honey = honey + ammount;
	}
	
	synchronized public void subHoney(int ammount) throws InsufficientHoneyException {
		if (honey >= ammount) {
			honey = honey - ammount;
		} else
			throw new InsufficientHoneyException("The honey is gone.");
	}	

	synchronized public void addPollen(int ammount) {
		pollen = pollen + ammount;
	}
	
	synchronized public void subPollen(int ammount) throws InsufficientPollenException {
		if (pollen >= ammount) {
			pollen = pollen - ammount;
		} else
			throw new InsufficientPollenException("The pollen is gone.");
	}	
}
