package fr.univpoitiers.core.solver;

import fr.univpoitiers.core.grid.Grid;
import fr.univpoitiers.core.rules.RuleApplier;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Solves a Sudoku grid by repeatedly applying the deduction rules of {@link RuleApplier},
 * recording each successful step in an {@link ExecutionStack}.
 */
public class Solver implements RuleApplier {

	private ExecutionStack executionStack;
	private Grid solvedGrid;

	/**
	 * Creates a solver for a grid whose solution is already known.
	 *
	 * @param solvedGrid the reference solution of the grid
	 */
	public Solver(Grid solvedGrid) {
		this.solvedGrid = solvedGrid;
		executionStack = new ExecutionStack();
	}

	/**
	 * Replace the previous solution by another different solution of the grid.
	 * @param newSolvedGrid the new reference solution
	 */
	private void setSolvedGrid(Grid newSolvedGrid) {
		solvedGrid = newSolvedGrid;
	}

	/**
	 * Resolve the current grid based on rules that are given.
	 * @param grid - The current grid to resolve
	 * @param rules - All rules applied
	 * @return true if the grid was completely filled before all rules were exhausted
	 */
	public boolean solve(Grid grid, int[] rules) {
		int ruleApplierIndex = 0;
		boolean hasNextValue;
		try {
			grid.nextAvailableCell(); // Throws NoSuchElementException if there's
			hasNextValue = true;
			RuleApplier.annotate(grid);
		} catch (Exception e) {
			hasNextValue = false;
		}

		while (ruleApplierIndex < rules.length && hasNextValue) {
			Grid clonedGridToStack = new Grid(grid);
			switch (ruleApplierIndex) {
				case 0:
					if (!RuleApplier.KObvious(grid)) {
						ruleApplierIndex++;
					} else executionStack.stack(clonedGridToStack, ruleApplierIndex);
					break;
				case 1:
					if (RuleApplier.KHidden(grid)) {
						executionStack.stack(clonedGridToStack, ruleApplierIndex);
						ruleApplierIndex = 0;
					} else {
						ruleApplierIndex++;
					}
					break;
				case 3:
					if (RuleApplier.PointingPair(grid)) {
						executionStack.stack(clonedGridToStack, ruleApplierIndex);
						ruleApplierIndex = 0;
					} else {
						ruleApplierIndex++;
					}
					break;
				case 4:
					if (RuleApplier.PointingTriple(grid)) {
						executionStack.stack(clonedGridToStack, ruleApplierIndex);
						ruleApplierIndex = 0;
					} else {
						ruleApplierIndex++;
					}
					break;
				case 5:
					if (RuleApplier.Swordfish(grid)) {
						executionStack.stack(clonedGridToStack, ruleApplierIndex);
						ruleApplierIndex = 0;
					} else {
						ruleApplierIndex++;
					}
					break;
				case 6:
					if (RuleApplier.XWing(grid)) {
						executionStack.stack(clonedGridToStack, ruleApplierIndex);
						ruleApplierIndex = 0;
					} else {
						ruleApplierIndex++;
					}
					break;
				case 7:
					if (RuleApplier.YWing(grid)) {
						executionStack.stack(clonedGridToStack, ruleApplierIndex);
						ruleApplierIndex = 0;
					} else {
						ruleApplierIndex++;
					}
					break;
				case 8:
					if (RuleApplier.FirstNaive(grid)) {
						executionStack.stack(clonedGridToStack, ruleApplierIndex);
						ruleApplierIndex = 0;
					} else {
						ruleApplierIndex++;
					}
					break;
			}
			try {
				grid.nextAvailableCell(); // Throws NoSuchElementException if there's no more cells available
			} catch (NoSuchElementException e) {
				hasNextValue = false;
			}
		}
		return ruleApplierIndex < rules.length && !hasNextValue;
	}

	/**
	 * Checks the values entered in the grid against the solution. Not implemented yet.
	 *
	 * @param grid the grid to check
	 * @return the positions of the wrong entries; currently always {@code null}
	 */
	public int[] checkEntries(Grid grid) {
		// TODO - implement Solver.checkEntries
		return null;
	}

	/**
	 * Generates a new puzzle in the given grid. Not implemented yet.
	 *
	 * @param grid the grid to fill
	 * @throws UnsupportedOperationException always, until the method is implemented
	 */
	public void generate(Grid grid) {
		// TODO - implement Solver.generate
		throw new UnsupportedOperationException("generate n'est pas encore implémenté.");
	}

	/**
	 * Removes values from a full grid until the given number of values remains. Not implemented yet.
	 *
	 * @param grid the grid to reduce
	 * @param countNumberLasting the number of values to keep
	 * @throws UnsupportedOperationException always, until the method is implemented
	 */
	private void reduce(Grid grid, int countNumberLasting) {
		// TODO - implement Solver.reduce
		throw new UnsupportedOperationException("reduce n'est pas encore implémenté.");
	}

}