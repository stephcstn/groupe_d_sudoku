package fr.univpoitiers.core.solver;

import fr.univpoitiers.core.grid.Grid;
import fr.univpoitiers.core.rules.RuleApplier;

public class Solver implements RuleApplier {

	private ExecutionStack executionStack;
	private Grid solvedGrid;

	public Solver(Grid solvedGrid) {
		this.solvedGrid = solvedGrid;
		executionStack = new ExecutionStack();
	}

	/**
	 * Replace the previous solution by another different solution of the grid.
	 * @param newSolvedGrid
	 */
	private void setSolvedGrid(Grid newSolvedGrid) {
		solvedGrid = newSolvedGrid;
	}

	/**
	 * Resolve the current grid based on rules that are given.
	 * @param grid - The current grid to resolve
	 * @param rules - All rules applied
	 */
	public boolean solve(Grid grid, int[] rules) {
		return true;
	}

	private void lastFreeCell(Grid grid) {

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