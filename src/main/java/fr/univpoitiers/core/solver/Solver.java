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
		// TODO - implement Solver.addSolvedGrid
		throw new UnsupportedOperationException("addSolvedGrid n'est pas encore implémenté.");
	}

	/**
	 * 
	 * @param grid
	 * @param rules
	 */
	public boolean solve(Grid grid, int[] rules) {
		// TODO - implement Solver.solve
		return true;
	}

	/**
	 * 
	 * @param grid
	 */
	public int[] checkEntries(Grid grid) {
		// TODO - implement Solver.checkEntries
		return null;
	}

	/**
	 * 
	 * @param grid
	 */
	public void generate(Grid grid) {
		// TODO - implement Solver.generate
		throw new UnsupportedOperationException("generate n'est pas encore implémenté.");
	}

	/**
	 * 
	 * @param grid
	 * @param countNumberLasting
	 */
	private void reduce(Grid grid, int countNumberLasting) {
		// TODO - implement Solver.reduce
		throw new UnsupportedOperationException("reduce n'est pas encore implémenté.");
	}

}