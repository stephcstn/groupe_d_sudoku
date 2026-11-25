package fr.univpoitiers.core.solver;

import java.util.ArrayList;

public class Bet {
    private ArrayList<Integer> nextChoices;
    private int pos;
    private int current;

    public int getCurrent() {
        return current;
    }

    public Bet setCurrent(int current) {
        this.current = current;
        return this;
    }

    public ArrayList<Integer> getNextChoices() {
        return nextChoices;
    }

    public Bet setNextChoices(ArrayList<Integer> nextChoices) {
        this.nextChoices = nextChoices;
        return this;
    }

    public int getPos() {
        return pos;
    }

    public Bet setPos(int pos) {
        this.pos = pos;
        return this;
    }
}
