/*
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

    public void setSelected(boolean selected) {
        String style = "selected";
        this.selected = selected;
        if (selected) {
            if (!getStyleClass().contains(style)) {
                getStyleClass().add(style);
            }
        } else {
            getStyleClass().remove(style);
        }
    }

    public boolean isSelected() {
        return selected;
    }


     * Modifie l'état visuel de surbrillance d'une cellule liée à la sélection.
     *
     * @param highlighted vrai si la cellule partage la ligne, la colonne ou le bloc sélectionné

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        String style = "highlighted";
        if (highlighted) {
            if (!getStyleClass().contains(style)) {
                getStyleClass().add(style);
            }
        } else {
            getStyleClass().remove(style);
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

*/
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
 * <p>La cellule superpose deux couches : une grille de notes (candidats) en arrière-plan et
 * un panneau contenant la valeur finale au premier plan. Elle affiche uniquement ce que le
 * contrôleur lui fournit et ne modifie jamais le modèle du jeu.</p>
 *
 * <p>Les états visuels (sélection, surbrillance) sont portés par les classes CSS
 * {@value #SELECTED_STYLE_CLASS} et {@value #HIGHLIGHTED_STYLE_CLASS}, définies dans
 * {@code style.css}.</p>
 *
 * <p><b>Threading :</b> toutes les méthodes doivent être appelées depuis le thread JavaFX
 * (utiliser {@code Platform.runLater} depuis un autre thread).</p>
 */
public class CellView extends StackPane {

    /** Taille (largeur et hauteur) d'une cellule, en pixels. */
    private static final double CELL_SIZE = 50.0;

    private static final String CELL_STYLE_CLASS = "cell-view";
    private static final String VALUE_STYLE_CLASS = "cell-value";
    private static final String VALUE_PANE_STYLE_CLASS = "cell-value-pane";
    private static final String NOTE_STYLE_CLASS = "cell-note";

    /** Classe CSS appliquée à la cellule sélectionnée. */
    static final String SELECTED_STYLE_CLASS = "selected";

    /** Classe CSS appliquée aux cellules partageant ligne, colonne ou bloc avec la sélection. */
    static final String HIGHLIGHTED_STYLE_CLASS = "highlighted";

    /** Libellé de la valeur finale ; vide lorsque la cellule n'a pas de valeur. */
    private final Label finalValue;

    /** Libellés de notes ; l'élément d'indice {@code i} affiche le candidat {@code i + 1}. */
    private final List<Label> noteLabels = new ArrayList<>();

    private boolean selected;
    private boolean highlighted;

    /**
     * Crée une cellule affichant le nombre demandé d'emplacements de notes.
     * Les emplacements sont répartis sur autant de lignes et de colonnes que
     * nécessaire, sans ajouter de note fictive lorsque le nombre n'est pas carré.
     *
     * @param howManyNotes nombre d'emplacements de notes, strictement positif
     * @param css          URL externe de la feuille de style à appliquer, non nulle
     * @throws IllegalArgumentException si le nombre d'emplacements n'est pas positif
     * @throws NullPointerException     si la feuille de style est nulle
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
     * Construit la grille de notes et enregistre ses libellés dans {@link #noteLabels}.
     *
     * @param howManyNotes nombre d'emplacements de notes
     * @return la grille de notes prête à être ajoutée à la cellule
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
     * Affiche exactement les notes candidates fournies ; les autres sont effacées.
     *
     * @param notes candidats à afficher, numérotés à partir de un, non nul
     * @throws NullPointerException     si l'ensemble est nul
     * @throws IllegalArgumentException si un candidat est hors limites
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
     * Modifie l'état visuel de sélection de la cellule.
     *
     * @param selected vrai si la cellule est sélectionnée
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        setStyleClassPresent(SELECTED_STYLE_CLASS, selected);
    }

    /**
     * Indique si la cellule est affichée comme sélectionnée.
     *
     * @return vrai si la cellule est sélectionnée
     */
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
        setStyleClassPresent(HIGHLIGHTED_STYLE_CLASS, highlighted);
    }

    /**
     * Indique si la cellule est affichée en surbrillance.
     *
     * @return vrai si la cellule est en surbrillance
     */
    public boolean isHighlighted() {
        return highlighted;
    }

    /**
     * Retourne la valeur actuellement affichée.
     *
     * @return la valeur affichée, ou 0 si la cellule est vide
     */
    public int getValue() {
        return finalValue.getText().isEmpty() ? 0 : Integer.parseInt(finalValue.getText());
    }

    /**
     * Indique si un candidat est actuellement affiché dans les notes.
     *
     * @param value candidat testé, à partir de un
     * @return vrai si la note est affichée
     * @throws IllegalArgumentException si le candidat est hors limites
     */
    public boolean hasNote(int value) {
        requireValidNote(value);
        return !noteLabels.get(value - 1).getText().isEmpty();
    }

    /**
     * Ajoute ou retire une classe CSS sans jamais la dupliquer.
     *
     * @param styleClass classe CSS concernée
     * @param present    vrai pour l'ajouter, faux pour la retirer
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
     * Vérifie qu'un candidat est compris entre 1 et le nombre d'emplacements de notes.
     *
     * @param note candidat à vérifier
     * @throws IllegalArgumentException si le candidat est hors limites
     */
    private void requireValidNote(int note) {
        if (note < 1 || note > noteLabels.size()) {
            throw new IllegalArgumentException("La note doit être comprise entre 1 et "
                    + noteLabels.size() + ".");
        }
    }
}