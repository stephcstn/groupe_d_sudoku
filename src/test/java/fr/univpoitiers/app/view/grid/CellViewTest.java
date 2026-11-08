package fr.univpoitiers.app.view.grid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.univpoitiers.app.view.pad.PadView;
import java.util.Set;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.image.ImageView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "DISPLAY", matches = ".+")
class CellViewTest {

    private static final String CSS = CellViewTest.class
            .getResource("/fr/univpoitiers/css/style.css").toExternalForm();

    @BeforeAll
    static void startJavaFx() {
        Platform.startup(() -> {
        });
    }

    @AfterAll
    static void stopJavaFx() {
        Platform.exit();
    }

    @Test
    void createsExactlyTheRequestedNumberOfNoteSlots() throws Exception {
        runOnJavaFxThread(() -> {
            for (int noteCount : new int[]{1, 2, 5, 9}) {
                CellView cell = new CellView(noteCount, CSS);
                GridPane notesGrid = (GridPane) cell.getChildren().get(0);
                int expectedColumns = (int) Math.ceil(Math.sqrt(noteCount));
                int expectedRows = (int) Math.ceil((double) noteCount / expectedColumns);

                assertEquals(noteCount, notesGrid.getChildren().size());
                assertEquals(expectedColumns, notesGrid.getColumnConstraints().size());
                assertEquals(expectedRows, notesGrid.getRowConstraints().size());
                for (int index = 0; index < noteCount; index++) {
                    Node note = notesGrid.getChildren().get(index);
                    assertEquals(index % expectedColumns, gridIndex(GridPane.getColumnIndex(note)));
                    assertEquals(index / expectedColumns, gridIndex(GridPane.getRowIndex(note)));
                }
            }
            return null;
        });
    }

    @Test
    void rejectsNonPositiveNoteCount() throws Exception {
        runOnJavaFxThread(() -> {
            assertThrows(IllegalArgumentException.class, () -> new CellView(0, CSS));
            assertThrows(IllegalArgumentException.class, () -> new CellView(-1, CSS));
            return null;
        });
    }

    @Test
    void selectingACellReplacesThePreviousSelection() throws Exception {
        runOnJavaFxThread(() -> {
            GridView grid = new GridView(2, 1, CSS);
            CellView firstCell = grid.getCellAt(0, 0);
            CellView secondCell = grid.getCellAt(0, 1);

            firstCell.fireEvent(mouseClick());
            assertTrue(firstCell.isSelected());
            assertFalse(secondCell.isSelected());

            secondCell.fireEvent(mouseClick());
            assertFalse(firstCell.isSelected());
            assertTrue(secondCell.isSelected());
            return null;
        });
    }

    @Test
    void selectionHighlightsItsRowColumnAndSubgrid() throws Exception {
        runOnJavaFxThread(() -> {
            GridView grid = new GridView(9, 9, CSS);

            grid.getCellAt(0, 0).fireEvent(mouseClick());
            assertTrue(grid.getCellAt(0, 0).isSelected());
            assertFalse(grid.getCellAt(0, 0).isHighlighted());
            assertTrue(grid.getCellAt(0, 8).isHighlighted());
            assertTrue(grid.getCellAt(8, 0).isHighlighted());
            assertTrue(grid.getCellAt(2, 2).isHighlighted());
            assertFalse(grid.getCellAt(3, 3).isHighlighted());

            grid.getCellAt(4, 4).fireEvent(mouseClick());
            assertFalse(grid.getCellAt(0, 0).isSelected());
            assertFalse(grid.getCellAt(0, 0).isHighlighted());
            assertTrue(grid.getCellAt(4, 0).isHighlighted());
            assertTrue(grid.getCellAt(0, 4).isHighlighted());
            assertTrue(grid.getCellAt(3, 3).isHighlighted());
            assertFalse(grid.getCellAt(0, 8).isHighlighted());
            assertTrue(grid.getCellAt(4, 4).isSelected());

            grid.getCellAt(8, 8).fireEvent(mouseClick());
            assertFalse(grid.getCellAt(4, 4).isSelected());
            assertFalse(grid.getCellAt(4, 4).isHighlighted());
            assertTrue(grid.getCellAt(8, 7).isHighlighted());
            assertTrue(grid.getCellAt(7, 8).isHighlighted());
            assertTrue(grid.getCellAt(6, 6).isHighlighted());
            assertFalse(grid.getCellAt(5, 5).isHighlighted());
            return null;
        });
    }

