package fr.univpoitiers.core.grid;

import java.util.ArrayList;

public class Grid {

	private ArrayList<Cell> cells;
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

}