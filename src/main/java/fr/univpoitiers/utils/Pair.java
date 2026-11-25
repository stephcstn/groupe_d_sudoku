package fr.univpoitiers.utils;

/**
 * Simple mutable container holding two values of possibly different types.
 *
 * @param <T1> type of the first item
 * @param <T2> type of the second item
 */
public class Pair<T1, T2> {

	private T1 item1;
	private T2 item2;

	/**
	 * Returns the first item.
	 *
	 * @return the first item, or {@code null} if it has not been set
	 */
	public T1 getItem1() {
		return this.item1;
	}

	/**
	 * Sets the first item.
	 *
	 * @param item1 the new first item
	 */
	public Pair<T1, T2> setItem1(T1 item1) {
		this.item1 = item1;
		return this;
	}

	/**
	 * Returns the second item.
	 *
	 * @return the second item, or {@code null} if it has not been set
	 */
	public T2 getItem2() {
		return this.item2;
	}

	/**
	 * Sets the second item.
	 *
	 * @param item2 the new second item
	 */
	public Pair<T1, T2> setItem2(T2 item2) {
		this.item2 = item2;
		return this;
	}

}