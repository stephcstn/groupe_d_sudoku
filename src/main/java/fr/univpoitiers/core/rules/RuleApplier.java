package fr.univpoitiers.core.rules;

import fr.univpoitiers.core.grid.Grid;

import java.util.ArrayList;

public interface RuleApplier {

	/**
	 * Fill the initial domain values from 1 to k included.
	 * @param listOfValues domain values list to fill
	 * @param k maximum value of the domain
	 */
	static void fillValues(ArrayList<Integer> listOfValues, int k) {
		listOfValues.clear();
		for (int i = 1; i < k + 1; i++) {
			listOfValues.add(i);
		}
	}

	/**
	 * Remove the domain values possible in the block from the initial index cell.
	 * @param grid current grid
	 * @param listOfValues list of domain values to update
	 * @param cellIndex cell belonging to the block processing
	 */
	private static void listOfValuesBlock(Grid grid, ArrayList<Integer> listOfValues, int cellIndex) {
		int startBlockIndex = grid.getBlockIndex(cellIndex);
		// Iterating line by line 2 times more after the first
		for (int i = startBlockIndex; i <= startBlockIndex + grid.getHeight() * 2; i += grid.getHeight()) {
			for (int j = i; j < i + 3 ; j++) {
				int cellValue = grid.getNumber(j);
				if (cellValue != 0) {
					if (listOfValues.contains(cellValue)) {
						listOfValues.remove((Integer) cellValue);
					}
				}
			}
		}
	}

	/**
	 * Remove the domain values possible in the row from the initial index cell.
	 * @param grid current grid
	 * @param listOfValues list of domain values to update
	 * @param cellIndex cell belonging to the row processing
	 */
	private static void listOfValuesRow(Grid grid, ArrayList<Integer> listOfValues, int cellIndex) {
		int startRowIndex = grid.getRowIndex(cellIndex);
		for (int i = startRowIndex; i < startRowIndex + grid.getWidth(); i++) {
			int cellValue = grid.getNumber(i);
			if (cellValue != 0) {
				if (listOfValues.contains(cellValue)) {
					listOfValues.remove((Integer) cellValue);
				}
			}
		}
	}

	/**
	 * Remove the domain values possible in the column from the initial index cell.
	 * @param grid current grid
	 * @param listOfValues list of domain values to update
	 * @param cellIndex cell belonging to the column processing
	 */
	private static void listOfValuesColumn(Grid grid, ArrayList<Integer> listOfValues, int cellIndex) {
		int startColumnIndex = grid.getColumnIndex(cellIndex);
		for (int i = startColumnIndex; i < startColumnIndex + (grid.getHeight() - 1) * grid.getHeight(); i += grid.getHeight()) {
			int cellValue = grid.getNumber(i);
			if (cellValue != 0) {
				if (listOfValues.contains(cellValue)) {
					listOfValues.remove((Integer) cellValue);
				}
			}
		}
	}

	/**
	 * Process if the next cell available is the last in the block or in the row or in the column and a value can be assigned.
	 * If possible, the value is set in the grid and removed from the available cells.
	 * @param grid current grid
	 * @return If it has changed grid state
	 */
	private static boolean lastFreeCell(Grid grid) {
		int cellIndex = grid.nextAvailableCell();
		ArrayList<Integer> listOfValues = new ArrayList<>();

		fillValues(listOfValues, grid.getWidth());
		listOfValuesBlock(grid, listOfValues, cellIndex);
		if (listOfValues.size() == 1) {
			grid.removeAvailableCell(cellIndex);
			return grid.addNumber(cellIndex, listOfValues.getFirst());
		}

		fillValues(listOfValues, grid.getWidth());
		listOfValuesRow(grid, listOfValues, cellIndex);
		if (listOfValues.size() == 1) {
			grid.removeAvailableCell(cellIndex);
			return grid.addNumber(cellIndex, listOfValues.getFirst());
		}

		fillValues(listOfValues, grid.getWidth());
		listOfValuesColumn(grid , listOfValues, cellIndex);
		if (listOfValues.size() == 1) {
			grid.removeAvailableCell(cellIndex);
			return grid.addNumber(cellIndex, listOfValues.getFirst());
		}
		return false;
	}

