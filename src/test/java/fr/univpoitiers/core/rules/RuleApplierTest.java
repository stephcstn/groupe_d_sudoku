package fr.univpoitiers.core.rules;

import fr.univpoitiers.core.grid.Grid;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for deduction rules.
 *
 * Includes tests for KObvious, Pointing (Pair / Triple), Box/Line Reduction (Pair / Triple),
 * X-Wing, Y-Wing, Swordfish rules and First Naive.
 *
 * Each test starts from an empty 9x9 grid without any notes, then manually places
 * the necessary notes for the scenario.
 */
class RuleApplierTest {

    private Grid grid;

    @BeforeEach
    void setUp() {
        grid = new Grid(9, 9);
    }

    /** Converts (row, column) coordinates into a 1D index. */
    private static int pos(int row, int col) {
        return row * 9 + col;
    }

    /** Adds the given note to each specified cell provided as {row, column}. */
    private void addNote(int note, int[]... cells) {
        for (int[] rc : cells) {
            grid.setNote(pos(rc[0], rc[1]), note);
        }
    }

    /** Helper method checking if a cell contains a specific candidate note. */
    private boolean has(int row, int col, int note) {
        return grid.isCellContaining(pos(row, col), note);
    }

    private void populateGrid(Grid targetGrid, int[] values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] != 0) {
                targetGrid.addNumber(i, values[i]);
            }
        }
    }

    @Nested
    @DisplayName("annotate: the grid state once executed in filled of notes")
    class Annotate {
        @BeforeEach
        void setup() {
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
        @DisplayName("The grid is already full")
        void annotate_gridAlreadyFull() {
            RuleApplier.annotate(grid);
            for (int i = 0; i < grid.getWidth() * grid.getWidth(); i++) {
                Assertions.assertTrue(grid.getNotes(i).isEmpty());
            }
        }
        @Test
        @DisplayName("A row is empty")
        void annotate_emptyRow() {
            for (int i = 0; i < grid.getWidth(); i++) {
                grid.removeNumber(i);
            }
            RuleApplier.annotate(grid);

            for (int i = 0; i < grid.getWidth(); i++) {
                assertEquals(1, grid.getNotes(i).size());
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
            RuleApplier.annotate(grid);

            for (int i = 0; i < grid.getWidth() * (grid.getWidth() - 1); i += grid.getWidth()) {
                assertEquals(1, grid.getNotes(i).size());
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
            for (int i = 0; i <= grid.getWidth() * (grid.getBlockHeight() - 1); i += grid.getWidth()) {
                for (int j = i; j < i + grid.getBlockWidth(); j++) {
                    grid.removeNumber(j);

                }
            }
            RuleApplier.annotate(grid);

            for (int i = 0; i <= grid.getWidth() * (grid.getBlockHeight() - 1); i += grid.getWidth()) {
                for (int j = i; j < i + grid.getBlockWidth(); j++) {
                    assertEquals(1, grid.getNotes(j).size());
                }
            }
            Assertions.assertTrue(grid.isCellContaining(0, 4));
            Assertions.assertTrue(grid.isCellContaining(1, 9));
            Assertions.assertTrue(grid.isCellContaining(2, 6));
            Assertions.assertTrue(grid.isCellContaining(9, 8));
            Assertions.assertTrue(grid.isCellContaining(10, 1));
            Assertions.assertTrue(grid.isCellContaining(11, 3));
            Assertions.assertTrue(grid.isCellContaining(18, 2));
            Assertions.assertTrue(grid.isCellContaining(19, 7));
            Assertions.assertTrue(grid.isCellContaining(20, 5));
        }
    }

    @Nested
    @DisplayName("KObvious")
    class KObviousTest{

    }

    @Nested
    @DisplayName("Pointing Pair / Triple")
    class PointingTests {

        @Test
        @DisplayName("Pair on a row: removes note from the rest of the row outside the block")
        void pointingPair_onRow_removesNoteOutsideBlock() {
            int note = 4;
            // Block 0: note is present only in (0,0) and (0,1), aligned on row 0
            addNote(note, new int[]{0, 0}, new int[]{0, 1});
            // Other occurrences: on row 0 (to be removed) and on row 1 (to be kept)
            addNote(note, new int[]{0, 5}, new int[]{0, 8}, new int[]{1, 5});

            assertTrue(RuleApplier.PointingPair(grid));

            assertTrue(has(0, 0, note));
            assertTrue(has(0, 1, note));
            assertFalse(has(0, 5, note));
            assertFalse(has(0, 8, note));
            assertTrue(has(1, 5, note), "a cell outside the row should not be modified");
        }

        @Test
        @DisplayName("Pair on a column: removes note from the rest of the column outside the block")
        void pointingPair_onColumn_removesNoteOutsideBlock() {
            int note = 2;
            // Block 0: note is present only in (0,0) and (2,0), aligned on column 0
            addNote(note, new int[]{0, 0}, new int[]{2, 0});
            addNote(note, new int[]{5, 0}, new int[]{8, 0}, new int[]{5, 1});

            assertTrue(RuleApplier.PointingPair(grid));

            assertTrue(has(0, 0, note));
            assertTrue(has(2, 0, note));
            assertFalse(has(5, 0, note));
            assertFalse(has(8, 0, note));
            assertTrue(has(5, 1, note), "a cell outside the column should not be modified");
        }

        @Test
        @DisplayName("Pair: returns false if the 2 cells in the block are not aligned")
        void pointingPair_whenNotAligned_returnsFalse() {
            int note = 4;
            addNote(note, new int[]{0, 0}, new int[]{1, 1}, new int[]{0, 5});
            Grid before = new Grid(grid);

            assertFalse(RuleApplier.PointingPair(grid));
            assertEquals(before, grid);
        }

        @Test
        @DisplayName("Pair: returns false when there is nothing to remove outside the block")
        void pointingPair_whenNothingToRemove_returnsFalse() {
            addNote(4, new int[]{0, 0}, new int[]{0, 1});
            Grid before = new Grid(grid);

            assertFalse(RuleApplier.PointingPair(grid));
            assertEquals(before, grid);
        }

        @Test
        @DisplayName("Pair: ignores already filled cells")
        void pointingPair_ignoresFilledCells() {
            int note = 4;
            addNote(note, new int[]{0, 0}, new int[]{0, 1}, new int[]{0, 5});
            // Cell (0,5) is filled: its note should not be modified
            grid.addNumber(pos(0, 5), 7);

            assertFalse(RuleApplier.PointingPair(grid));
            assertTrue(has(0, 5, note));
        }

        @Test
        @DisplayName("Triple: only PointingTriple applies to 3 aligned cells")
        void pointingTriple_onRow_removesNoteOutsideBlock() {
            int note = 6;
            addNote(note, new int[]{0, 0}, new int[]{0, 1}, new int[]{0, 2});
            addNote(note, new int[]{0, 5});

            assertFalse(RuleApplier.PointingPair(grid), "Pair should not process 3 cells");
            assertTrue(RuleApplier.PointingTriple(grid));

            assertTrue(has(0, 0, note));
            assertTrue(has(0, 1, note));
            assertTrue(has(0, 2, note));
            assertFalse(has(0, 5, note));
        }
    }

    @Nested
    @DisplayName("Box/Line Reduction Pair / Triple")
    class BoxLineReductionTests {

        @Test
        @DisplayName("Pair on a row: removes note from the rest of the block")
        void boxLinePair_onRow_removesNoteInRestOfBlock() {
            int note = 2;
            // Row 4: note is present only in (4,3) and (4,4), both in the central block
            addNote(note, new int[]{4, 3}, new int[]{4, 4});
            // Other cells in the central block (to be removed)
            addNote(note, new int[]{3, 3}, new int[]{5, 5});

            assertTrue(RuleApplier.boxLineReductionPair(grid));

            assertTrue(has(4, 3, note));
            assertTrue(has(4, 4, note));
            assertFalse(has(3, 3, note));
            assertFalse(has(5, 5, note));
        }

        @Test
        @DisplayName("Pair on a column: removes note from the rest of the block")
        void boxLinePair_onColumn_removesNoteInRestOfBlock() {
            int note = 6;
            // Column 4: note is present only in (3,4) and (5,4), both in the central block
            addNote(note, new int[]{3, 4}, new int[]{5, 4});
            // Other cells in central block (to be removed) + one cell outside block (to be kept)
            addNote(note, new int[]{4, 3}, new int[]{4, 5}, new int[]{4, 0});

            assertTrue(RuleApplier.boxLineReductionPair(grid));

            assertTrue(has(3, 4, note));
            assertTrue(has(5, 4, note));
            assertFalse(has(4, 3, note));
            assertFalse(has(4, 5, note));
            assertTrue(has(4, 0, note), "a cell outside the block should not be modified");
        }

        @Test
        @DisplayName("Pair: returns false if the 2 cells on the row are in different blocks")
        void boxLinePair_whenDifferentBlocks_returnsFalse() {
            addNote(1, new int[]{0, 0}, new int[]{0, 5}, new int[]{1, 1});
            Grid before = new Grid(grid);

            assertFalse(RuleApplier.boxLineReductionPair(grid));
            assertEquals(before, grid);
        }

        @Test
        @DisplayName("Pair: returns false when there is nothing to remove in the block")
        void boxLinePair_whenNothingToRemove_returnsFalse() {
            addNote(2, new int[]{4, 3}, new int[]{4, 4});
            Grid before = new Grid(grid);

            assertFalse(RuleApplier.boxLineReductionPair(grid));
            assertEquals(before, grid);
        }

        @Test
        @DisplayName("Triple: only boxLineReductionTriple applies to 3 cells on a row")
        void boxLineTriple_onRow_removesNoteInRestOfBlock() {
            int note = 8;
            // Row 4: note is present only in (4,3), (4,4), (4,5), all in the central block
            addNote(note, new int[]{4, 3}, new int[]{4, 4}, new int[]{4, 5});
            // (3,3) in the block (to be removed), (0,3) outside the block (to be kept)
            addNote(note, new int[]{3, 3}, new int[]{0, 3});

            assertFalse(RuleApplier.boxLineReductionPair(grid), "Pair should not process 3 cells");
            assertTrue(RuleApplier.boxLineReductionTriple(grid));

            assertTrue(has(4, 3, note));
            assertTrue(has(4, 4, note));
            assertTrue(has(4, 5, note));
            assertFalse(has(3, 3, note));
            assertTrue(has(0, 3, note), "a cell outside the block should not be modified");
        }
    }

    @Nested
    @DisplayName("X-Wing")
    class XWingTests {

        @Test
        @DisplayName("X-Wing on rows: removes candidate from the rest of the two columns")
        void xWing_onRows_removesNoteInColumns() {
            int note = 3;
            // Rows 1 and 4: candidate is only present in columns 2 and 5
            addNote(note, new int[]{1, 2}, new int[]{1, 5});
            addNote(note, new int[]{4, 2}, new int[]{4, 5});

            // Other occurrences in these columns (to be removed) and one outside columns (to be kept)
            addNote(note, new int[]{7, 2}, new int[]{8, 5}, new int[]{7, 0});

            assertTrue(RuleApplier.XWing(grid));
            assertTrue(has(1, 2, note));
            assertTrue(has(1, 5, note));
            assertTrue(has(4, 2, note));
            assertTrue(has(4, 5, note));
            assertFalse(has(7, 2, note));
            assertFalse(has(8, 5, note));
            assertTrue(has(7, 0, note), "a cell outside the target columns should not be modified");
        }

        @Test
        @DisplayName("X-Wing on columns: removes candidate from the rest of the two rows")
        void xWing_onColumns_removesNoteInRows() {
            int note = 6;
            // Columns 1 and 6: candidate is only present in rows 2 and 7
            addNote(note, new int[]{2, 1}, new int[]{7, 1});
            addNote(note, new int[]{2, 6}, new int[]{7, 6});

            // Other occurrences in these rows (to be removed) and one outside rows (to be kept)
            addNote(note, new int[]{2, 3}, new int[]{7, 8}, new int[]{0, 3});

            assertTrue(RuleApplier.XWing(grid));
            assertTrue(has(2, 1, note));
            assertTrue(has(7, 1, note));
            assertTrue(has(2, 6, note));
            assertTrue(has(7, 6, note));
            assertFalse(has(2, 3, note));
            assertFalse(has(7, 8, note));
            assertTrue(has(0, 3, note), "a cell outside the target rows should not be modified");
        }

        @Test
        @DisplayName("X-Wing: returns false when there is nothing to remove")
        void xWing_whenNothingToRemove_returnsFalse() {
            int note = 2;
            addNote(note, new int[]{1, 2}, new int[]{1, 5});
            addNote(note, new int[]{4, 2}, new int[]{4, 5});

            Grid before = new Grid(grid);
            assertFalse(RuleApplier.XWing(grid));
            assertEquals(before, grid);
        }

        @Test
        @DisplayName("X-Wing: ignores already filled cells")
        void xWing_ignoresFilledCells() {
            int note = 3;
            addNote(note, new int[]{1, 2}, new int[]{1, 5});
            addNote(note, new int[]{4, 2}, new int[]{4, 5});
            addNote(note, new int[]{7, 2});

            // Cell (7,2) is filled: its candidate should not be modified
            grid.addNumber(pos(7, 2), 4);

            assertFalse(RuleApplier.XWing(grid));
            assertTrue(has(7, 2, note));
        }
    }

    @Nested
    @DisplayName("Y-Wing")
    class YWingTests {

        @Test
        @DisplayName("Classic Y-Wing: removes target candidate seeing both pincers")
        void yWing_removesTargetCandidate() {
            int candA = 1;
            int candB = 2;
            int candC = 3;

            // Pivot at (0,0) with {A, B}
            addNote(candA, new int[]{0, 0});
            addNote(candB, new int[]{0, 0});

            // Pincer 1 at (0,5) with {A, C} (sees pivot via row 0)
            addNote(candA, new int[]{0, 5});
            addNote(candC, new int[]{0, 5});

            // Pincer 2 at (5,0) with {B, C} (sees pivot via column 0)
            addNote(candB, new int[]{5, 0});
            addNote(candC, new int[]{5, 0});

            // Cell (5,5): sees Pincer 1 (col 5) and Pincer 2 (row 5) -> C should be removed
            addNote(candC, new int[]{5, 5});
            addNote(candA, new int[]{5, 5});

            // Control cell (7,5) seeing only Pincer 1
            addNote(candC, new int[]{7, 5});

            assertTrue(RuleApplier.YWing(grid));
            assertTrue(has(0, 0, candA));
            assertTrue(has(0, 5, candC));
            assertTrue(has(5, 0, candC));
            assertFalse(has(5, 5, candC));
            assertTrue(has(5, 5, candA));
            assertTrue(has(7, 5, candC), "a cell seeing only one pincer should not be modified");
        }

        @Test
        @DisplayName("Y-Wing: returns false if pivot has more than 2 candidates")
        void yWing_whenPivotNotBivalue_returnsFalse() {
            int candA = 1;
            int candB = 2;
            int candC = 3;

            // Pivot with 3 candidates
            addNote(candA, new int[]{0, 0});
            addNote(candB, new int[]{0, 0});
            addNote(candC, new int[]{0, 0});

            addNote(candA, new int[]{0, 5});
            addNote(candC, new int[]{0, 5});
            addNote(candB, new int[]{5, 0});
            addNote(candC, new int[]{5, 0});
            addNote(candC, new int[]{5, 5});

            Grid before = new Grid(grid);
            assertFalse(RuleApplier.YWing(grid));
            assertEquals(before, grid);
        }

        @Test
        @DisplayName("Y-Wing: returns false if pincers do not see the pivot")
        void yWing_whenWingsDoNotSeePivot_returnsFalse() {
            int candA = 1;
            int candB = 2;
            int candC = 3;

            addNote(candA, new int[]{0, 0});
            addNote(candB, new int[]{0, 0});

            // Pincer 1 sees the pivot
            addNote(candA, new int[]{0, 5});
            addNote(candC, new int[]{0, 5});

            // Pincer 2 at (7,8) shares no row, column, or block with (0,0)
            addNote(candB, new int[]{7, 8});
            addNote(candC, new int[]{7, 8});
            addNote(candC, new int[]{7, 5});

            Grid before = new Grid(grid);
            assertFalse(RuleApplier.YWing(grid));
            assertEquals(before, grid);
        }

        @Test
        @DisplayName("Y-Wing: returns false when there is nothing to remove")
        void yWing_whenNothingToRemove_returnsFalse() {
            int candA = 1;
            int candB = 2;
            int candC = 3;

            addNote(candA, new int[]{0, 0});
            addNote(candB, new int[]{0, 0});
            addNote(candA, new int[]{0, 5});
            addNote(candC, new int[]{0, 5});
            addNote(candB, new int[]{5, 0});
            addNote(candC, new int[]{5, 0});

            Grid before = new Grid(grid);
            assertFalse(RuleApplier.YWing(grid));
            assertEquals(before, grid);
        }
    }

    @Nested
    @DisplayName("FirstNaive")
    class FirstNaiveTests {

        /** Fills the grid from 81 digits (0 = empty). */
        private void load(String digits) {
            for (int p = 0; p < 81; p++) {
                int v = digits.charAt(p) - '0';
                if (v != 0) grid.addNumber(p, v);
            }
        }

        /** Checks every cell is filled and respects row/column/block constraints. */
        private void assertSolved() {
            for (int p = 0; p < 81; p++) {
                int v = grid.getNumber(p);
                assertNotEquals(0, v, "cell " + p + " should be filled");
                assertTrue(grid.checkValidValues(p, v), "cell " + p + " breaks a constraint");
            }
        }

        @Test
        @DisplayName("Solves a classic puzzle and keeps the given clues")
        void firstNaive_solvesPuzzle() {
            String puzzle = "530070000600195000098000060800060003400803001700020006060000280000419005000080079";
            String solution = "534678912672195348198342567859761423426853791713924856961537284287419635345286179";
            load(puzzle);

            assertTrue(RuleApplier.FirstNaive(grid));
            assertSolved();
            for (int p = 0; p < 81; p++) {
                assertEquals(solution.charAt(p) - '0', grid.getNumber(p));
            }
        }

        @Test
        @DisplayName("Empty grid: finds the first solution in arithmetic order")
        void firstNaive_emptyGrid_firstRowIsAscending() {
            assertTrue(RuleApplier.FirstNaive(grid));
            assertSolved();
            for (int col = 0; col < 9; col++) {
                assertEquals(col + 1, grid.getNumber(pos(0, col)));
            }
        }

        @Test
        @DisplayName("Returns false and leaves the grid unchanged when there is no solution")
        void firstNaive_noSolution_returnsFalseAndRestoresGrid() {
            // Row 0 holds 1..8 in columns 0..7, and 9 is already in column 8 further down:
            // cell (0,8) can take no value.
            for (int col = 0; col < 8; col++) grid.addNumber(pos(0, col), col + 1);
            grid.addNumber(pos(5, 8), 9);
            Grid before = new Grid(grid);

            assertFalse(RuleApplier.FirstNaive(grid));
            assertEquals(before, grid);
        }

        @Test
        @DisplayName("Returns false on an already full grid")
        void firstNaive_fullGrid_returnsFalse() {
            load("534678912672195348198342567859761423426853791713924856961537284287419635345286179");
            assertFalse(RuleApplier.FirstNaive(grid));
        }
    }

    @Nested
    @DisplayName("Swordfish")
    class SwordfishTests {

        @Test
        @DisplayName("Swordfish on rows: removes candidate from the rest of the 3 columns")
        void swordfish_onRows_removesNoteInColumns() {
            int note = 5;

            // 3 rows (0, 3, 6) restricted to 3 columns (1, 4, 7)
            addNote(note, new int[]{0, 1}, new int[]{0, 4});
            addNote(note, new int[]{3, 4}, new int[]{3, 7});
            addNote(note, new int[]{6, 1}, new int[]{6, 7});

            // Occurrences to remove in columns 1, 4, 7 outside these 3 rows
            addNote(note, new int[]{1, 1}, new int[]{8, 4}, new int[]{5, 7});

            // Control cell on column 0 (to be kept)
            addNote(note, new int[]{1, 0});

            assertTrue(RuleApplier.Swordfish(grid));
            assertTrue(has(0, 1, note));
            assertTrue(has(0, 4, note));
            assertTrue(has(3, 4, note));
            assertTrue(has(3, 7, note));
            assertTrue(has(6, 1, note));
            assertTrue(has(6, 7, note));

            assertFalse(has(1, 1, note));
            assertFalse(has(8, 4, note));
            assertFalse(has(5, 7, note));
            assertTrue(has(1, 0, note), "a cell outside the target columns should not be modified");
        }

        @Test
        @DisplayName("Swordfish on columns: removes candidate from the rest of the 3 rows")
        void swordfish_onColumns_removesNoteInRows() {
            int note = 1;

            // 3 columns (0, 4, 8) restricted to 3 rows (1, 2, 7)
            addNote(note, new int[]{1, 0}, new int[]{2, 0});
            addNote(note, new int[]{2, 4}, new int[]{7, 4});
            addNote(note, new int[]{1, 8}, new int[]{7, 8});

            // Occurrences to remove on rows 1, 2, 7 outside these 3 columns
            addNote(note, new int[]{1, 3}, new int[]{7, 5});

            // Control cell
            addNote(note, new int[]{5, 3});

            assertTrue(RuleApplier.Swordfish(grid));
            assertTrue(has(1, 0, note));
            assertTrue(has(2, 0, note));
            assertTrue(has(2, 4, note));
            assertTrue(has(7, 4, note));
            assertTrue(has(1, 8, note));
            assertTrue(has(7, 8, note));

            assertFalse(has(1, 3, note));
            assertFalse(has(7, 5, note));
            assertTrue(has(5, 3, note), "a cell outside the target rows should not be modified");
        }

        @Test
        @DisplayName("Swordfish: returns false when there is nothing to remove")
        void swordfish_whenNothingToRemove_returnsFalse() {
            int note = 4;
            addNote(note, new int[]{0, 1}, new int[]{0, 4});
            addNote(note, new int[]{3, 4}, new int[]{3, 7});
            addNote(note, new int[]{6, 1}, new int[]{6, 7});

            Grid before = new Grid(grid);
            assertFalse(RuleApplier.Swordfish(grid));
            assertEquals(before, grid);
        }
    }
}