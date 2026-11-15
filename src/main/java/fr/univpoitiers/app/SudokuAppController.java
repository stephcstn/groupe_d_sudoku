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

/**
 * Contrôleur de la vue principale ({@code vue.fxml}) : relie la grille, le pavé numérique,
 * le mode notes, le verrouillage et le choix des règles au {@link SudokuCoreModel}.
 */
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

    /**
     * Crée le modèle et la grille, insère la grille et le pavé dans la vue, puis configure
     * le mode notes, l'affichage du verrou et le choix des règles. Appelée par JavaFX après
     * l'injection des champs {@code @FXML}.
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
     * Sélectionne une case et met à jour l'affichage de l'ancienne et de la nouvelle case,
     * car le mode notes ne s'applique qu'à la case sélectionnée.
     *
     * @param row    ligne de la case, à partir de 0
     * @param column colonne de la case, à partir de 0
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
     * Traite un chiffre du pavé sur la case sélectionnée : en mode notes, ajoute ou retire la
     * note ; sinon, saisit la valeur. Les notes de la case sont conservées. Sans effet si
     * aucune case n'est sélectionnée ou si la case est verrouillée.
     *
     * @param digit chiffre choisi, à partir de 1
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
     * Efface la case sélectionnée selon le mode : ses notes en mode notes, sa valeur sinon.
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
     * Réinitialise la grille et rafraîchit toutes les cases et l'affichage du verrou.
     */
    @FXML
    private void resetGrid() {
        coreModel.resetGrid();
        refreshAllCells();
        updateLockDisplay();
    }

    /**
     * Verrouille les cases remplies, ou déverrouille toute la grille si elle est déjà verrouillée.
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
     * Met à jour le texte et l'icône du bouton de verrouillage selon l'état de la grille.
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
     * Charge une icône depuis les ressources de l'application.
     *
     * @param path chemin relatif à la classe {@link App}
     * @return l'image chargée, ou {@code null} si la ressource est introuvable
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
     * Indique si une case est sélectionnée.
     *
     * @return vrai si une case est sélectionnée
     */
    private boolean hasSelection() {
        return selectedRow >= 0 && selectedColumn >= 0;
    }

    /**
     * Met à jour l'affichage de toutes les cases de la grille.
     */
    private void refreshAllCells() {
        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int column = 0; column < GRID_WIDTH; column++) {
                refreshCell(row, column);
            }
        }
    }

    /**
     * Met à jour l'affichage d'une case : la case sélectionnée en mode notes n'affiche que ses
     * notes ; les autres cases affichent leur valeur, et leurs notes seulement si elles sont vides.
     *
     * @param row    ligne de la case, à partir de 0
     * @param column colonne de la case, à partir de 0
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
     * Retourne les notes actives d'une case.
     *
     * @param row    ligne de la case, à partir de 0
     * @param column colonne de la case, à partir de 0
     * @return les chiffres notés, à partir de 1
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
     * Retourne les cases à cocher des règles individuelles (toutes sauf « toutes les règles »).
     *
     * @return la liste des cases à cocher des règles
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
     * Configure le choix des règles : cocher « toutes les règles » décoche les règles
     * individuelles et inversement, et le bouton de résolution reste désactivé tant
     * qu'aucune règle n'est cochée.
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
}