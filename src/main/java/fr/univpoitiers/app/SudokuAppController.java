package fr.univpoitiers.app;

import fr.univpoitiers.App;
import fr.univpoitiers.app.view.grid.CellView;
import fr.univpoitiers.app.view.grid.GridView;
import fr.univpoitiers.app.view.pad.PadView;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Controller of the main view ({@code vue.fxml}): connects the grid, the number pad,
 * the notes mode, the lock and the rule selection to the {@link SudokuCoreModel}.
 */
public class SudokuAppController {

    private static final Logger LOGGER = Logger.getLogger(SudokuAppController.class.getName());

    private static final int GRID_WIDTH = 9;
    private static final int GRID_HEIGHT = 9;
    private static final int MAX_DIGIT = Math.max(GRID_WIDTH, GRID_HEIGHT);

    private static final String CSS_PATH = "css/style.css";
    private static final String LOCKED_ICON_PATH = "images/Cadenas-ferme-rouge.png";
    private static final String UNLOCKED_ICON_PATH = "images/Cadenas-ouvert-vert.png";

    @FXML public Button checkButton;
    @FXML Label errorLabel;
    @FXML private CheckBox checkAllRules;
    @FXML private CheckBox checkLastFreeCell;
    @FXML private CheckBox checkLastRemainingCell;
    @FXML private CheckBox checkLastPossibleNumber;
    @FXML private CheckBox checkNakedSingles;
    @FXML private CheckBox checkNakedPairs;
    @FXML private CheckBox checkNakedTriples;
    @FXML private CheckBox checkNakedTuples;
    @FXML private CheckBox checkHiddenSingles;
    @FXML private CheckBox checkHiddenPairs;
    @FXML private CheckBox checkHiddenTriples;
    @FXML private CheckBox checkHiddenTuples;
    @FXML private CheckBox checkPointingPairs;
    @FXML private CheckBox checkPointingTriples;
    @FXML private CheckBox checkBoxReduction;
    @FXML private CheckBox checkYWing;
    @FXML private CheckBox checkXWing;
    @FXML private CheckBox checkSwordFish;

    @FXML private Button runAutoResolution;
    @FXML private CheckBox notesMode;
    @FXML private VBox padControls;
    @FXML private Button lockGridButton;
    @FXML private ImageView lockIcon;
    @FXML private BorderPane centerPane;

    private SudokuCoreModel coreModel;
    private GridView gridView;
    private Image lockedIcon;
    private Image unlockedIcon;

    private int selectedRow = -1;
    private int selectedColumn = -1;

    private boolean isUpdating = false;

