package fr.univpoitiers.acceptance;

import fr.univpoitiers.core.grid.Grid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Acceptance (validation) tests for backlog scenarios related to constraint verification:
 * T03, T04 (US02) and T12 (US06).
 *
 * The interface does not yet allow entering digits: these scenarios are therefore executed
 * at the model level, which enforces the business rules.
 *
 * Backlog coordinates (row, column starting from 1) are converted into positions.
 */
class ConstraintAcceptanceTest {

    private Grid grid;

    /** Initial 9x9 grid layout (0 represents an empty cell). */
    private static final int[] INITIAL = {
            5, 3, 0, 0, 7, 0, 0, 0, 0,
            6, 0, 0, 1, 9, 5, 0, 0, 0,
            0, 9, 8, 0, 0, 0, 0, 6, 0,
            8, 0, 0, 0, 6, 0, 0, 0, 3,
            4, 0, 0, 8, 0, 3, 0, 0, 1,
            7, 0, 0, 0, 2, 0, 0, 0, 6,
            0, 6, 0, 0, 0, 0, 2, 8, 0,
            0, 0, 0, 4, 1, 9, 0, 0, 5,
            0, 0, 0, 0, 8, 0, 0, 7, 9
    };

    @BeforeEach
    void setUp() {
        grid = new Grid(9, 9);
        for (int i = 0; i < INITIAL.length; i++) {
            if (INITIAL[i] != 0) {
                grid.addNumber(i, INITIAL[i]);
            }
        }
    }

    /**
     * Calculates the cell position from 1-based (row, column) coordinates.
     */
    private static int pos(int row, int col) {
        return (row - 1) * 9 + (col - 1);
    }

    /**
     * Player input helper: places the value and returns whether it complies with the rules.
     */
    private boolean input(int row, int col, int value) {
        int p = pos(row, col);
        boolean valid = grid.checkValidValues(p, value);
        grid.addNumber(p, value);
        return valid;
    }

    /**
     * Verification ("Check" button): retrieves positions of player entries that cause conflicts.
     */
    private List<Integer> check(List<Integer> playerEntries) {
        List<Integer> invalid = new ArrayList<>();
        for (int p : playerEntries) {
            if (!grid.checkValidValues(p, grid.getNumber(p))) {
                invalid.add(p);
            }
        }
        return invalid;
    }

    @Test
    @DisplayName("T03 (US02): a duplicated digit on the same row and block is detected")
    void t03_duplicatedNumberIsDetected() {
        // Empty grid to reproduce the exact scenario: 4 at (1,1) then 4 at (1,2)
        grid = new Grid(9, 9);

        assertTrue(input(1, 1, 4), "first 4: no alert expected");
        assertFalse(input(1, 2, 4), "second 4: alert expected (same row and same block)");
        assertFalse(grid.checkRow(pos(1, 2), 4));
        assertFalse(grid.checkBlock(pos(1, 2), 4));
    }

    @Test
    @DisplayName("T04 (US02): an unauthorized value is rejected")
    void t04_unauthorizedValueIsRejected() {
        int p = pos(1, 3); // empty cell
        // Letter inputs are filtered by the UI; the model rejects any value outside the 1..9 range.
        assertFalse(grid.checkValidValues(p, 0));
        assertFalse(grid.checkValidValues(p, 10));
        assertFalse(grid.checkValidValues(p, -4));
        assertEquals(0, grid.getNumber(p), "the cell remains empty");
    }

    @Test
    @DisplayName("T12 (US06): verification highlights only invalid player entries")
    void t12_checkHighlightsOnlyInvalidPlayerEntries() {
        List<Integer> entries = new ArrayList<>();

        // Valid inputs
        input(1, 3, 4);  entries.add(pos(1, 3));
        input(2, 2, 7);  entries.add(pos(2, 2));
        // Invalid input: a 5 is already present at (1,1) on row 1
        input(1, 4, 5);  entries.add(pos(1, 4));

        List<Integer> invalid = check(entries);

        assertEquals(List.of(pos(1, 4)), invalid, "only cell (1,4) should be flagged");
        // Verification does not block the rest of the board: other inputs remain in place
        assertEquals(4, grid.getNumber(pos(1, 3)));
        assertEquals(7, grid.getNumber(pos(2, 2)));
    }

    @Test
    @DisplayName("T12 (US06): no cells flagged when all player entries are valid")
    void t12_checkReportsNothingWhenEntriesAreValid() {
        List<Integer> entries = new ArrayList<>();
        input(1, 3, 4);  entries.add(pos(1, 3));
        input(2, 2, 7);  entries.add(pos(2, 2));

        assertTrue(check(entries).isEmpty());
    }
}