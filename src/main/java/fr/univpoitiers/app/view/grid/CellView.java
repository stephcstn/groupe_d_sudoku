package fr.univpoitiers.app.view.grid;

import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import java.util.function.Consumer;

/**
 * Représente la vue d'une cellule dans la grille du jeu de Sudoku.
 * Gère l'affichage des notes, de la valeur finale et des interactions utilisateur (clics).
 */
public class CellView extends StackPane {

    private GridPane gridNotes;
    private StackPane finalPane;
    private Label finalValue;
    private final fr.univpoitiers.core.grid.Cell cell;
    private final NoteView[] noteViews;

    private int posX;
    private int posY;
    private boolean isSelected;

    /** Callback exécuté lorsque la cellule est cliquée pour notifier la grille parente. */
    private Consumer<CellView> onClickAction;

    /**
     * Construit une nouvelle vue de cellule.
     *
     * @param howManyNotes Le nombre total de notes/candidats possibles.
     * @param css Le chemin vers la feuille de style CSS.
     * @param xP La position X (colonne) dans la grille.
     * @param yP La position Y (ligne) dans la grille.
     */
    public CellView(int howManyNotes, String css, int xP, int yP) {
        this.posX = xP;
        this.posY = yP;
        this.isSelected = false;
        this.cell = new fr.univpoitiers.core.grid.Cell(howManyNotes);
        this.noteViews = new NoteView[howManyNotes + 1];

        this.getStylesheets().add(css);
        this.getStyleClass().add("cell-view");

        gridNotes = new GridPane();
        int columns = (int) Math.ceil(Math.sqrt(howManyNotes));
        int rows = (int) Math.ceil((double) howManyNotes / columns);
        for (int column = 0; column < columns; column++) {
            ColumnConstraints constraint = new ColumnConstraints();
            constraint.setPercentWidth(100.0 / columns);
            gridNotes.getColumnConstraints().add(constraint);
        }
        for (int row = 0; row < rows; row++) {
            RowConstraints constraint = new RowConstraints();
            constraint.setPercentHeight(100.0 / rows);
            gridNotes.getRowConstraints().add(constraint);
        }
        for (int value = 1; value <= howManyNotes; value++) {
            NoteView noteView = new NoteView(value);
            noteViews[value] = noteView;
            gridNotes.add(noteView, (value - 1) % columns, (value - 1) / columns);
        }
        this.getChildren().add(gridNotes);

        finalValue = new Label("");
        finalValue.getStyleClass().add("cell-value");
        finalPane = new StackPane();
        finalPane.getChildren().add(finalValue);
        finalPane.getStyleClass().add("cell-value-pane");
        this.getChildren().add(finalPane);

        this.setPrefSize(50, 50);
        this.setMinSize(50, 50);
        this.setMaxSize(50, 50);

        // Gestion du clic sur la cellule
        this.setOnMouseClicked(event -> {
            if (onClickAction != null) {
                onClickAction.accept(this);
            }
        });
    }

    /**
     * Définit l'action à exécuter lors du clic sur la cellule.
     *
     * @param action Le consommateur prenant cette cellule en paramètre.
     */
    public void setOnClickAction(Consumer<CellView> action) {
        this.onClickAction = action;
    }

    /**
     * Applique ou retire l'état sélectionné de la cellule.
     *
     * @param selected Vrai si la cellule doit être marquée comme sélectionnée.
     */
    public void setSelectedStyle(boolean selected) {
        this.isSelected = selected;
        if (selected) {
            if (!this.getStyleClass().contains("selected")) {
                this.getStyleClass().add("selected");
            }
        } else {
            this.getStyleClass().remove("selected");
        }
    }

    /**
     * Applique ou retire l'état mis en surbrillance (ligne/colonne/bloc).
     *
     * @param highlighted Vrai si la cellule fait partie des pairs à colorer.
     */
    public void setHighlightedStyle(boolean highlighted) {
        if (highlighted) {
            if (!this.getStyleClass().contains("highlighted")) {
                this.getStyleClass().add("highlighted");
            }
        } else {
            this.getStyleClass().remove("highlighted");
        }
    }

    /** @return L'indice de la colonne dans la grille. */
    public int getPosX() { return posX; }

    /** @return L'indice de la ligne dans la grille. */
    public int getPosY() { return posY; }

    /** @return La cellule du modèle associée à cette vue. */
    public fr.univpoitiers.core.grid.Cell getCell() {
        return cell;
    }

    /**
     * Définit la valeur de la cellule et actualise son affichage.
     *
     * @param value Valeur à saisir, ou 0 pour vider la cellule.
     */
    public void setCellValue(int value) {
        cell.setValue(value);
        refresh();
    }

    /**
     * Ajoute ou retire une note, puis actualise l'affichage.
     * La cellule doit être vide et non verrouillée pour modifier ses notes.
     *
     * @param value Valeur de la note à modifier.
     */
    public void toggleNote(int value) {
        if (cell.hasNote(value)) {
            cell.removeNote(value);
        } else {
            cell.setNote(value);
        }
        refresh();
    }

    /** Efface la valeur et les notes, sauf si la cellule est verrouillée. */
    public void clearCell() {
        cell.setValue(0);
        refresh();
    }

    /** Efface la valeur et les notes et déverrouille la cellule. */
    public void resetCell() {
        cell.reset();
        refresh();
    }

    /** Synchronise l'affichage de la valeur et des notes avec le modèle. */
    private void refresh() {
        int value = cell.getValue();
        finalValue.setText(value == 0 ? "" : String.valueOf(value));
        gridNotes.setVisible(value == 0);
        gridNotes.setManaged(value == 0);
        for (int note = 1; note < noteViews.length; note++) {
            noteViews[note].setNoteActive(value == 0 && cell.hasNote(note));
        }
    }
}