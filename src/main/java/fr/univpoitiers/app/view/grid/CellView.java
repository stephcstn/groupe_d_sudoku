
package fr.univpoitiers.app.view.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

/**
 * Displays a cell of the Sudoku grid.
 *
 * <p>The cell stacks two layers: a grid of notes (candidates) in the background and
 * a pane holding the final value in the foreground. It only displays what the
 * controller gives it and never modifies the game model.</p>
 *
 * <p>The visual states (selection, highlight) are carried by the CSS classes
 * {@value #SELECTED_STYLE_CLASS} and {@value #HIGHLIGHTED_STYLE_CLASS}, defined in
 * {@code style.css}.</p>
 *
 * <p><b>Threading:</b> every method must be called from the JavaFX thread
 * (use {@code Platform.runLater} from another thread).</p>
 */
public class CellView extends StackPane {

    /** Size (width and height) of a cell, in pixels. */
    private static final double CELL_SIZE = 50.0;

    private static final String CELL_STYLE_CLASS = "cell-view";
    private static final String VALUE_STYLE_CLASS = "cell-value";
    private static final String VALUE_PANE_STYLE_CLASS = "cell-value-pane";
    private static final String NOTE_STYLE_CLASS = "cell-note";

    /** CSS class applied to the selected cell. */
    static final String SELECTED_STYLE_CLASS = "selected";

    /** CSS class applied to the cells sharing a row, column or block with the selection. */
    static final String HIGHLIGHTED_STYLE_CLASS = "highlighted";

    /** CSS class applied to the cells with an error. */
    static final String ERROR_STYLE_CLASS = "error";

    /** Label of the final value; empty when the cell has no value. */
    private final Label finalValue;

    /** Note labels; the element at index {@code i} shows the candidate {@code i + 1}. */
    private final List<Label> noteLabels = new ArrayList<>();

    private boolean selected;
    private boolean highlighted;
    private boolean error;

    /**
     * Creates a cell showing the requested number of note slots.
     * The slots are spread over as many rows and columns as needed,
     * without adding a dummy note when the number is not a perfect square.
     *
     * @param howManyNotes number of note slots, strictly positive
     * @param css          external URL of the style sheet to apply, not null
     * @throws IllegalArgumentException if the number of slots is not positive
     * @throws NullPointerException     if the style sheet is null
     */
    public CellView(int howManyNotes, String css) {
        if (howManyNotes <= 0) {
            throw new IllegalArgumentException("Le nombre de notes doit être positif.");
        }
        Objects.requireNonNull(css, "La feuille de style ne peut pas être nulle.");

        getStylesheets().add(css);
        getChildren().add(createNotesGrid(howManyNotes));

        finalValue = new Label();
        finalValue.getStyleClass().add(VALUE_STYLE_CLASS);
        StackPane valuePane = new StackPane(finalValue);
        valuePane.getStyleClass().add(VALUE_PANE_STYLE_CLASS);
        getChildren().add(valuePane);

        getStyleClass().add(CELL_STYLE_CLASS);
        setPrefSize(CELL_SIZE, CELL_SIZE);
        setMinSize(CELL_SIZE, CELL_SIZE);
        setMaxSize(CELL_SIZE, CELL_SIZE);
    }

    /**
     * Builds the notes grid and stores its labels in {@link #noteLabels}.
     *
     * @param howManyNotes number of note slots
     * @return the notes grid, ready to be added to the cell
     */
    private GridPane createNotesGrid(int howManyNotes) {
        int columns = (int) Math.ceil(Math.sqrt(howManyNotes));
        int rows = (int) Math.ceil((double) howManyNotes / columns);
        GridPane notesGrid = new GridPane();

        for (int column = 0; column < columns; column++) {
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setPercentWidth(100.0 / columns);
            constraints.setHalignment(HPos.CENTER);
            constraints.setFillWidth(false);
            notesGrid.getColumnConstraints().add(constraints);
        }
        for (int row = 0; row < rows; row++) {
            RowConstraints constraints = new RowConstraints();
            constraints.setPercentHeight(100.0 / rows);
            constraints.setValignment(VPos.CENTER);
            constraints.setFillHeight(false);
            notesGrid.getRowConstraints().add(constraints);
        }

        for (int index = 0; index < howManyNotes; index++) {
            Label note = new Label();
            note.getStyleClass().add(NOTE_STYLE_CLASS);
            noteLabels.add(note);
            notesGrid.add(note, index % columns, index / columns);
        }
        return notesGrid;
    }

