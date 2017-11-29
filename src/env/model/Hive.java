package model;

import java.util.ArrayList;
import java.util.List;

import model.enumeration.HoneySupply;
import model.exception.InsufficientHoneyException;

public class Hive {
	private static Hive instance;
	private static int maxHoney = 1000;

	private int temperature;

	private List<Bee> queens = new ArrayList<>();
	private List<Bee> feeders = new ArrayList<>();
	private List<Bee> sentinels = new ArrayList<>();
	private List<Bee> workers = new ArrayList<>();
	private List<Larva> larvas = new ArrayList<>();

	private int honey;

	private Hive() {
	}
	
	public HoneySupply getStatus() {
		if (honey < maxHoney * 0.05)
			return HoneySupply.EMPTY;
		else if (honey < maxHoney * 0.2)
			return HoneySupply.LOW;
		else if (honey < maxHoney * 0.6)
			return HoneySupply.MEDIUM;
		else if (honey < maxHoney * 0.9)
			return HoneySupply.HIGH;
		else
			return HoneySupply.FULL;
	}
	
	public static Hive getInstance() {
		if (instance == null) 
			instance = new Hive();
		
		return instance;
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

	public Bee createFeeder(String id, int age) {
		Bee bee = new Bee(id, age);
		feeders.add(bee);
		return bee;
	}

	public Bee createSentinel(String id, int age) {
		Bee bee = new Bee(id, age);
		sentinels.add(bee);
		return bee;
	}

	public Bee createWorker(String id, int age) {
		Bee bee = new Bee(id, age);
		workers.add(bee);
		return bee;
	}

	public Bee createQueen(String id, int age) {
		Bee bee = new Bee(id, age);
		queens.add(bee);
		return bee;
	}

	public void addHoney(int ammount) {
		honey = honey + ammount;
	}
	
	synchronized public void subHoney(int ammount) throws InsufficientHoneyException {
		if (honey >= ammount) {
			honey = honey - ammount;
		} else
			throw new InsufficientHoneyException("The honey is gone.");
	}	
}