	/**
	 * Select all rows and columns in the block that doesn't include the current cell
	 * and try to check if only one value can only be in the current cell and insert it if possible.
	 * @param grid the current grid
	 * @return If it has changed grid state
	 */
	private static boolean lastRemainingCell(Grid grid) {
		int cellIndex = grid.nextAvailableCell();
		ArrayList<Integer> listOfValues = new ArrayList<>();
		fillValues(listOfValues, grid.getWidth());
		listOfValuesBlock(grid, listOfValues, cellIndex);
		// Remove all values present in rows and columns in the block that doesn't include this cell
		int blockPosX = cellIndex % 3;
		int blockPosY = (cellIndex / 9) % 3;
		int startBlock = grid.getBlockIndex(cellIndex);
		for (int j = startBlock; j < startBlock + grid.getHeight() * 2; j += grid.getHeight()) {
			for (int i = j; i < j + 3; j++) {
				if (i % 3 != blockPosX
						&& (i / 9) % 3 != blockPosY
						&& grid.getNumber(i) != 0
						&& listOfValues.contains(grid.getNumber(i))) {
					listOfValues.remove((Integer) grid.getNumber(i));
				}
			}
		}
		if (listOfValues.size() == 1) {
			grid.removeAvailableCell(cellIndex);
			return grid.addNumber(cellIndex, listOfValues.getFirst());
		}

		return false;
	}

	/**
	 * Get the last possible value to assign to the current cell taking
	 * in account the block, the row and the columns values already present that it belongs.
	 * @param grid The current grid
	 * @return If it changed the grid state
	 */
	private static boolean lastPossibleCell(Grid grid) {
		int cellIndex = grid.nextAvailableCell();
		ArrayList<Integer> listOfValues = new ArrayList<>();
		fillValues(listOfValues, grid.getWidth());
		listOfValuesBlock(grid, listOfValues, cellIndex);
		// Remove all values present in rows and columns in the block that doesn't include this cell
		int blockPosX = cellIndex % 3;
		int blockPosY = (cellIndex / 9) % 3;
		int startBlock = grid.getBlockIndex(cellIndex);
		for (int j = startBlock; j < startBlock + grid.getHeight() * 2; j += grid.getHeight()) {
			for (int i = j; i < j + 3; j++) {
				if (i % 3 == blockPosX
						&& (i / 9) % 3 == blockPosY
						&& grid.getNumber(i) != 0
						&& listOfValues.contains(grid.getNumber(i))) {
					listOfValues.remove((Integer) grid.getNumber(i));
				}
			}
		}
		if (listOfValues.size() == 1) {
			grid.removeAvailableCell(cellIndex);
			return grid.addNumber(cellIndex, listOfValues.getFirst());
		}

		return false;
	}

	/**
	 * Wrap all basic rules : Last free cell, Last remaining cell, and Last possible cell.
	 * @param grid The current grid
	 * @return If one of those rule has changed the grid state
	 */
	static boolean BasicRules(Grid grid) {
		return lastFreeCell(grid) || lastRemainingCell(grid) || lastPossibleCell(grid);
	}

	/**
	 * 
	 * @param grid
	 */
	static boolean KObvious(Grid grid) {
		return true;
	}

	/**
	 * 
	 * @param grid
	 */
	static boolean KHidden(Grid grid) {
		return true;
	}

	/**
	 * 
	 * @param grid
	 */
	static boolean PointingPair(Grid grid){
		return true;
	}

	/**
	 * 
	 * @param grid
	 */
	static boolean PointingTriple(Grid grid){
		return true;
	}

	/**
	 * 
	 * @param grid
	 */
	static boolean YWing(Grid grid){
		return true;
	}

	/**
	 * 
	 * @param grid
	 */
	static boolean XWing(Grid grid){
		return true;
	}

	/**
	 * 
	 * @param grid
	 */
	static boolean SwordFish(Grid grid){
		return true;
	}

	/**
	 * 
	 * @param grid
	 */
	static boolean FirstNaive(Grid grid){
		return true;
	}

}