    /**
     * Creates the model and the grid, inserts the grid and the pad into the view, then sets up
     * the notes mode, the lock display and the rule selection. Called by JavaFX after the
     * {@code @FXML} fields have been injected.
     */
    @FXML
    public void initialize() {
        coreModel = new SudokuCoreModel();
        coreModel.createGrid(GRID_WIDTH, GRID_HEIGHT);

        URL cssUrl = Objects.requireNonNull(App.class.getResource(CSS_PATH),
                "Feuille de style introuvable : " + CSS_PATH);
        gridView = new GridView(GRID_WIDTH, GRID_HEIGHT, cssUrl.toExternalForm(), this::selectCell);
        centerPane.setCenter(gridView);

        padControls.getChildren().add(0, new PadView(MAX_DIGIT, this::handleDigit));

        lockedIcon = loadIcon(LOCKED_ICON_PATH);
        unlockedIcon = loadIcon(UNLOCKED_ICON_PATH);

        notesMode.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (hasSelection()) {
                refreshCell(selectedRow, selectedColumn);
            }
        });

        setupRuleSelection();
        updateLockDisplay();
    }

    // ------------------------------------------------------------------------------------------
    // Interactions
    // ------------------------------------------------------------------------------------------

    /**
     * Selects a cell and refreshes the display of the previous and the new cell,
     * because the notes mode only applies to the selected cell.
     *
     * @param row    row of the cell, starting at 0
     * @param column column of the cell, starting at 0
     */
    private void selectCell(int row, int column) {
        int previousRow = selectedRow;
        int previousColumn = selectedColumn;
        selectedRow = row;
        selectedColumn = column;
        if (previousRow >= 0 && previousColumn >= 0) {
            refreshCell(previousRow, previousColumn);
        }
        refreshCell(row, column);
    }

    /**
     * Applies a digit from the pad to the selected cell: in notes mode, adds or removes the
     * note; otherwise, enters the value. The notes of the cell are kept. Does nothing if
     * no cell is selected or if the cell is locked.
     *
     * @param digit the chosen digit, starting at 1
     */
    private void handleDigit(int digit) {
        if (!hasSelection() || coreModel.isCellLocked(selectedColumn, selectedRow)) {
            return;
        }

        if (notesMode.isSelected()) {
            coreModel.toggleNote(digit, selectedColumn, selectedRow);
        } else if (!coreModel.input(digit, selectedColumn, selectedRow)) {
            return;
        }
        refreshCell(selectedRow, selectedColumn);
    }

    /**
     * Clears the selected cell according to the mode: its notes in notes mode, its value otherwise.
     */
    @FXML
    private void clearSelectedCell() {
        if (!hasSelection() || coreModel.isCellLocked(selectedColumn, selectedRow)) {
            return;
        }
        if (notesMode.isSelected()) {
            coreModel.clearCellNotes(selectedColumn, selectedRow);
        } else if (!coreModel.input(0, selectedColumn, selectedRow)) {
            return;
        }
        refreshCell(selectedRow, selectedColumn);
    }

    /**
     * Resets the grid and refreshes every cell and the lock display.
     */
    @FXML
    private void resetGrid() {
        coreModel.resetGrid();
        refreshAllCells();
        updateLockDisplay();
    }

    /**
     * Locks the filled cells, or unlocks the whole grid if it is already locked.
     */
    @FXML
    private void toggleGridLock() {
        if (coreModel.isGridLocked()) {
            coreModel.unlockGrid();
        } else {
            coreModel.lockFilledCells();
        }
        updateLockDisplay();
    }

    // ------------------------------------------------------------------------------------------
    // Affichage
    // ------------------------------------------------------------------------------------------

    /**
     * Updates the text and the icon of the lock button according to the state of the grid.
     */
    private void updateLockDisplay() {
        boolean locked = coreModel.isGridLocked();
        lockGridButton.setText(locked ? "LOCK" : "UNLOCK");
        Image icon = locked ? lockedIcon : unlockedIcon;
        if (icon != null) {
            lockIcon.setImage(icon);
        }
    }

    /**
     * Loads an icon from the application resources.
     *
     * @param path path relative to the {@link App} class
     * @return the loaded image, or {@code null} if the resource cannot be found
     */
    private Image loadIcon(String path) {
        URL url = App.class.getResource(path);
        if (url == null) {
            LOGGER.log(Level.WARNING, "Icône introuvable : {0}", path);
            return null;
        }
        return new Image(url.toExternalForm());
    }

    /**
     * Tells whether a cell is selected.
     *
     * @return true if a cell is selected
     */
    private boolean hasSelection() {
        return selectedRow >= 0 && selectedColumn >= 0;
    }

    /**
     * Refreshes the display of every cell of the grid.
     */
    private void refreshAllCells() {
        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int column = 0; column < GRID_WIDTH; column++) {
                refreshCell(row, column);
            }
        }
    }

    /**
     * Refreshes the display of a cell: in notes mode, the selected cell only shows its notes;
     * the other cells show their value, and their notes only when they are empty.
     *
     * @param row    row of the cell, starting at 0
     * @param column column of the cell, starting at 0
     */
    private void refreshCell(int row, int column) {
        CellView cellView = gridView.getCellAt(row, column);
        int value = coreModel.getNumber(column, row);
        boolean notesOnly = notesMode.isSelected() && row == selectedRow && column == selectedColumn;
        boolean showNotes = notesOnly || value == 0;
        cellView.setValue(notesOnly ? 0 : value);
        cellView.setNotes(showNotes ? visibleNotes(row, column) : Set.of());
    }

    /**
     * Returns the active notes of a cell.
     *
     * @param row    row of the cell, starting at 0
     * @param column column of the cell, starting at 0
     * @return the noted digits, starting at 1
     */
    private Set<Integer> visibleNotes(int row, int column) {
        Set<Integer> visible = new HashSet<>();
        for (int digit = 1; digit <= MAX_DIGIT; digit++) {
            if (coreModel.getNote(digit, column, row)) {
                visible.add(digit);
            }
        }
        return visible;
    }

    // ------------------------------------------------------------------------------------------
    // Règles de résolution
    // ------------------------------------------------------------------------------------------

    /**
     * Returns the check boxes of the individual rules (all of them except "all rules").
     *
     * @return the list of rule check boxes
     */
    private List<CheckBox> individualRules() {
        return List.of(
                checkLastFreeCell, checkLastRemainingCell, checkLastPossibleNumber,
                checkNakedSingles, checkNakedPairs, checkNakedTriples, checkNakedTuples,
                checkHiddenSingles, checkHiddenPairs, checkHiddenTriples, checkHiddenTuples,
                checkPointingPairs, checkPointingTriples, checkBoxReduction,
                checkXWing, checkYWing, checkSwordFish
        );
    }

    /**
     * Sets up the rule selection: checking "all rules" unchecks the individual rules
     * and vice versa, and the solve button stays disabled as long as
     * no rule is checked.
     */
    private void setupRuleSelection() {
        List<CheckBox> rules = individualRules();

        checkAllRules.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (isUpdating || !Boolean.TRUE.equals(newValue)) {
                return;
            }
            isUpdating = true;
            rules.forEach(rule -> rule.setSelected(false));
            isUpdating = false;
        });

        for (CheckBox rule : rules) {
            rule.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (isUpdating || !Boolean.TRUE.equals(newValue)) {
                    return;
                }
                isUpdating = true;
                checkAllRules.setSelected(false);
                isUpdating = false;
            });
        }

        List<CheckBox> allCheckBoxes = new ArrayList<>(rules);
        allCheckBoxes.add(checkAllRules);
        Observable[] dependencies = allCheckBoxes.stream()
                .map(CheckBox::selectedProperty)
                .toArray(Observable[]::new);
        runAutoResolution.disableProperty().bind(Bindings.createBooleanBinding(
                () -> allCheckBoxes.stream().noneMatch(CheckBox::isSelected),
                dependencies));
    }

    @FXML
    public void onCheckButtonClick() {
        // 1. Toujours nettoyer les anciennes cases rouges avant de revérifier
        gridView.clearErrors(); // (ou le nom de ta méthode qui remet le style normal)

        int[] invalidPositions = coreModel.checkValidValues();

        if (invalidPositions.length == 0) {
            if (coreModel.isGridComplete()) {
                errorLabel.setText("Félicitations ! La grille est complète et valide.");
            } else {
                errorLabel.setText("Aucune erreur pour le moment, continue !");
            }
        } else {
            for (int pos : invalidPositions) {
                int[] rowColumn = coreModel.getRowAndColumn(pos);
                gridView.highlightError(rowColumn[0], rowColumn[1]);
            }
            errorLabel.setText(invalidPositions.length + " erreur(s) détectée(s).");
        }
    }
}