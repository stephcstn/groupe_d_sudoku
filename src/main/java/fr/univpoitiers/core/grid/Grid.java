package fr.univpoitiers.core.grid;

import java.util.ArrayList;

public class Grid {

	private Cell[] cells;
	private int WIDTH;
	private int HEIGHT;
	private ArrayList<Integer> availableCells;

	/**
	 * 
	 * @param width
	 * @param height
	 */
	public Grid(int width, int height) {
		// TODO - implement Grid.Grid
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param pos
	 * @param value
	 *
	 */
	public boolean checkValidValues(int pos, int value) {
		return checkBlock(pos,value) && checkColumn(pos,value) && checkRow(pos, value);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param value
	 */
	public boolean addNumber(int x, int y, int value) {
		// TODO - implement Grid.addNumber
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public int getNumber(int x, int y) {
		// TODO - implement Grid.getNumber
		throw new UnsupportedOperationException();
	}

	public int nextAvailableCell() {
		// TODO - implement Grid.nextAvailableCell
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param pos
	 */
	public void backCellAvailable(int pos) {
		// TODO - implement Grid.backCellAvailable
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param note
	 */
	public boolean isCellContaining(int x, int y, int note) {
		// TODO - implement Grid.isCellContaining
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void removeNumber(int x, int y) {
		// TODO - implement Grid.removeNumber
		throw new UnsupportedOperationException();
	}

	public void lockFilledCells() {
		// TODO - implement Grid.lockFilledCells
		throw new UnsupportedOperationException();
	}

	public void clearEntries() {
		// TODO - implement Grid.clearEntries
		throw new UnsupportedOperationException();
	}

	public void resetGrid() {
		// TODO - implement Grid.resetGrid
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param x
	 */
	public void lineUpdate(int x) {
		// TODO - implement Grid.lineUpdate
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param y
	 */
	public void columnUpdate(int y) {
		// TODO - implement Grid.columnUpdate
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void blockUpdate(int x, int y) {
		// TODO - implement Grid.blockUpdate
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void diagonalUpdate(int x, int y) {
		// TODO - implement Grid.diagonalUpdate
		throw new UnsupportedOperationException();
	}

	/**
	 * Converts 2D coordinates (x, y) into a 1D array index.
	 *
	 * @param x
	 * @param y
	 * @return the corresponding 1D index
	 */
	public int cellToIndex(int x, int y) {
		return y * WIDTH + x;
	}

	/**
	 * Vérifie que la valeur n'est pas déjà présente sur la ligne.
	 *
	 * @param pos
	 * @param value
	 *
	 */
	public boolean checkRow(int pos, int value) {
		int y = pos / WIDTH;

		for (int x = 0; x < WIDTH; x++) {
			int index = cellToIndex(x, y);
			Cell cell = cells[index];

			if (cell != null && cell.getValue() == value) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Vérifie que la valeur n'est pas déjà présente sur la colonne.
	 * @param pos
	 * @param value
	 *
	 */
	public boolean checkColumn(int pos, int value) {
		int x = pos % WIDTH;

		for (int y = 0; y < HEIGHT; y++) {
			int index = cellToIndex(x, y);
			Cell cell = cells[index];

			if (cell != null && cell.getValue() == value) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Vérifie que la valeur n'est pas déjà présente dans le sous-bloc 3x3.
	 * @param pos
	 * @param value
	 *
	 */
	public boolean checkBlock(int pos, int value) {
		int x = pos % WIDTH;
		int y = pos / WIDTH;

		int startX = (x / 3) * 3;
		int startY = (y / 3) * 3;

		for (int r = startY; r < startY + 3; r++) {
			for (int c = startX; c < startX + 3; c++) {
				int index = cellToIndex(c, r);
				Cell cell = cells[index];

				if (cell != null && cell.getValue() == value) {
					return false;
				}
			}
		}
		return true;
	}

}