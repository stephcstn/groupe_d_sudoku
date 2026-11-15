package fr.univpoitiers.core.solver;

import fr.univpoitiers.core.grid.Grid;
import fr.univpoitiers.utils.Pair;

import java.util.ArrayList;

/**
 * History of the grid states produced while solving, each paired with the index of the
 * rule that led to it. Not implemented yet.
 */
public class ExecutionStack {

	private ArrayList<Pair<Grid, Integer>> stack;

	/**
	 * Creates an empty execution stack. Not implemented yet.
	 *
	 * @throws UnsupportedOperationException always, until the class is implemented
	 */
	public ExecutionStack() {
		// TODO - implement ExecutionStack.ExecutionStack
		throw new UnsupportedOperationException();
	}

	/**
	 * Pushes a grid state together with the rule that produced it. Not implemented yet.
	 *
	 * @param grid the grid state to save
	 * @param rule the index of the applied rule, or -1 for the initial annotation
	 * @throws UnsupportedOperationException always, until the method is implemented
	 */
	public void stack(Grid grid, Integer rule) {
		// TODO - implement ExecutionStack.stack
		throw new UnsupportedOperationException("stack n'est pas encore implémenté.");
	}

	/**
	 * Removes and returns the last saved state.
	 *
	 * <p>/!\ TODO: to be rewritten. The current version is a placeholder that always returns an
	 * empty 0x0 grid paired with rule 0, without reading the stack.</p>
	 *
	 * @return the last grid state and the rule that produced it
	 */
	public Pair<Grid, Integer> pop() {
		Pair<Grid, Integer> p = new Pair<Grid, Integer>();
		p.setItem1(new Grid(0, 0));
		p.setItem2(0);
		return p;
	}

}