package fr.univpoitiers.core.solver;

import fr.univpoitiers.core.grid.Grid;
import org.junit.jupiter.api.*;

public class SolverTest {
    private Grid grid;
    private Solver solver;

    private void populateGrid(Grid targetGrid, int[] values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] != 0) {
                targetGrid.addNumber(i, values[i]);
            }
        }
    }

    @BeforeEach
    void setUp() {
        grid = new Grid(9, 9);
        solver = new Solver(null);

        int[] initialGrid = {
                4, 9, 6, 8, 3, 2, 5, 1, 7,
                8, 1, 3, 6, 5, 7, 4, 2, 9,
                2, 7, 5, 4, 9, 1, 3, 8, 6,
                6, 4, 2, 5, 8, 9, 7, 3, 1,
                7, 3, 8, 1, 2, 6, 9, 5, 4,
                9, 5, 1, 7, 4, 3, 8, 6, 2,
                5, 6, 4, 2, 7, 8, 1, 9, 3,
                3, 2, 7, 9, 1, 5, 6, 4, 8,
                1, 8, 9, 3, 6, 4, 2, 7, 5,
        };
        populateGrid(grid, initialGrid);
    }
}
