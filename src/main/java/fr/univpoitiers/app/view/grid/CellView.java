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
 * Affiche une cellule de la grille de Sudoku.
 *
 * <p>La cellule affiche une valeur ou des notes fournies par le contrôleur ;
 * elle ne modifie pas le modèle du jeu.</p>
 */
public class CellView extends StackPane {

    private static final double CELL_SIZE = 50.0;

    private final Label finalValue;
    private final List<Label> noteLabels = new ArrayList<>();
    private boolean selected;
    private boolean highlighted;

    /**
     * Crée une cellule affichant le nombre demandé d'emplacements de notes.
     * Les emplacements sont répartis sur autant de lignes et de colonnes que
     * nécessaire, sans ajouter de note fictive lorsque le nombre n'est pas carré.
     *
     * @param howManyNotes nombre d'emplacements de notes, strictement positif
     * @param css          feuille de style à appliquer à la cellule
     * @throws IllegalArgumentException si le nombre d'emplacements n'est pas positif
     */
    public CellView(int howManyNotes, String css) {
        if (howManyNotes <= 0) {
            throw new IllegalArgumentException("Le nombre de notes doit être positif.");
        }

        getStylesheets().add(css);
        getChildren().add(createNotesGrid(howManyNotes));

        finalValue = new Label();
        finalValue.getStyleClass().add("cell-value");
        StackPane valuePane = new StackPane(finalValue);
        valuePane.getStyleClass().add("cell-value-pane");
        getChildren().add(valuePane);

        getStyleClass().add("cell-view");
        setPrefSize(CELL_SIZE, CELL_SIZE);
        setMinSize(CELL_SIZE, CELL_SIZE);
        setMaxSize(CELL_SIZE, CELL_SIZE);
    }

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

        for (int value = 0; value < howManyNotes; value++) {
            Label note = new Label();
            note.getStyleClass().add("cell-note");
            noteLabels.add(note);
            notesGrid.add(note, value % columns, value / columns);
        }
        return notesGrid;
    }

    /**
     * Affiche une valeur, ou vide l'affichage pour la valeur zéro.
     *
     * @param value valeur à afficher, entre zéro et le nombre de notes
     * @throws IllegalArgumentException si la valeur est hors limites
     */
    public void setValue(int value) {
        if (value < 0 || value > noteLabels.size()) {
            throw new IllegalArgumentException("La valeur doit être comprise entre 0 et "
                    + noteLabels.size() + ".");
        }
        finalValue.setText(value == 0 ? "" : Integer.toString(value));
    }

    /**
     * Affiche exactement les notes candidates fournies.
     *
     * @param notes candidats à afficher, numérotés à partir de un
     * @throws IllegalArgumentException si un candidat est hors limites
     */
    public void setNotes(Set<Integer> notes) {
        Objects.requireNonNull(notes, "Les notes ne peuvent pas être nulles.");
        for (int note : notes) {
            if (note < 1 || note > noteLabels.size()) {
                throw new IllegalArgumentException("Une note doit être comprise entre 1 et "
                        + noteLabels.size() + ".");
            }
        }
        for (int index = 0; index < noteLabels.size(); index++) {
            int value = index + 1;
            noteLabels.get(index).setText(notes.contains(value) ? Integer.toString(value) : "");
        }
    }

    /**
     * Modifie l'état visuel de sélection de la cellule.
     *
     * @param selected vrai si la cellule est sélectionnée
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            if (!getStyleClass().contains("selected")) {
                getStyleClass().add("selected");
            }
        } else {
            getStyleClass().remove("selected");
        }
    }

    public boolean isSelected() {
        return selected;
    }

    /**
     * Modifie l'état visuel de surbrillance d'une cellule liée à la sélection.
     *
     * @param highlighted vrai si la cellule partage la ligne, la colonne ou le bloc sélectionné
     */
    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        if (highlighted) {
            if (!getStyleClass().contains("highlighted")) {
                getStyleClass().add("highlighted");
            }
        } else {
            getStyleClass().remove("highlighted");
        }
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public int getValue() {
        return finalValue.getText().isEmpty() ? 0 : Integer.parseInt(finalValue.getText());
    }

    public boolean hasNote(int value) {
        if (value < 1 || value > noteLabels.size()) {
            throw new IllegalArgumentException("La note doit être comprise entre 1 et "
                    + noteLabels.size() + ".");
        }
        return !noteLabels.get(value - 1).getText().isEmpty();
    }
}