package fr.univpoitiers.core.grid;

public class Cell {

	private static final int DEFAULT_MAX_VALUE = 9;
	private final byte[] note;
	private boolean locked;
	private int value;

	public Cell() {
		this(DEFAULT_MAX_VALUE);
	}

	public Cell(int maxValue) {
		if (maxValue < 1) {
			throw new IllegalArgumentException("Le nombre de valeurs possibles doit être positif.");
		}
		this.note = new byte[maxValue + 1];
	}

	/**
	 * 
	 * @param value
	 */
	public void setNote(int value) {
		checkNoteValue(value);
		if (!locked && this.value == 0) {
			this.note[value] = 1;
		}
	}

	/**
	 * 
	 * @param value
	 */
	public void removeNote(int value) {
		checkNoteValue(value);
		if (!locked && this.value == 0) {
			this.note[value] = 0;
		}
	}

	public boolean hasNote(int value) {
		checkNoteValue(value);
		return this.note[value] == 1;
	}

	private void checkNoteValue(int value) {
		if (value < 1 || value >= note.length) {
			throw new IllegalArgumentException("Une note doit être comprise entre 1 et " + (note.length - 1) + ".");
		}
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
		if (value < 0 || value >= note.length) {
			throw new IllegalArgumentException("La valeur doit être comprise entre 0 et " + (note.length - 1) + ".");
		}
		if (locked) {
			return;
		}
		this.value = value;
		for (int i = 1; i < note.length; i++) {
			note[i] = 0;
		}
	}

	public void reset() {
		this.value = 0;
		this.locked = false;
		for (int i = 1; i < note.length; i++) {
			note[i] = 0;
		}
	}

}