package fr.univpoitiers.core.solver;

import fr.univpoitiers.core.grid.Grid;
import fr.univpoitiers.utils.Pair;

import java.util.ArrayList;

public class ExecutionStack {

	private ArrayList<Pair<Grid, Integer>> stack;

	public ExecutionStack() {
		// TODO - implement ExecutionStack.ExecutionStack
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param grid
	 * @param rule
	 */
	public void stack(Grid grid, Integer rule) {

	}

	/**
	 * /!\ TODO : à réécrire
	 * @return
	 */
	public Pair<Grid, Integer> pop() {
		Pair<Grid, Integer> p = new Pair<Grid, Integer>();
		p.setItem1(new Grid(0, 0));
		p.setItem2(0);
		return p;
	}

}