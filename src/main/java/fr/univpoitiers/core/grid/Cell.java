package fr.univpoitiers.core.grid;


/**
 * Represents an individual cell within the Sudoku grid, holding its value,
 * candidate notes, and locked status.
 */
public class Cell {

	private byte[] note;
	private boolean locked;
	private int value;

    /**
     * Constructs a new Cell with a specified number of candidate note variables.
     *
     * @param nbVariables the maximum number of potential candidate values (e.g., 9 for a standard Sudoku)
     */
    public Cell(int nbVariables) {
        this.note = new byte[nbVariables];
        this.locked = false;
        this.value = 0;
    }

    /**
     * Constructs a new Cell by performing a deep copy of another Cell instance.
     *
     * @param other the Cell instance to copy
     */
    public Cell(Cell other) {
        this.note = new byte[other.note.length];
        System.arraycopy(other.note, 0, this.note, 0, other.note.length);
        this.locked = other.locked;
        this.value = other.value;
    }

    /**
     * Sets a candidate note value as active (1) for this cell.
     *
     * @param value the index/value of the candidate note to enable
     */
    public void setNote(int value) {
        this.note[value] = 1;
    }

    /**
     * Counts the total number of active candidate notes set for this cell.
     *
     * @return the number of active notes
     */
    public int count() {
        int count = 0;
        for (int i = 0; i < this.note.length; i++) {
            if (this.note[i] == 1) {
                count++;
            }
        }
        return count;
    }

    /**
     * Retrieves the status of a specific candidate note for this cell.
     *
     * @param value the candidate note index to query
     * @return 1 if the note is active, 0 otherwise
     */
    public int getNote(int value) {
        return this.note[value];
    }

    /**
     * Removes a candidate note value (resets to 0) for this cell.
     *
     * @param value the index/value of the candidate note to disable
     */
    public void removeNote(int value) {
        this.note[value] = 0;
    }

    /**
     * Checks whether this cell is locked (initial clue/given value).
     *
     * @return true if the cell is locked, false otherwise
     */
    public boolean isLocked() {
        return this.locked;
    }

    /**
     * Locks this cell to prevent its value from being modified or cleared.
     */
    public void lock() {
        this.locked = true;
    }

    /**
     * Gets the current numeric value of this cell.
     *
     * @return the value of the cell (0 if empty)
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Sets the numeric value of this cell.
     *
     * @param value the numeric value to set
     */
    public void setValue(int value) {
        this.value = value;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cell other = (Cell) obj;
        if ((this.value != other.value) || (this.locked != other.locked) || (this.note == null || other.note == null)) return false;
        for (int i = 0; i < this.note.length; i++) {
            if (this.note[i] != other.note[i]) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(this.value);
        result = 31 * result + java.util.Arrays.hashCode(this.note);
        return result;
    }

}