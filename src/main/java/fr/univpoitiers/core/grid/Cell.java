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