package fr.univpoitiers.core.grid;

import java.util.ArrayList;

public class Grid {

	private Cell[] cells;
	private final int WIDTH;
	private final int HEIGHT;
	private ArrayList<Integer> availableCells;
    private final int BLOCK_WIDTH;
    private final int BLOCK_HEIGHT;

	/**
	 *
	 * @param width
	 * @param height
	 */
	public Grid(int width, int height) {
        int max = Math.max(width, height);
		this.WIDTH = width;
		this.HEIGHT = height;
		this.cells = new Cell[width*height];
		this.availableCells = new ArrayList<>();
		this.BLOCK_WIDTH = (int) Math.sqrt(width);
		this.BLOCK_HEIGHT = (int) Math.sqrt(height);
        for (int i = 0; i < this.cells.length; i++) {
            this.cells[i] = new Cell(max);
            this.availableCells.add(i); // a modifier
        }
	}

    public Grid(Grid other) {
        this.WIDTH = other.WIDTH;
        this.HEIGHT = other.HEIGHT;
        this.cells = new Cell[other.cells.length];
        this.availableCells = new ArrayList<>(other.availableCells);
        this.BLOCK_WIDTH = other.BLOCK_WIDTH;
        this.BLOCK_HEIGHT = other.BLOCK_HEIGHT;
        for (int i = 0; i < other.cells.length; i++) {
            this.cells[i] = new Cell(other.cells[i]);
        }
    }

    public int getRowIndex(int pos) {
        return pos / this.WIDTH;
    }

    public int getColumnIndex(int pos) {
        return pos % this.WIDTH;
    }

    public int getBlockIndex(int pos) {
        int blockWidth = (int) Math.sqrt(this.WIDTH);
        int blockHeight = (int) Math.sqrt(this.HEIGHT);
        int x = getColumnIndex(pos);
        int y = getRowIndex(pos);
        int startX = x - (x % blockWidth);
        int startY = y - (y % blockHeight);
        return startY * this.WIDTH + startX;
    }

    public int getWidth() {
        return this.WIDTH;
    }

    public int getHeight() {
        return this.HEIGHT;
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
        for (int i = x * this.WIDTH; i < (x + 1) * this.WIDTH; i++) {
            this.cells[i].removeNote(valeur);
        }
	}

	/**
	 * 
	 * @param y
     * @param valeur
	 */
	public void columnUpdate(int y, int valeur) {
        for (int i = y; i < this.cells.length; i+=this.WIDTH) {
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

        if (x < 0 || x >= this.WIDTH || y < 0 || y >= this.HEIGHT) {
            throw new IllegalArgumentException("x and y must be within the grid bounds.");
        }

        int startBlock = getBlockIndex(pos);

        int blockWidth = (int) Math.sqrt(this.WIDTH);
        int blockHeight = (int) Math.sqrt(this.HEIGHT);

        for (int i = startBlock; i < startBlock + blockHeight * this.WIDTH; i += this.WIDTH) {
            for (int j = startBlock; j < startBlock + blockWidth; j++) {
                this.cells[i * this.WIDTH + j].removeNote(valeur);
            }
        }
	}

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;

        Grid otherGrid = (Grid) obj;

        if (this.WIDTH != otherGrid.WIDTH || this.HEIGHT != otherGrid.HEIGHT) return false;

        for (int i = 0; i < this.cells.length; i++) {
            if (this.cells[i].getValue() != otherGrid.cells[i].getValue()) {
                return false;
            }
            for (int j = 0; j < Math.max(this.WIDTH, this.HEIGHT); j++) {
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
