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
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param value
	 */
	public void removeNote(int value) {
		this.note[value] = 0;
		throw new UnsupportedOperationException();
	}

	public boolean isLocked() {
		return this.locked;
	}

	public void lock() {
		this.locked = true;
		throw new UnsupportedOperationException();
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