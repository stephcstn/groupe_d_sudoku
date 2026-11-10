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
	 * Saisit une valeur dans la case de coordonnées {@code (x, y)}.
	 *
	 * @param value valeur à saisir, 0 pour vider la case
	 * @param x     colonne de la case, à partir de 0
	 * @param y     ligne de la case, à partir de 0
	 * @return vrai si la saisie a été acceptée
	 * @throws IllegalArgumentException si les coordonnées sont hors de la grille
	 */
	public boolean input(int value, int x, int y) {
		Grid grid = requireGrid();
		int index = toIndex(grid, x, y);
		if (value == 0) {
			// Vider une case : removeNumber la remet aussi dans les cases disponibles.
			grid.removeNumber(index);
			return true;
		}
		return grid.addNumber(index, value);
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

	/**
	 * Lit la valeur de la case de coordonnées {@code (x, y)}.
	 *
	 * @param x colonne de la case, à partir de 0
	 * @param y ligne de la case, à partir de 0
	 * @return la valeur, ou 0 si la case est vide
	 * @throws IllegalArgumentException si les coordonnées sont hors de la grille
	 */
	public int getNumber(int x, int y) {
		Grid grid = requireGrid();
		return grid.getNumber(toIndex(grid, x, y));
	}

	public void lockFilledCells() {
		requireGrid().lockFilledCells();
		gridLocked = true;
	}

	public void unlockGrid() {
		requireGrid().lockFilledCells();
		gridLocked = false;
	}

	public boolean isGridLocked() {
		return gridLocked;
	}

	/**
	 * Convertit des coordonnées 2D en index 1D de la grille, après avoir vérifié qu'elles
	 * désignent bien une case ({@link Grid#cellToIndex(int, int)} ne fait pas cette vérification).
	 *
	 * @param grid grille concernée
	 * @param x    colonne de la case
	 * @param y    ligne de la case
	 * @return l'index 1D de la case
	 * @throws IllegalArgumentException si les coordonnées sont hors de la grille
	 */
	private static int toIndex(Grid grid, int x, int y) {
		if (x < 0 || x >= grid.getWidth() || y < 0 || y >= grid.getHeight()) {
			throw new IllegalArgumentException("Coordonnées hors de la grille : (" + x + ", " + y + ").");
		}
		return grid.cellToIndex(x, y);
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