    /**
     * Displays a value, or clears the display for the value zero.
     *
     * @param value the value to display, between zero and the number of notes
     * @throws IllegalArgumentException if the value is out of bounds
     */
    public void setValue(int value) {
        if (value < 0 || value > noteLabels.size()) {
            throw new IllegalArgumentException("La valeur doit être comprise entre 0 et "
                    + noteLabels.size() + ".");
        }
        finalValue.setText(value == 0 ? "" : Integer.toString(value));
    }

    /**
     * Displays exactly the given candidate notes; the others are cleared.
     *
     * @param notes the candidates to display, numbered from one, not null
     * @throws NullPointerException     if the set is null
     * @throws IllegalArgumentException if a candidate is out of bounds
     */
    public void setNotes(Set<Integer> notes) {
        Objects.requireNonNull(notes, "Les notes ne peuvent pas être nulles.");
        for (int note : notes) {
            requireValidNote(note);
        }
        for (int index = 0; index < noteLabels.size(); index++) {
            int candidate = index + 1;
            noteLabels.get(index).setText(notes.contains(candidate) ? Integer.toString(candidate) : "");
        }
    }

    /**
     * Changes the visual selection state of the cell.
     *
     * @param selected true if the cell is selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        setStyleClassPresent(SELECTED_STYLE_CLASS, selected);
    }

    /**
     * Tells whether the cell is displayed as selected.
     *
     * @return true if the cell is selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Changes the visual highlight state of a cell related to the selection.
     *
     * @param highlighted true if the cell shares the row, the column or the block of the selection
     */
    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        setStyleClassPresent(HIGHLIGHTED_STYLE_CLASS, highlighted);
    }

    /**
     * Tells whether the cell is displayed as highlighted.
     *
     * @return true if the cell is highlighted
     */
    public boolean isHighlighted() {
        return highlighted;
    }

    /**
     * Returns the value currently displayed.
     *
     * @return the displayed value, or 0 if the cell is empty
     */
    public int getValue() {
        return finalValue.getText().isEmpty() ? 0 : Integer.parseInt(finalValue.getText());
    }

    /**
     * Tells whether a candidate is currently displayed in the notes.
     *
     * @param value the tested candidate, starting at one
     * @return true if the note is displayed
     * @throws IllegalArgumentException if the candidate is out of bounds
     */
    public boolean hasNote(int value) {
        requireValidNote(value);
        return !noteLabels.get(value - 1).getText().isEmpty();
    }

    /**
     * Adds or removes a CSS class without ever duplicating it.
     *
     * @param styleClass the CSS class concerned
     * @param present    true to add it, false to remove it
     */
    private void setStyleClassPresent(String styleClass, boolean present) {
        if (present) {
            if (!getStyleClass().contains(styleClass)) {
                getStyleClass().add(styleClass);
            }
        } else {
            getStyleClass().remove(styleClass);
        }
    }

    /**
     * Checks that a candidate is between 1 and the number of note slots.
     *
     * @param note the candidate to check
     * @throws IllegalArgumentException if the candidate is out of bounds
     */
    private void requireValidNote(int note) {
        if (note < 1 || note > noteLabels.size()) {
            throw new IllegalArgumentException("La note doit être comprise entre 1 et "
                    + noteLabels.size() + ".");
        }
    }

    public void setError(boolean error) {
        this.error = error;
        setStyleClassPresent(ERROR_STYLE_CLASS, error);
    }
}