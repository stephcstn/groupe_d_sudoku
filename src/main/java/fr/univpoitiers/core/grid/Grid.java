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
		this.WIDTH = width;
		this.HEIGHT = height;
		this.cells = new Cell[width*height];
		this.availableCells = new ArrayList<>();
	}

	/**
	 * 
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 */
	public boolean checkValidValues(int x0, int y0, int x1, int y1) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param value
	 */
	public boolean addNumber(int x, int y, int value) {
        this.cells[y * this.WIDTH + x].setValue(value);
        this.availableCells.remove(Integer.valueOf(y * this.WIDTH + x));
        return true;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public int getNumber(int x, int y) {
		return this.cells[y * this.WIDTH + x].getValue();
	}

	public int nextAvailableCell() {
		return this.availableCells.getFirst();
	}

	/**
	 * 
	 * @param pos
	 */
	public void backCellAvailable(int pos) {
		this.availableCells.add(pos);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param note
	 */
	public boolean isCellContaining(int x, int y, int note) {
        return this.cells[y * this.WIDTH + x].getNote(note) == 1;
    }

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void removeNumber(int x, int y) {
		this.cells[y * this.WIDTH + x].setValue(); // a clarifier avec thomas car 0 est la valeur par défaut pour une cellule vide
	}

	public void lockFilledCells() {
        for (int i = 0; i < this.cells.length; i++) {  // Parcours de toutes les cellules du tableau
            if (this.cells[i].getValue() != 0) {
                this.cells[i].lock();                  // Si la cellule n'est pas vide, on la verrouille
            }
        }
	}

	public void clearEntries() {
        for (int i = 0; i < this.cells.length; i++) {  // Parcours de toutes les cellules du tableau
            if (!this.cells[i].isLocked()) {
                this.cells[i].setValue(0);             // Si la cellule n'est pas verrouillée, on la vide
            }
        }
	}

	public void resetGrid() {
        for (int i = 0; i < this.cells.length; i++) {  // Parcours de toutes les cellules du tableau
            this.cells[i].setValue(0);                 // On vide toutes les cellules}
        }
    }

	/**
	 * 
	 * @param x
	 */
	public void lineUpdate(int pos, int valeur) {

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