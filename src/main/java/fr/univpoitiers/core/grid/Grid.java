package fr.univpoitiers.core.grid;

import java.util.ArrayList;

public class Grid {

	private final Cell[] cells;
	private final int WIDTH;
	private final int HEIGHT;
	private final ArrayList<Integer> availableCells;

	/**
	 * 
	 * @param width
	 * @param height
	 */
	public Grid(int width, int height) {
		if (width < 0 || height < 0 || (width == 0) != (height == 0)) {
			throw new IllegalArgumentException("Les dimensions doivent être positives ou toutes deux nulles.");
		}
		this.WIDTH = width;
		this.HEIGHT = height;
		this.cells = new Cell[width * height];
		this.availableCells = new ArrayList<>(cells.length);
		for (int position = 0; position < cells.length; position++) {
			cells[position] = new Cell();
			availableCells.add(position);
		}
	}

	/**
	 * 
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 */
	public boolean checkValidValues(int x0, int y0, int x1, int y1) {
		// TODO - implement Grid.checkValidValues
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param value
	 */
	public boolean addNumber(int x, int y, int value) {
		if (value < 0 || value > Math.max(WIDTH, HEIGHT)) {
			throw new IllegalArgumentException("La valeur est hors limites pour cette grille.");
		}
		return getCell(x, y).setValue(value);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public int getNumber(int x, int y) {
		return getCell(x, y).getValue();
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
		getCell(x, y).setValue(0);
	}

	public void lockFilledCells() {
		for (Cell cell : cells) {
			if (cell.getValue() != 0) {
				cell.lock();
			}
		}
	}

	public void unlockCells() {
		for (Cell cell : cells) {
			cell.unlock();
		}
	}

	public void clearEntries() {
		// TODO - implement Grid.clearEntries
		throw new UnsupportedOperationException();
	}

	public void resetGrid() {
		availableCells.clear();
		for (int position = 0; position < cells.length; position++) {
			cells[position].reset();
			availableCells.add(position);
		}
	}

	public boolean isCellLocked(int x, int y) {
		return getCell(x, y).isLocked();
	}

	private Cell getCell(int x, int y) {
		if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
			throw new IllegalArgumentException("Les coordonnées ne correspondent pas à une cellule.");
		}
		return cells[y * WIDTH + x];
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

}