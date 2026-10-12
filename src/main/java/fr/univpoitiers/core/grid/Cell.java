package fr.univpoitiers.core.grid;

public class Cell {

	private byte[] note;
	private boolean locked;
	private int value;

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

}