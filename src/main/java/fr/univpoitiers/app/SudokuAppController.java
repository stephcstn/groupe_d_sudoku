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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class SudokuAppController {

    private static final Logger LOGGER = Logger.getLogger(SudokuAppController.class.getName());

    private static final int GRID_WIDTH = 9;
    private static final int GRID_HEIGHT = 9;
    private static final int MAX_DIGIT = Math.max(GRID_WIDTH, GRID_HEIGHT);

    private static final String CSS_PATH = "css/style.css";
    private static final String LOCKED_ICON_PATH = "images/Cadenas-ferme-rouge.png";
    private static final String UNLOCKED_ICON_PATH = "images/Cadenas-ouvert-vert.png";

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

        setupRuleSelection();
        updateLockDisplay();
    }

    // ------------------------------------------------------------------------------------------
    // Interactions
    // ------------------------------------------------------------------------------------------

    private void selectCell(int row, int column) {
        selectedRow = row;
        selectedColumn = column;
    }

    private void handleDigit(int digit) {
        if (!hasSelection() || coreModel.isCellLocked(selectedColumn, selectedRow)) {
            return;
        }

        if (notesMode.isSelected()) {
            if (coreModel.getNumber(selectedColumn, selectedRow) != 0) {
                return;
            }
            coreModel.toggleNote(digit, selectedColumn, selectedRow);
        } else {
            if (!coreModel.input(digit, selectedColumn, selectedRow)) {
                return;
            }
            coreModel.clearCellNotes(selectedColumn, selectedRow);
        }
        refreshCell(selectedRow, selectedColumn);
    }

    @FXML
    private void clearSelectedCell() {
        if (!hasSelection() || coreModel.isCellLocked(selectedColumn, selectedRow)) {
            return;
        }
        if (!coreModel.input(0, selectedColumn, selectedRow)) {
            return;
        }
        coreModel.clearCellNotes(selectedColumn, selectedRow);
        refreshCell(selectedRow, selectedColumn);
    }

    @FXML
    private void resetGrid() {
        coreModel.resetGrid();
        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int column = 0; column < GRID_WIDTH; column++) {
                refreshCell(row, column);
            }
        }
        updateLockDisplay();
    }

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

    private void updateLockDisplay() {
        boolean locked = coreModel.isGridLocked();
        lockGridButton.setText(locked ? "LOCK" : "UNLOCK");
        Image icon = locked ? lockedIcon : unlockedIcon;
        if (icon != null) {
            lockIcon.setImage(icon);
        }
    }

    private Image loadIcon(String path) {
        URL url = App.class.getResource(path);
        if (url == null) {
            LOGGER.log(Level.WARNING, "Icône introuvable : {0}", path);
            return null;
        }
        return new Image(url.toExternalForm());
    }

    private boolean hasSelection() {
        return selectedRow >= 0 && selectedColumn >= 0;
    }

    private void refreshCell(int row, int column) {
        CellView cellView = gridView.getCellAt(row, column);
        cellView.setValue(coreModel.getNumber(column, row));
        cellView.setNotes(visibleNotes(row, column));
    }

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

    private List<CheckBox> individualRules() {
        return List.of(
                checkLastFreeCell, checkLastRemainingCell, checkLastPossibleNumber,
                checkNakedSingles, checkNakedPairs, checkNakedTriples, checkNakedTuples,
                checkHiddenSingles, checkHiddenPairs, checkHiddenTriples, checkHiddenTuples,
                checkPointingPairs, checkPointingTriples, checkBoxReduction,
                checkXWing, checkYWing, checkSwordFish
        );
    }

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
}