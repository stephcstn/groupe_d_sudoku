package fr.univpoitiers.core;

import fr.univpoitiers.core.grid.Grid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GridTest {

    private Grid grid;

    /**
     * Helper method to populate a grid from a 1D array representation.
     * 0 represents an empty cell.
     */
    private void populateGrid(Grid targetGrid, int[] values) {
        int width = targetGrid.getWidth();
        for (int i = 0; i < values.length; i++) {
            if (values[i] != 0) {
                int x = i % width;
                int y = i / width;
                targetGrid.addNumber(x, y, values[i]);
            }
        }
    }

    @BeforeEach
    void setUp() {
        // Standard 9x9 reference grid (0 = empty cell)
        int[] initialGrid = {
                5, 3, 0,  0, 7, 0,  0, 0, 0,
                6, 0, 0,  1, 9, 5,  0, 0, 0,
                0, 9, 8,  0, 0, 0,  0, 6, 0,

                8, 0, 0,  0, 6, 0,  0, 0, 3,
                4, 0, 0,  8, 0, 3,  0, 0, 1,
                7, 0, 0,  0, 2, 0,  0, 0, 6,

                0, 6, 0,  0, 0, 0,  2, 8, 0,
                0, 0, 0,  4, 1, 9,  0, 0, 5,
                0, 0, 0,  0, 8, 0,  0, 7, 9
        };

        grid = new Grid(9, 9);
        populateGrid(grid, initialGrid);
    }

    // =========================================================================
    // Unit Tests: checkRow
    // =========================================================================

    @Test
    @DisplayName("checkRow: returns false when target value is already present in the row")
    void checkRow_whenValueAlreadyExistsInRow_shouldReturnFalse() {
        // Target cell index 2 corresponds to (x=2, y=0)
        // Values 5, 3, and 7 are already present on row 0
        assertFalse(grid.checkRow(2, 5));
        assertFalse(grid.checkRow(2, 3));
        assertFalse(grid.checkRow(2, 7));
    }

    @Test
    @DisplayName("checkRow: returns true when target value does not exist in the row")
    void checkRow_whenValueDoesNotExistInRow_shouldReturnTrue() {
        // Value 1 is absent from row 0
        assertTrue(grid.checkRow(2, 1));
    }

    // =========================================================================
    // Unit Tests: checkColumn
    // =========================================================================

    @Test
    @DisplayName("checkColumn: returns false when target value is already present in the column")
    void checkColumn_whenValueAlreadyExistsInColumn_shouldReturnFalse() {
        // Target index 2 is on column 2 (x=2)
        // Value 8 is already present at pos = 20 (x=2, y=2)
        assertFalse(grid.checkColumn(2, 8));
    }

    @Test
    @DisplayName("checkColumn: returns true when target value does not exist in the column")
    void checkColumn_whenValueDoesNotExistInColumn_shouldReturnTrue() {
        // Value 4 does not exist anywhere in column 2
        assertTrue(grid.checkColumn(2, 4));
    }

    // =========================================================================
    // Unit Tests: checkBlock
    // =========================================================================

    @Test
    @DisplayName("checkBlock: returns false when target value is already present in the 3x3 block")
    void checkBlock_whenValueAlreadyExistsInBlock_shouldReturnFalse() {
        // Target index 2 belongs to the top-left 3x3 block
        // Value 6 is present at index 9 (x=0, y=1)
        assertFalse(grid.checkBlock(2, 6));
    }

    @Test
    @DisplayName("checkBlock: returns true when target value exists only in an adjacent block")
    void checkBlock_whenValueExistsInAdjacentBlock_shouldReturnTrue() {
        // Value 7 exists on row 0 at index 4 (center-top block), but not in block 0
        assertTrue(grid.checkBlock(2, 7));
    }

    // =========================================================================
    // Integration Tests: checkValidValues (Composite Rule)
    // =========================================================================

    @Test
    @DisplayName("checkValidValues: returns false if row constraint is violated")
    void checkValidValues_whenRowConstraintViolated_shouldReturnFalse() {
        // Target index 2: value 7 is valid for column and block, but invalid for row
        assertFalse(grid.checkValidValues(2, 7));
    }

    @Test
    @DisplayName("checkValidValues: returns false if column constraint is violated")
    void checkValidValues_whenColumnConstraintViolated_shouldReturnFalse() {
        // Target index 2: value 8 is valid for row, but invalid for column (and block)
        assertFalse(grid.checkValidValues(2, 8));
    }

    @Test
    @DisplayName("checkValidValues: returns true when row, column, and block constraints are satisfied")
    void checkValidValues_whenAllConstraintsSatisfied_shouldReturnTrue() {
        // Target index 2: value 1 is absent from row 0, column 2, and block 0
        assertTrue(grid.checkValidValues(2, 1));
    }

    // =========================================================================
    // State Tracking Tests: availableCells Management
    // =========================================================================

    @Test
    @DisplayName("addNumber: updates cell value and removes index from availableCells")
    void addNumber_shouldUpdateValueAndRemoveFromAvailableCells() {
        Grid emptyGrid = new Grid(9, 9);
        assertEquals(0, emptyGrid.nextAvailableCell());

        emptyGrid.addNumber(0, 0, 4);

        assertEquals(4, emptyGrid.getNumber(0, 0));
        assertEquals(1, emptyGrid.nextAvailableCell());
    }
}