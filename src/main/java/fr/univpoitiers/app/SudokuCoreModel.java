package fr.univpoitiers.app;

import fr.univpoitiers.core.solver.*;
import fr.univpoitiers.core.grid.*;

public class SudokuCoreModel {

	private Solver solver;
	private Grid currentGrid;
	private boolean gridLocked;

	public void generateGrid() {
		// TODO - implement SudokuCoreModel.generateGrid
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param width
	 * @param height
	 */
	public void createGrid(int width, int height) {
		currentGrid = new Grid(width, height);
		gridLocked = false;
	}

	/**
	 * 
	 * @param value
	 * @param x
	 * @param y
	 */
	public boolean input(int value, int x, int y) {
		return requireGrid().addNumber(x, y, value);
	}

	public int[] checkEntries() {
		// TODO - implement SudokuCoreModel.checkEntries
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param rules
	 */
	public boolean solve(int[] rules) {
		// TODO - implement SudokuCoreModel.solve
		throw new UnsupportedOperationException();
	}

	public void resetGrid() {
		requireGrid().resetGrid();
		gridLocked = false;
	}

	public int getNumber(int x, int y) {
		return requireGrid().getNumber(x, y);
	}

	public void lockFilledCells() {
		requireGrid().lockFilledCells();
		gridLocked = true;
	}

	public void unlockGrid() {
		requireGrid().unlockCells();
		gridLocked = false;
	}

	public boolean isGridLocked() {
		return gridLocked;
	}

	public boolean isCellLocked(int x, int y) {
		return requireGrid().isCellLocked(x, y);
	}

	private Grid requireGrid() {
		if (currentGrid == null) {
			throw new IllegalStateException("La grille doit être créée avant d'être utilisée.");
		}
		return currentGrid;
	}

	public void clearGrid() {
		// TODO - implement SudokuCoreModel.clearGrid
		throw new UnsupportedOperationException();
	}

}