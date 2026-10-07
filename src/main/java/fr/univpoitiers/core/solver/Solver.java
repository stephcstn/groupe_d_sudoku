package fr.univpoitiers.core.solver;

import fr.univpoitiers.core.grid.Grid;
import fr.univpoitiers.core.rules.RuleApplier;

public class Solver implements RuleApplier {

	private ExecutionStack executionStack;
	private Grid solvedGrid;

	public Solver() {
		// TODO - implement Solver.Solver
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param grid
	 */
	private void addSolvedGrid(Grid grid) {

	}

	/**
	 * 
	 * @param grid
	 * @param rules
	 */
	public boolean solve(Grid grid, int[] rules) {
		return true;
	}

	/**
	 * 
	 * @param grid
	 */
	public int[] checkEntries(Grid grid) {
		return null;

	}

	/**
	 * 
	 * @param grid
	 */
	public void generate(Grid grid) {

	}

	/**
	 * 
	 * @param grid
	 * @param countNumberLasting
	 */
	private void reduce(Grid grid, int countNumberLasting) {

	}

}