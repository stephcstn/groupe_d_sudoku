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
		// TODO - implement Cell.setNote
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param value
	 */
	public void removeNote(int value) {
		// TODO - implement Cell.removeNote
		throw new UnsupportedOperationException();
	}

	public boolean isLocked() {
		return this.locked;
	}

	public void lock() {
		// TODO - implement Cell.lock
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