package fr.univpoitiers.core.grid;

import java.util.ArrayList;

public class Grid {

	private Cell[] cells;
	private final int width;
	private final int height;
	private ArrayList<Integer> availableCells;
    private final int blockWidth;
    private final int blockHeight;

	/**
	 *
	 * @param width
	 * @param height
	 */
	public Grid(int width, int height) {
        int max = Math.max(width, height);
		this.width = width;
		this.height = height;
		this.cells = new Cell[width*height];
		this.availableCells = new ArrayList<>();
		this.blockWidth = (int) Math.sqrt(width);
		this.blockHeight = (int) Math.sqrt(height);
        for (int i = 0; i < this.cells.length; i++) {
            this.cells[i] = new Cell(max);
            this.availableCells.add(i); // a modifier
        }
	}

    public Grid(Grid other) {
        this.width = other.width;
        this.height = other.height;
        this.cells = new Cell[other.cells.length];
        this.availableCells = new ArrayList<>(other.availableCells);
        this.blockWidth = other.blockWidth;
        this.blockHeight = other.blockHeight;
        for (int i = 0; i < other.cells.length; i++) {
            this.cells[i] = new Cell(other.cells[i]);
        }
    }

    public int getRowIndex(int pos) {
        return pos / this.width;
    }

    public int getColumnIndex(int pos) {
        return pos % this.width;
    }

    public int getBlockIndex(int pos) {
        int x = getColumnIndex(pos);
        int y = getRowIndex(pos);
        int startX = x - (x % this.blockWidth);
        int startY = y - (y % this.blockHeight);
        return startY * this.width + startX;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
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
	 * @param pos
	 * @param value
	 */
	public boolean addNumber(int pos, int value) {
        this.cells[pos].setValue(value);
        this.availableCells.remove(Integer.valueOf(pos));
        return true;
	}

	/**
	 *
	 * @param pos
	 */
	public int getNumber(int pos) {
		return this.cells[pos].getValue();
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
	 * @param pos
	 * @param note
	 */
	public boolean isCellContaining(int pos, int note) {
        return this.cells[pos].getNote(note) == 1;
    }

	/**
	 *
	 * @param pos
	 */
	public void removeNumber(int pos) {
		this.cells[pos].setValue(0);
        this.availableCells.add(pos);
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
        for (int i = x * this.width; i < (x + 1) * this.width; i++) {
            this.cells[i].removeNote(valeur);
        }
	}

	/**
	 * 
	 * @param y
     * @param valeur
	 */
	public void columnUpdate(int y, int valeur) {
        for (int i = y; i < this.cells.length; i+=this.width) {
                this.cells[i].removeNote(valeur);
        }
	}

	/**
	 *
	 * @param pos
	 * @param valeur
	 */
	public void blockUpdate(int pos, int valeur) {
        int x = getColumnIndex(pos);
        int y = getRowIndex(pos);

        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            throw new IllegalArgumentException("x and y must be within the grid bounds.");
        }

        int startBlock = getBlockIndex(pos);

        for (int i = startBlock; i < startBlock + this.blockHeight * this.width; i += this.width) {
            for (int j = startBlock; j < startBlock + this.blockWidth; j++) {
                this.cells[i * this.width + j].removeNote(valeur);
            }
        }
	}

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;

        Grid otherGrid = (Grid) obj;

        if (this.width != otherGrid.width || this.height != otherGrid.height) return false;

        for (int i = 0; i < this.cells.length; i++) {
            if (this.cells[i].getValue() != otherGrid.cells[i].getValue()) {
                return false;
            }
            for (int j = 0; j < Math.max(this.width, this.height); j++) {
                if (this.cells[i].getNote(j) != otherGrid.cells[i].getNote(j)) {
                    return false;
                }
            }
        }

        return true;
    }

	/**
	 *
	 * @param pos
	 */
	public void diagonalUpdate(int pos) {

	}

}