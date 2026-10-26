package fr.univpoitiers.core.rules;

import fr.univpoitiers.core.grid.Grid;

import java.util.ArrayList;

public interface RuleApplier {

	/**
	 * Fill the initial domain values from 1 to k.
	 * @param listOfValues domain values list to fill
	 * @param k maximum value of the domain
	 */
	private static void fillValues(ArrayList<Integer> listOfValues, int k) {
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
						listOfValues.remove(cellValue);
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
					listOfValues.remove(cellValue);
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
					listOfValues.remove(cellValue);
				}
			}
		}
	}

	/**
	 * Process if the next cell available is the last in the block or in the row or in the column and a value can be assigned.
	 * If possible, the value is set in the grid and removed from the available cells.
	 * @param grid current grid
	 * @return If the cell is updated
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

	private static boolean lastRemainingCell(Grid grid) {
		int cellIndex = grid.nextAvailableCell();
		ArrayList<Integer> listOfValues = new ArrayList<>();
		fillValues(listOfValues, grid.getWidth());
		listOfValuesBlock(grid, listOfValues, cellIndex);

		return false;
	}

	/**
	 * @param grid
	 */
	static boolean BasicRules(Grid grid) {
		return lastFreeCell(grid) || lastRemainingCell(grid) || lastPossibleCell(grid);
	}

	/**
	 * 
	 * @param grid
	 */
	static boolean KObvious(Grid grid, int k) {
        // Init list of value that will be present in
        ArrayList<Integer> listOfValues = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            listOfValues.add(i);
        }
        int startBlockIndex = grid.getBlockIndex(grid.nextAvailableCell());
        int indexToSet = -1;
        // Iterating line by line 2 times more after the first
        for (int i = startBlockIndex; i <= startBlockIndex + grid.getHeight() * 2; i *= 9) {
            for (int j = i; j < i + 3 ; j++) {
                int cellValue = grid.getNumber(j);
                if (cellValue != 0) {
                    if (listOfValues.contains(cellValue)) {
                        listOfValues.remove(cellValue);
                    }
                } else {
                    indexToSet = indexToSet == -1 ? j : -1;
                }
            }
        }
        if (listOfValues.size() == 1) {
            return grid.addNumber(indexToSet, listOfValues.getFirst());
        }
		int startRowIndex = grid.getRowIndex(grid.nextAvailableCell());
		for (int i = startRowIndex; i < startRowIndex + grid.getWidth(); i++) {
			int cellValue = grid.getNumber(i);
			if (cellValue != 0) {
				if (listOfValues.contains(cellValue)) {
					listOfValues.remove(cellValue);
				}
			} else {
				indexToSet = indexToSet == -1 ? j : -1;
			}
		}
		if (listOfValues.size() == 1) {
			return grid.addNumber(indexToSet, listOfValues.getFirst());
		}

		return false;
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