package fr.univpoitiers.core.rules;

import fr.univpoitiers.core.grid.Grid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;

public class BasicRulesTest {
    private Grid grid;

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

    @Test
    @DisplayName("fillValues : the list in parameter has been reset with 1 to k numbers.")
    void fillValues_providingList1ToKNumbers() {
        ArrayList<Integer> list = new ArrayList<>();

        list.add(100);
        RuleApplier.fillValues(list, 15);
        Assertions.assertFalse(list.contains(100));

        for (int i = 1; i <= 15; i++) {
            Assertions.assertEquals((Integer) i, list.removeFirst());
        }
    }

    @Nested
    @DisplayName("lastFreeCell: 4 type of situations the rule can solve : block, row, column, and no criteria matching.")
    class LastFreeCell {
        @Test
        @DisplayName("Block, row and column the cell belongs contains 2 values unassigned.")
        void lastFreeCell_2inBlockRowColumn() {
            // Even if there are more than 1 unassigned values in the block,
            // column or row can satisfy the rule.
            grid.removeNumber(0);
            grid.removeNumber(7);
            grid.removeNumber(10);
            grid.removeNumber(72);
            grid.backCellAvailable(0);
            grid.backCellAvailable(7);
            grid.backCellAvailable(10);
            grid.backCellAvailable(72);

            Assertions.assertFalse(RuleApplier.lastFreeCell(grid));
        }

        @Test
        @DisplayName("One value still unassigned in the block")
        void lastFreeCell_lastValueBlock() {
            // Isolating situation from the row and the column
            grid.removeNumber(0);
            grid.removeNumber(5);
            grid.removeNumber(27);
            grid.backCellAvailable(0);
            grid.backCellAvailable(5);
            grid.backCellAvailable(27);

            Assertions.assertTrue(RuleApplier.lastFreeCell(grid));
            Assertions.assertEquals(4, grid.getNumber(0));
            Assertions.assertEquals(0, grid.getNumber(5));
        }

        @Test
        @DisplayName("One value still unassigned in row")
        void lastFreeCell_lastValueRow() {
            // Isolating situation from the block and the column
            grid.removeNumber(5);
            grid.removeNumber(13);
            grid.removeNumber(50);
            grid.backCellAvailable(5);
            grid.backCellAvailable(13);
            grid.backCellAvailable(50);

            Assertions.assertTrue(RuleApplier.lastFreeCell(grid));
        }

        @Test
        @DisplayName("One value still unassigned in column")
        void lastFreeCell_lastValueColumn() {
            // Isolating situation from the block and the row
            grid.removeNumber(28);
            grid.removeNumber(29);
            grid.backCellAvailable(28);
            grid.backCellAvailable(29);

            Assertions.assertTrue(RuleApplier.lastFreeCell(grid));
            Assertions.assertEquals(4, grid.getNumber(28));
        }
    }

    @Nested
    @DisplayName("lastRemainingCell : 3 types of situation valid in permutation")
    class LastRemainingCell {
        @Test
        @DisplayName("The cell unassigned is in a corner")
        void lastRemainingCell_inCorner() {
            grid.removeNumber(0);
            grid.removeNumber(2);
            grid.removeNumber(11);
            grid.removeNumber(18);
            grid.removeNumber(20);
            grid.backCellAvailable(0);
            grid.backCellAvailable(2);
            grid.backCellAvailable(11);
            grid.backCellAvailable(18);
            grid.backCellAvailable(20);

            Assertions.assertTrue(RuleApplier.lastRemainingCell(grid));
            Assertions.assertEquals(4, grid.getNumber(0));
        }

        @Test
        @DisplayName("The cell unassigned is at the edge of the block except corners")
        void lastRemainingCell_atEdge() {

        }

        @Test
        @DisplayName("The cell unassigned is at the middle of the block")
        void lastRemainingCell_atMiddle() {

        }
    }
}
