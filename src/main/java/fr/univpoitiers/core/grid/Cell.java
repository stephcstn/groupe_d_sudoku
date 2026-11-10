package fr.univpoitiers.core.grid;

public class Cell {

	private final byte[] note;
	private boolean locked;
	private int value;

    public Cell(int nbVariables) {
        this.note = new byte[nbVariables];
        this.locked = false;
        this.value = 0;
    }

    public Cell(Cell other) {
        this.note = new byte[other.note.length];
        System.arraycopy(other.note, 0, this.note, 0, other.note.length);
        this.locked = other.locked;
        this.value = other.value;
    }

	/**
	 * 
	 * @param value
	 */
	public void setNote(int value) {
		this.note[value] = 1;
	}

    public int count(){
        int count = 0;
        for (int i = 0; i < this.note.length; i++) {
            if (this.note[i] == 1) {
                count++;
            }
        }
        return count;
    }

    /**
     *
     * @param value
     */
    public int getNote(int value) {
        return this.note[value];
    }

	/**
	 * 
	 * @param value
	 */
	public void removeNote(int value) {
		this.note[value] = 0;
	}

	public boolean isLocked() {
		return this.locked;
	}

	public void lock() {
		this.locked = true;
	}

	public int getValue() {
		return this.value;
	}

	/**
	 * 
	 * @param value
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