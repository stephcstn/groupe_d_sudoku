package fr.univpoitiers.core.rules;

import fr.univpoitiers.core.grid.Grid;

public interface RuleApplier {

	/**
	 * 
	 * @param grid
	 */
	static boolean KObvious(Grid grid){
		return true;
	}

	/**
	 * 
	 * @param grid
	 */
	static boolean KHidden(Grid grid){
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