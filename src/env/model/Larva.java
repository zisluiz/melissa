package model;

import artifact.Parameters;

public class Larva {
	private int honeyEated;
	
	public Larva() {
	}
	
	public int getHoneyEated() {
		return honeyEated;
	}

	public boolean isEvolving() {
		return honeyEated >= Parameters.LARVA_TOTAL_HONEY_TO_EVOLVE;
	}

	public void feed(int honey) {
		honeyEated += honey;
	}
}