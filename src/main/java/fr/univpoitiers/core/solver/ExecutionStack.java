package fr.univpoitiers.core.solver;

import fr.univpoitiers.core.grid.Grid;
import fr.univpoitiers.utils.Pair;

import java.util.ArrayList;

/**
 * History of the grid states produced while solving, each paired with the index of the
 * rule that led to it. Not implemented yet.
 */
public class ExecutionStack {

	private ArrayList<Pair<Grid, Bet>> stack;

	/**
	 * Creates an empty execution stack. Not implemented yet.
	 *
	 * @throws UnsupportedOperationException always, until the class is implemented
	 */
	public ExecutionStack() {
		stack = new ArrayList<>();
	}

	/**
	 * Pushes a grid state together with the rule that produced it. Not implemented yet.
	 *
	 * @param grid the grid state to save
	 * @param bet bet of choosing a value instead of others
	 * @throws UnsupportedOperationException always, until the method is implemented
	 */
	public ExecutionStack stack(Grid grid, Bet bet) {
		stack.add(new Pair<Grid, Bet>()
				.setItem1(grid)
				.setItem2(bet)
		);
		return this;
	}

	/**
	 * Removes and returns the last saved state.
	 * @return the last grid state and the rule to retrieve it
	 */
	public Pair<Grid, Bet> pop() {
		return stack.removeLast();
	}

	/**
	 * Returns if the current stack is empty
	 */
	public boolean isEmpty() { return stack.isEmpty(); }
}