    @Test
    void highlightingHandlesIncompleteBlocksOnRectangularGrids() throws Exception {
        runOnJavaFxThread(() -> {
            GridView grid = new GridView(5, 4, CSS);

            grid.getCellAt(3, 4).fireEvent(mouseClick());

            assertTrue(grid.getCellAt(3, 3).isHighlighted());
            assertTrue(grid.getCellAt(3, 0).isHighlighted());
            assertTrue(grid.getCellAt(0, 4).isHighlighted());
            assertFalse(grid.getCellAt(2, 3).isHighlighted());
            assertTrue(grid.getCellAt(3, 4).isSelected());
            return null;
        });
    }

    @Test
    void displaysValuesAndOnlyEnabledCandidateNotes() throws Exception {
        runOnJavaFxThread(() -> {
            CellView cell = new CellView(9, CSS);
            cell.setNotes(Set.of(2, 8));
            assertTrue(cell.hasNote(2));
            assertFalse(cell.hasNote(1));
            assertTrue(cell.hasNote(8));

            cell.setValue(5);
            cell.setNotes(Set.of());
            assertEquals(5, cell.getValue());
            assertFalse(cell.hasNote(2));
            cell.setValue(0);
            assertEquals(0, cell.getValue());
            assertThrows(IllegalArgumentException.class, () -> cell.setValue(10));
            assertThrows(IllegalArgumentException.class, () -> cell.setNotes(Set.of(0)));
            return null;
        });
    }

