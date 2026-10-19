package fr.univpoitiers.core.rules;

import fr.univpoitiers.core.grid.Grid;

public interface RuleApplier {

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
        for (int i = startBlockIndex; i <= startBlockIndex + 9 * 2; i *= 9) {
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
        if (listOfValues.size() == 1 && indexToSet != -1) {
            grid.addNumber(j, listOfValues.getFirst());
            executionStack.stack(grid.clone(), 0); // throws CloneNotSupportedSupported if cannot be cloned
			return true;
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