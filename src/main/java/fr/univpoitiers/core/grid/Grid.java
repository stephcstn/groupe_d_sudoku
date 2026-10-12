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
		this.cells[y * this.WIDTH + x].setValue(0);
	}

	public void lockFilledCells() {
        for (Cell cell : this.cells) {
            if (cell.getValue() != 0) {
                cell.lock();
            }
        }
	}

	public void clearEntries() {
        for (Cell cell : this.cells) {
            if (!cell.isLocked()) {
                cell.setValue(0);
            }
        }
	}

	public void resetGrid() {
        for (Cell cell : this.cells) {
            cell.setValue(0);
        }
    }

	/**
	 * 
	 * @param x
     * @param valeur
	 */
	public void lineUpdate(int x, int valeur) {
        for (int i = 0; i < this.cells.length; i++) {
            if (i / this.WIDTH == x) {
                this.cells[i].removeNote(valeur);
            }
        }
	}

	/**
	 * 
	 * @param y
     * @param valeur
	 */
	public void columnUpdate(int y, int valeur) {
        for (int i = 0; i < this.cells.length; i++) {
            if (i % this.WIDTH == y) {
                this.cells[i].removeNote(valeur);
            }
        }
	}

    public int getBlockIndex(int pos) {
        int blockWidth = (int) Math.sqrt(this.WIDTH);
        int blockHeight = (int) Math.sqrt(this.HEIGHT);
        int x = pos % this.WIDTH;
        int y = pos / this.WIDTH;
        int startX = x - (x % blockWidth);
        int startY = y - (y % blockHeight);
        return startY * this.WIDTH + startX;
    }

	/**
	 *
	 * @param pos
	 * @param valeur
	 */
	public void blockUpdate(int pos, int valeur) {
        int x = pos % this.WIDTH;
        int y = pos / this.WIDTH;

        if (x < 0 || x >= this.WIDTH || y < 0 || y >= this.HEIGHT) {
            throw new IllegalArgumentException("x and y must be within the grid bounds.");
        }

        int blockWidth = (int) Math.sqrt(this.WIDTH);
        int blockHeight = (int) Math.sqrt(this.HEIGHT);
        int startX = x - (x % blockWidth);
        int startY = y - (y % blockHeight);

        for (int i = startY; i < startY + blockHeight; i++) {
            for (int j = startX; j < startX + blockWidth; j++) {
                this.cells[i * this.WIDTH + j].removeNote(valeur);
            }
        }
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