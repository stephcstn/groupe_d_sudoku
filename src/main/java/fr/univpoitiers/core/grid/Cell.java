package fr.univpoitiers.core.grid;

public class Cell {

	private byte[] note = new byte[10];
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
	}

	public void unlock() {
		this.locked = false;
	}

	public int getValue() {
		return this.value;
	}

	/**
	 * 
	 * @param value
	 */
	public boolean setValue(int value) {
		if (this.locked) {
			return false;
		}
		this.value = value;
		return true;
	}

	public void reset() {
		this.value = 0;
		this.note = new byte[10];
		this.locked = false;
	}

}