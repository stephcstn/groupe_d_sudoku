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

    @Nested
    @DisplayName("annotate: the grid state once executed in filled of notes")
    class Annotate {
        @Test
        @DisplayName("The grid is already full")
        void annotate_gridAlreadyFull() {
            solver.annotate(grid);
            for (int i = 0; i < grid.getWidth() * (grid.getWidth() - 1); i++) {
                for (int j = i; j < j + grid.getWidth(); j++) {
                    Assertions.assertTrue(grid.getNotes(j).isEmpty());
                }
            }
        }
        @Test
        @DisplayName("A row is empty")
        void annotate_emptyRow() {
            for (int i = 0; i < grid.getWidth(); i++) {
                grid.removeNumber(i);
            }
            solver.annotate(grid);

            for (int i = 0; i < grid.getWidth(); i++) {
                Assertions.assertTrue(grid.getNotes(i).size() == 1);
            }
            Assertions.assertTrue(grid.isCellContaining(0, 4));
            Assertions.assertTrue(grid.isCellContaining(1, 9));
            Assertions.assertTrue(grid.isCellContaining(2, 6));
            Assertions.assertTrue(grid.isCellContaining(3, 8));
            Assertions.assertTrue(grid.isCellContaining(4, 3));
            Assertions.assertTrue(grid.isCellContaining(5, 2));
            Assertions.assertTrue(grid.isCellContaining(6, 5));
            Assertions.assertTrue(grid.isCellContaining(7, 1));
            Assertions.assertTrue(grid.isCellContaining(8, 7));
        }

        @Test
        @DisplayName("A column is empty")
        void annotate_emptyColumn() {
            for (int i = 0; i <= grid.getWidth() * (grid.getWidth() - 1); i += grid.getWidth()) {
                grid.removeNumber(i);
            }
            solver.annotate(grid);

            for (int i = 0; i < grid.getWidth() * (grid.getWidth() - 1); i += grid.getWidth()) {
                Assertions.assertTrue(grid.getNotes(i).size() == 1);
            }
            Assertions.assertTrue(grid.isCellContaining(0, 4));
            Assertions.assertTrue(grid.isCellContaining(9, 8));
            Assertions.assertTrue(grid.isCellContaining(18, 2));
            Assertions.assertTrue(grid.isCellContaining(27, 6));
            Assertions.assertTrue(grid.isCellContaining(36, 7));
            Assertions.assertTrue(grid.isCellContaining(45, 9));
            Assertions.assertTrue( grid.isCellContaining(54, 5));
            Assertions.assertTrue( grid.isCellContaining(63, 3));
            Assertions.assertTrue(grid.isCellContaining(72, 1));
        }

        @Test
        @DisplayName("A block is empty")
        void annotate_emptyBlock() {
            for (int i = 0; i <= grid.getWidth() * 2; i += grid.getWidth()) {
                for (int j = i; j < i + 3; j++) {
                    grid.removeNumber(j);
                }
            }
            solver.annotate(grid);

            for (int i = 0; i <= grid.getWidth() * 2; i += grid.getWidth()) {
                for (int j = i; j < i + 3; j++) {
                    Assertions.assertTrue(grid.getNotes(i).size() == 1);
                }
            }
            Assertions.assertTrue(grid.isCellContaining(0, 4));
            Assertions.assertTrue(grid.isCellContaining(27, 9));
            Assertions.assertTrue(grid.isCellContaining(36, 6));
            Assertions.assertTrue(grid.isCellContaining(9, 8));
            Assertions.assertTrue(grid.isCellContaining(45, 1));
            Assertions.assertTrue(grid.isCellContaining(54, 3));
            Assertions.assertTrue(grid.isCellContaining(18, 2));
            Assertions.assertTrue(grid.isCellContaining(63, 7));
            Assertions.assertTrue(grid.isCellContaining(72, 5));
        }
    }
}