    @Test
    void fxmlConnectsNotesModeDigitPadAndClearAction() throws Exception {
        runOnJavaFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univpoitiers/views/vue.fxml"));
            Parent root = loader.load();
            new Scene(root);
            GridView grid = (GridView) ((BorderPane) root.lookup("#centerPane")).getCenter();
            BorderPane rightPane = (BorderPane) root.lookup("#rightPane");
            VBox padControls = (VBox) rightPane.getCenter();
            PadView pad = (PadView) padControls.getChildren().get(0);
            CheckBox notesMode = (CheckBox) root.lookup("#notesMode");
            Button clear = (Button) root.lookup("#clearButton");
            Button reset = (Button) root.lookup("#resetGridButton");
            CellView firstCell = grid.getCellAt(0, 0);
            CellView secondCell = grid.getCellAt(0, 1);
            Button one = digitButton(pad, 1);
            Button two = digitButton(pad, 2);
            Button seven = digitButton(pad, 7);

            assertEquals(8.0, pad.getHgap(), 0.0);
            assertEquals(8.0, pad.getVgap(), 0.0);
            assertEquals(14.0, padControls.getSpacing(), 0.0);

            seven.fire();
            notesMode.setSelected(true);
            seven.fire();
            clear.fire();
            assertEquals(0, firstCell.getValue());
            assertFalse(firstCell.hasNote(7));

            firstCell.fireEvent(mouseClick());
            root.applyCss();
            assertEquals(Color.web("#e6c6bd"), firstCell.getBackground().getFills().get(0).getFill());
            assertEquals(Color.web("#efe7dd"), secondCell.getBackground().getFills().get(0).getFill());
            seven.fire();
            assertTrue(firstCell.hasNote(7));
            seven.fire();
            assertFalse(firstCell.hasNote(7));
            seven.fire();
            two.fire();
            assertTrue(firstCell.hasNote(7));
            assertTrue(firstCell.hasNote(2));

            secondCell.fireEvent(mouseClick());
            one.fire();
            assertTrue(secondCell.hasNote(1));
            assertTrue(firstCell.hasNote(7));

            firstCell.fireEvent(mouseClick());
            notesMode.setSelected(false);
            seven.fire();
            assertEquals(7, firstCell.getValue());
            assertFalse(firstCell.hasNote(7));
            notesMode.setSelected(true);
            two.fire();
            assertFalse(firstCell.hasNote(2));

            clear.fire();
            assertEquals(0, firstCell.getValue());
            assertFalse(firstCell.hasNote(7));
            assertFalse(firstCell.hasNote(2));
            assertTrue(secondCell.hasNote(1));
            seven.fire();
            assertTrue(firstCell.hasNote(7));
            clear.fire();
            assertFalse(firstCell.hasNote(7));

            notesMode.setSelected(false);
            seven.fire();
            secondCell.fireEvent(mouseClick());
            notesMode.setSelected(true);
            two.fire();
            assertEquals(7, firstCell.getValue());
            assertTrue(secondCell.hasNote(2));

            firstCell.fireEvent(mouseClick());
            reset.fire();
            assertTrue(firstCell.isSelected());
            assertTrue(notesMode.isSelected());
            for (int row = 0; row < 9; row++) {
                for (int column = 0; column < 9; column++) {
                    CellView cell = grid.getCellAt(row, column);
                    assertEquals(0, cell.getValue());
                    for (int digit = 1; digit <= 9; digit++) {
                        assertFalse(cell.hasNote(digit));
                    }
                }
            }
            seven.fire();
            assertTrue(firstCell.hasNote(7));
            return null;
        });
    }

    @Test
    void fxmlLockButtonUsesModelAndUpdatesItsIcon() throws Exception {
        runOnJavaFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univpoitiers/views/vue.fxml"));
            Parent root = loader.load();
            GridView grid = (GridView) ((BorderPane) root.lookup("#centerPane")).getCenter();
            Button lockButton = (Button) root.lookup("#lockGridButton");
            Button clearButton = (Button) root.lookup("#clearButton");
            Button resetButton = (Button) root.lookup("#resetGridButton");
            ImageView lockIcon = (ImageView) root.lookup("#lockIcon");
            PadView pad = (PadView) ((VBox) ((BorderPane) root.lookup("#rightPane")).getCenter())
                    .getChildren().get(0);
            CellView firstCell = grid.getCellAt(0, 0);
            CellView secondCell = grid.getCellAt(0, 1);

            assertEquals("UNLOCK", lockButton.getText());
            assertTrue(lockIcon.getImage().getUrl().endsWith("Cadenas-ouvert-vert.png"));

            firstCell.fireEvent(mouseClick());
            digitButton(pad, 4).fire();
            lockButton.fire();
            assertEquals("LOCK", lockButton.getText());
            assertTrue(lockIcon.getImage().getUrl().endsWith("Cadenas-ferme-rouge.png"));

            digitButton(pad, 7).fire();
            assertEquals(4, firstCell.getValue());
            clearButton.fire();
            assertEquals(4, firstCell.getValue());

            secondCell.fireEvent(mouseClick());
            digitButton(pad, 2).fire();
            assertEquals(2, secondCell.getValue());

            lockButton.fire();
            assertEquals("UNLOCK", lockButton.getText());
            assertTrue(lockIcon.getImage().getUrl().endsWith("Cadenas-ouvert-vert.png"));
            firstCell.fireEvent(mouseClick());
            digitButton(pad, 7).fire();
            assertEquals(7, firstCell.getValue());

            resetButton.fire();
            assertEquals("UNLOCK", lockButton.getText());
            assertTrue(lockIcon.getImage().getUrl().endsWith("Cadenas-ouvert-vert.png"));
            assertEquals(0, firstCell.getValue());
            assertEquals(0, secondCell.getValue());
            return null;
        });
    }

    @Test
    void gridUsesWidthForColumnsAndHeightForRows() throws Exception {
        runOnJavaFxThread(() -> {
            GridView grid = new GridView(3, 5, CSS);

            assertEquals(15, grid.getChildren().size());
            for (int index = 0; index < grid.getChildren().size(); index++) {
                Node cell = grid.getChildren().get(index);
                assertEquals(index % 3, gridIndex(GridPane.getColumnIndex(cell)));
                assertEquals(index / 3, gridIndex(GridPane.getRowIndex(cell)));
            }
            return null;
        });
    }

    @Test
    void rejectsNonPositiveGridDimensions() throws Exception {
        runOnJavaFxThread(() -> {
            assertThrows(IllegalArgumentException.class, () -> new GridView(0, 3, CSS));
            assertThrows(IllegalArgumentException.class, () -> new GridView(3, 0, CSS));
            return null;
        });
    }

    private static MouseEvent mouseClick() {
        return new MouseEvent(MouseEvent.MOUSE_CLICKED, 1, 1, 1, 1, MouseButton.PRIMARY, 1,
                false, false, false, false, false, false, false, false, false, true, null);
    }

    private static int gridIndex(Integer index) {
        return index == null ? 0 : index;
    }

    private static Button digitButton(PadView pad, int digit) {
        return pad.getChildren().stream()
                .filter(Button.class::isInstance)
                .map(Button.class::cast)
                .filter(button -> button.getText().equals(Integer.toString(digit)))
                .findFirst()
                .orElseThrow();
    }

    private static <T> T runOnJavaFxThread(java.util.concurrent.Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(30, TimeUnit.SECONDS);
    }
}