package fr.univpoitiers.core.solver;

import fr.univpoitiers.core.grid.Grid;
import fr.univpoitiers.core.rules.RuleApplier;

import java.util.ArrayList;
import java.util.NoSuchElementException;

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
		int ruleApplierIndex = 0;
		boolean hasNextValue;
		try {
			grid.nextAvailableCell(); // Throws NoSuchElementException if there's
			hasNextValue = true;
		} catch (Exception e) {
			hasNextValue = false;
		}
		annotate(grid);
		while (ruleApplierIndex < rules.length && hasNextValue) {
			switch (ruleApplierIndex) {
				case 0:
					if (!RuleApplier.KObvious(grid)) {
						ruleApplierIndex++;
					}
					break;
				case 1:
					if (RuleApplier.KHidden(grid)) {
						ruleApplierIndex = 0;
					} else {
						ruleApplierIndex++;
					}
					break;
				case 2:
					if (RuleApplier.PointingPair(grid)) {
						ruleApplierIndex = 0;
					} else {
						ruleApplierIndex++;
					}
					break;
				case 3:
					if (RuleApplier.PointingTriple(grid)) {
						ruleApplierIndex = 0;
					} else {
						ruleApplierIndex++;
					}
					break;
				case 4:
					if (RuleApplier.SwordFish(grid)) {
						ruleApplierIndex = 0;
					} else {
						ruleApplierIndex++;
					}
					break;
				case 5:
					if (RuleApplier.XWing(grid)) {
						ruleApplierIndex = 0;
					} else {
						ruleApplierIndex++;
					}
					break;
				case 6:
					if (RuleApplier.YWing(grid)) {
						ruleApplierIndex = 0;
					} else {
						ruleApplierIndex++;
					}
					break;
				case 7:
					if (RuleApplier.FirstNaive(grid)) {
						ruleApplierIndex = 0;
					} else {
						ruleApplierIndex++;
					}
					break;
			}
			try {
				grid.nextAvailableCell(); // Throws NoSuchElementException if there's no more cells available
				hasNextValue = true;
			} catch (NoSuchElementException e) {
				hasNextValue = false;
			}
		}
		return ruleApplierIndex < rules.length && !hasNextValue;
	}

	private void restrictFromBlock(Grid grid, int currentPos, ArrayList<Integer> notesToAdd) {
		int value;
		int startIndex = grid.getBlockIndex(currentPos);
		for (int i = startIndex; i <= startIndex + 9 * 2; i++) {
			for (int j = i; j < i + 3; j++) {
				value = grid.getNumber(j);
				if (value != 0 && notesToAdd.contains(value)) {
					notesToAdd.remove(value);
				}
			}
		}
	}

	private void restrictFromLine(Grid grid, int currentPos, ArrayList<Integer> notesToAdd) {
		int value;
		int startIndex = grid.getLineIndex(currentPos);
		for (int i = startIndex; i < startIndex + 9; i++) {
			value = grid.getNumber(i);
			if (value != 0 && notesToAdd.contains(value)) {
				notesToAdd.remove(value);
			}
		}
	}

	private void restrictFromColumn(Grid grid, int currentPos, ArrayList<Integer> notesToAdd) {
		int value;
		int startIndex = grid.getColumnIndex(currentPos);
		for (int i = startIndex; i <= startIndex * 9; i += 9) {
			value = grid.getNumber(i);
			if (value != 0 && notesToAdd.contains(value)) {
				notesToAdd.remove(value);
			}
		}
	}

	private void annotate(Grid grid) {
		ArrayList<Integer> allNumbers = new ArrayList<>();
		for (int val = 1; val < grid.getWidth(); val++) {
			allNumbers.add(val);
		}
		ArrayList<Integer> notesToAdd = new ArrayList<>();

		for (int pos = 0; pos < grid.getWidth(); pos++) {
			notesToAdd.addAll(allNumbers);
			restrictFromBlock(grid, pos, notesToAdd);
			restrictFromLine(grid, pos, notesToAdd);
			restrictFromColumn(grid, pos, notesToAdd);
			// restrictFromDiagonal(notesToAdd, startIndex); TODO: NOT NOW

			for (Integer note : notesToAdd) {
				grid.addNoteToCell(i, note);
			}
			notesToAdd.clear();
		}
		executionStack.stack(grid, -1);
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