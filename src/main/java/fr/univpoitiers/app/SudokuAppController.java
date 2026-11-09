package fr.univpoitiers.app;
/*
package fr.univpoitiers.app;

import fr.univpoitiers.app.view.AppView;
import fr.univpoitiers.app.view.grid.GridView;
import fr.univpoitiers.app.view.pad.PadView;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SudokuAppController {
	private final int sizeH=9;
	private final int sizeW=9;


	//RULES
	@FXML public CheckBox checkAllRules;
	@FXML public CheckBox checkLastFreeCell;
	@FXML public CheckBox checkLastRemainingCell;
	@FXML public CheckBox checkLastPossibleNumber;
	@FXML public CheckBox checkNakedSingles;
	@FXML public CheckBox checkNakedPairs;
	@FXML public CheckBox checkNakedTriples;
	@FXML public CheckBox checkNakedTuples;
	@FXML public CheckBox checkHiddenSingles;
	@FXML public CheckBox checkHiddenPairs;
	@FXML public CheckBox checkHiddenTriples;
	@FXML public CheckBox checkHiddenTuples;
	@FXML public CheckBox checkPointingPairs;
	@FXML public CheckBox checkPointingTriples;
	@FXML public CheckBox checkBoxReduction;
	@FXML public CheckBox checkX_wing;
	@FXML public CheckBox checkY_wing;
	@FXML public CheckBox checkSword_fish;

	@FXML public Button runAutoResolution;
	@FXML private CheckBox notesMode;
	@FXML private VBox padControls;
	@FXML private Button lockGridButton;
	@FXML private ImageView lockIcon;


	private SudokuCoreModel coreModel;
	private AppView appView;
	private GridView gridView;
	private String cssPath;
	private final boolean[][][] notes = new boolean[sizeH][sizeW][Math.max(sizeH, sizeW) + 1];
	private int selectedRow = -1;
	private int selectedColumn = -1;


	// Indicateur pour éviter une boucle d'événements infinie lors des mises à jour automatiques
	private boolean isUpdating = false;

	private PadView padView;


	@FXML
	private BorderPane centerPane;
/*
	private SudokuAppController(SudokuCoreModel model, AppView view) {
		// TODO - implement SudokuAppController.SudokuAppController
		throw new UnsupportedOperationException();
	}*/
/*
	/**
	 * 
	 * @param model
	 * @param view
	 */

import fr.univpoitiers.app.SudokuCoreModel;
import fr.univpoitiers.core.grid.Grid;

/*
	public static SudokuAppController create(SudokuCoreModel model, AppView view) {
		SudokuAppController s = new SudokuAppController(model, view);
		return null;
	}



	private void buildListener() {
		// TODO - implement SudokuAppController.buildListener
		throw new UnsupportedOperationException();
	}

    @FXML
    public void initialize() {

		coreModel = new SudokuCoreModel();
		coreModel.createGrid(sizeW, sizeH);
		cssPath = getClass().getResource("/fr/univpoitiers/css/style.css").toExternalForm();
		gridView = new GridView(sizeW, sizeH, cssPath, this::selectCell);
		centerPane.setCenter(gridView);
		List<CheckBox> ruleList = initAllRules();

		checkAllRules.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (isUpdating) return; // Empêche la récursivité

			if (newValue) { // Si checkAllRules vient d'être coché
				isUpdating = true;
				for (CheckBox cb : ruleList) {
					cb.setSelected(false); // Décoche toutes les règles individuelles
				}
				isUpdating = false;
			}
		});

		for (CheckBox cb : ruleList) {
			cb.selectedProperty().addListener((observable, oldValue, newValue) -> {
				if (isUpdating) return; // Empêche la récursivité

				if (newValue) { // Dès qu'une règle individuelle est cochée
					isUpdating = true;
					checkAllRules.setSelected(false); // Décoche "checkAllRules"
					isUpdating = false;
				}
			});
		}

		bindAutoResolvedButton();
		padView = new PadView(sizeH, this::handleDigit);
		padControls.getChildren().add(0, padView);
		updateLockDisplay();
	}

	private void selectCell(int row, int column) {
		selectedRow = row;
		selectedColumn = column;
	}

	private void handleDigit(int digit) {
		if (selectedRow < 0 || selectedColumn < 0) {
			return;
		}

		if (notesMode.isSelected()) {
			if (coreModel.getNumber(selectedColumn, selectedRow) != 0
					|| coreModel.isCellLocked(selectedColumn, selectedRow)) {
				return;
			}
			notes[selectedRow][selectedColumn][digit] = !notes[selectedRow][selectedColumn][digit];
		} else {
			if (!coreModel.input(digit, selectedColumn, selectedRow)) {
				return;
			}
			Arrays.fill(notes[selectedRow][selectedColumn], false);
		}
		refreshSelectedCell();
	}

	@FXML
	private void clearSelectedCell() {
		if (selectedRow < 0 || selectedColumn < 0) {
			return;
		}
		if (!coreModel.input(0, selectedColumn, selectedRow)) {
			return;
		}
		Arrays.fill(notes[selectedRow][selectedColumn], false);
		refreshSelectedCell();
	}

	@FXML
	private void resetGrid() {
		coreModel.resetGrid();
		for (int row = 0; row < sizeH; row++) {
			for (int column = 0; column < sizeW; column++) {
				Arrays.fill(notes[row][column], false);
				gridView.getCellAt(row, column).setValue(0);
				gridView.getCellAt(row, column).setNotes(Set.of());
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

	private void updateLockDisplay() {
		boolean locked = coreModel.isGridLocked();
		lockGridButton.setText(locked ? "LOCK" : "UNLOCK");
		String imagePath = locked
				? "/fr/univpoitiers/images/Cadenas-ferme-rouge.png"
				: "/fr/univpoitiers/images/Cadenas-ouvert-vert.png";
		String imageUrl = Objects.requireNonNull(getClass().getResource(imagePath)).toExternalForm();
		lockIcon.setImage(new Image(imageUrl));
	}

	private void refreshSelectedCell() {
		Set<Integer> visibleNotes = new HashSet<>();
		for (int value = 1; value < notes[selectedRow][selectedColumn].length; value++) {
			if (notes[selectedRow][selectedColumn][value]) {
				visibleNotes.add(value);
			}
		}
		gridView.getCellAt(selectedRow, selectedColumn)
				.setValue(coreModel.getNumber(selectedColumn, selectedRow));
		gridView.getCellAt(selectedRow, selectedColumn).setNotes(visibleNotes);
	}





	// Désactive le bouton si AUCUNE CheckBox n'est cochée

	private void bindAutoResolvedButton(){
		runAutoResolution.disableProperty().bind(
				Bindings.createBooleanBinding(() ->
								!checkAllRules.isSelected() &&
										!checkLastFreeCell.isSelected() &&
										!checkLastRemainingCell.isSelected() &&
										!checkLastPossibleNumber.isSelected() &&
										!checkNakedSingles.isSelected() &&
										!checkNakedPairs.isSelected() &&
										!checkNakedTriples.isSelected() &&
										!checkNakedTuples.isSelected() &&
										!checkHiddenSingles.isSelected() &&
										!checkHiddenPairs.isSelected() &&
										!checkHiddenTriples.isSelected() &&
										!checkHiddenTuples.isSelected() &&
										!checkPointingPairs.isSelected() &&
										!checkPointingTriples.isSelected() &&
										!checkBoxReduction.isSelected() &&
										!checkX_wing.isSelected() &&
										!checkY_wing.isSelected() &&
										!checkSword_fish.isSelected(),

						// Propriétés à écouter pour redéclencher la vérification
						checkAllRules.selectedProperty(),
						checkLastFreeCell.selectedProperty(),
						checkLastRemainingCell.selectedProperty(),
						checkLastPossibleNumber.selectedProperty(),
						checkNakedSingles.selectedProperty(),
						checkNakedPairs.selectedProperty(),
						checkNakedTriples.selectedProperty(),
						checkNakedTuples.selectedProperty(),
						checkHiddenSingles.selectedProperty(),
						checkHiddenPairs.selectedProperty(),
						checkHiddenTriples.selectedProperty(),
						checkHiddenTuples.selectedProperty(),
						checkPointingPairs.selectedProperty(),
						checkPointingTriples.selectedProperty(),
						checkBoxReduction.selectedProperty(),
						checkX_wing.selectedProperty(),
						checkY_wing.selectedProperty(),
						checkSword_fish.selectedProperty()
				)
		);

	}

	// Regrouper toutes les règles dans une liste
	private List<CheckBox> initAllRules(){
		return List.of(
				checkLastFreeCell, checkLastRemainingCell, checkLastPossibleNumber,
				checkNakedSingles, checkNakedPairs, checkNakedTriples, checkNakedTuples,
				checkHiddenSingles, checkHiddenPairs, checkHiddenTriples, checkHiddenTuples,
				checkPointingPairs, checkPointingTriples, checkBoxReduction,
				checkX_wing, checkY_wing, checkSword_fish
		);
	}
}

/*
package fr.univpoitiers.app;

import fr.univpoitiers.core.grid.Grid;
import fr.univpoitiers.core.solver.Solver;

/**
 * Façade du modèle métier utilisée par le contrôleur JavaFX.
 *
 * <p>Convention de coordonnées : toutes les méthodes publiques prennent des coordonnées
 * 2D {@code (x, y)} où {@code x} est la <b>colonne</b> et {@code y} la <b>ligne</b>.
 * La {@link Grid} est un tableau à une dimension : la conversion {@code (x, y) -> index}
 * est faite à un seul endroit, {@link #toIndex(int, int)}, via {@link Grid#cellToIndex(int, int)}.</p>
 *
 * <p>Attention : {@code GridView.getCellAt(row, column)} utilise l'ordre inverse
 * {@code (ligne, colonne)}. C'est au contrôleur de faire la correspondance.</p>
 */


import fr.univpoitiers.app.view.grid.CellView;
import fr.univpoitiers.app.view.grid.GridView;
import fr.univpoitiers.app.view.pad.PadView;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
 * Contrôleur principal de l'application : relie la vue FXML ({@link GridView}, {@link PadView})
 * au modèle ({@link SudokuCoreModel}).
 *
 * <p><b>Coordonnées.</b> La vue est un {@code GridPane} 2D adressé par {@code (row, column)}.
 * Le modèle, lui, stocke la grille à une dimension et {@link SudokuCoreModel#input(int, int, int)}
 * et {@link SudokuCoreModel#getNumber(int, int)} utilisent actuellement leur premier paramètre
 * de position directement comme index 1D. Toute la conversion {@code (row, column)} vers index 1D
 * est isolée dans {@link #toModelPosition(int, int)}, {@link #modelInput(int, int, int)} et
 * {@link #modelValue(int, int)} : c'est le seul endroit à adapter si le modèle change.</p>
 *
 * <p><b>Verrouillage.</b> Le modèle ne permet ni de savoir si une case est verrouillée, ni de la
 * protéger, ni de la déverrouiller. Le contrôleur mémorise donc lui-même les cases verrouillées
 * ({@link #lockedCells}) et refuse de les modifier.</p>
 *
 * <p><b>Threading :</b> toutes les méthodes s'exécutent sur le thread JavaFX.</p>
 */
public class SudokuAppController {

	private static final Logger LOGGER = Logger.getLogger(SudokuAppController.class.getName());

	private static final int GRID_WIDTH = 9;
	private static final int GRID_HEIGHT = 9;
	private static final int MAX_DIGIT = Math.max(GRID_WIDTH, GRID_HEIGHT);

	private static final String CSS_PATH = "/fr/univpoitiers/css/style.css";
	private static final String LOCKED_ICON_PATH = "/fr/univpoitiers/images/Cadenas-ferme-rouge.png";
	private static final String UNLOCKED_ICON_PATH = "/fr/univpoitiers/images/Cadenas-ouvert-vert.png";

	// Règles de résolution
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
	@FXML private CheckBox checkX_wing;
	@FXML private CheckBox checkY_wing;
	@FXML private CheckBox checkSword_fish;

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

	/**
	 * Notes candidates saisies par l'utilisateur, indexées {@code [row][column][digit]}
	 * avec {@code digit} de 1 à {@link #MAX_DIGIT} (l'indice 0 est inutilisé).
	 */
	private final boolean[][][] notes = new boolean[GRID_HEIGHT][GRID_WIDTH][MAX_DIGIT + 1];

	/** Cases verrouillées, indexées {@code [row][column]}. */
	private final boolean[][] lockedCells = new boolean[GRID_HEIGHT][GRID_WIDTH];

	private int selectedRow = -1;
	private int selectedColumn = -1;

	/** Évite une boucle d'événements lors des mises à jour automatiques des cases à cocher. */
	private boolean isUpdating = false;

	/**
	 * Initialise le modèle, la grille, le pavé numérique, les règles et l'icône de verrouillage.
	 * Appelée automatiquement par le {@code FXMLLoader} après l'injection des champs.
	 *
	 * @throws NullPointerException si la feuille de style est introuvable
	 */
	@FXML
	public void initialize() {
		coreModel = new SudokuCoreModel();
		coreModel.createGrid(GRID_WIDTH, GRID_HEIGHT);

		URL cssUrl = Objects.requireNonNull(getClass().getResource(CSS_PATH),
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
	// Adaptation au modèle (seul endroit où (row, column) devient un index 1D)
	// ------------------------------------------------------------------------------------------

	/**
	 * Convertit une position de la vue 2D en index 1D de la grille du modèle.
	 *
	 * @param row    ligne de la cellule
	 * @param column colonne de la cellule
	 * @return l'index 1D, {@code row * GRID_WIDTH + column}
	 */
	private static int toModelPosition(int row, int column) {
		return row * GRID_WIDTH + column;
	}

	/**
	 * Saisit une valeur dans le modèle. Le modèle lit son premier paramètre de position comme
	 * index 1D et ignore le second, qui est donc passé à 0.
	 *
	 * @param value  valeur à saisir, 0 pour vider la case
	 * @param row    ligne de la cellule
	 * @param column colonne de la cellule
	 * @return le résultat de {@link SudokuCoreModel#input(int, int, int)}
	 */
	private boolean modelInput(int value, int row, int column) {
		return coreModel.input(value, toModelPosition(row, column), 0);
	}

	/**
	 * Lit la valeur d'une cellule dans le modèle (voir {@link #modelInput(int, int, int)}).
	 *
	 * @param row    ligne de la cellule
	 * @param column colonne de la cellule
	 * @return la valeur, ou 0 si la cellule est vide
	 */
	private int modelValue(int row, int column) {
		return coreModel.getNumber(toModelPosition(row, column), 0);
	}

	// ------------------------------------------------------------------------------------------
	// Interactions
	// ------------------------------------------------------------------------------------------

	/**
	 * Mémorise la cellule sélectionnée dans la vue.
	 *
	 * @param row    ligne de la cellule
	 * @param column colonne de la cellule
	 */
	private void selectCell(int row, int column) {
		selectedRow = row;
		selectedColumn = column;
	}

	/**
	 * Applique un chiffre du pavé à la cellule sélectionnée : bascule la note en mode notes,
	 * sinon saisit la valeur. Ignoré si aucune cellule n'est sélectionnée, si elle est verrouillée,
	 * ou (en mode notes) si elle contient déjà une valeur.
	 *
	 * @param digit chiffre choisi, de 1 à {@link #MAX_DIGIT}
	 */
	private void handleDigit(int digit) {
		if (!hasSelection() || lockedCells[selectedRow][selectedColumn]) {
			return;
		}

		if (notesMode.isSelected()) {
			if (modelValue(selectedRow, selectedColumn) != 0) {
				return;
			}
			notes[selectedRow][selectedColumn][digit] = !notes[selectedRow][selectedColumn][digit];
		} else {
			if (!modelInput(digit, selectedRow, selectedColumn)) {
				return;
			}
			Arrays.fill(notes[selectedRow][selectedColumn], false);
		}
		refreshCell(selectedRow, selectedColumn);
	}

	/**
	 * Vide la valeur et les notes de la cellule sélectionnée, sauf si elle est verrouillée.
	 */
	@FXML
	private void clearSelectedCell() {
		if (!hasSelection() || lockedCells[selectedRow][selectedColumn]) {
			return;
		}
		if (!modelInput(0, selectedRow, selectedColumn)) {
			return;
		}
		Arrays.fill(notes[selectedRow][selectedColumn], false);
		refreshCell(selectedRow, selectedColumn);
	}

	/**
	 * Remet toute la grille à zéro (valeurs, notes et verrouillage) et rafraîchit la vue.
	 */
	@FXML
	private void resetGrid() {
		coreModel.resetGrid();
		for (int row = 0; row < GRID_HEIGHT; row++) {
			Arrays.fill(lockedCells[row], false);
			for (int column = 0; column < GRID_WIDTH; column++) {
				Arrays.fill(notes[row][column], false);
				refreshCell(row, column);
			}
		}
		updateLockDisplay();
	}

	/**
	 * Verrouille les cases remplies si la grille est déverrouillée, et inversement.
	 */
	@FXML
	private void toggleGridLock() {
		if (coreModel.isGridLocked()) {
			coreModel.unlockGrid();
			for (boolean[] row : lockedCells) {
				Arrays.fill(row, false);
			}
		} else {
			coreModel.lockFilledCells();
			for (int row = 0; row < GRID_HEIGHT; row++) {
				for (int column = 0; column < GRID_WIDTH; column++) {
					lockedCells[row][column] = modelValue(row, column) != 0;
				}
			}
		}
		updateLockDisplay();
	}

	// ------------------------------------------------------------------------------------------
	// Affichage
	// ------------------------------------------------------------------------------------------

	/**
	 * Met à jour le libellé du bouton et l'icône selon l'état de verrouillage du modèle.
	 * Si une icône est introuvable, l'image précédente est conservée.
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
	 * Charge une icône depuis le classpath.
	 *
	 * @param path chemin de la ressource
	 * @return l'image, ou {@code null} (avec un avertissement) si la ressource est introuvable
	 */
	private Image loadIcon(String path) {
		URL url = getClass().getResource(path);
		if (url == null) {
			LOGGER.warning("Icône introuvable : " + path);
			return null;
		}
		return new Image(url.toExternalForm());
	}

	/**
	 * Indique si une cellule est actuellement sélectionnée.
	 *
	 * @return vrai si une cellule a été sélectionnée
	 */
	private boolean hasSelection() {
		return selectedRow >= 0 && selectedColumn >= 0;
	}

	/**
	 * Synchronise l'affichage d'une cellule (valeur du modèle et notes du contrôleur).
	 *
	 * @param row    ligne de la cellule
	 * @param column colonne de la cellule
	 */
	private void refreshCell(int row, int column) {
		CellView cellView = gridView.getCellAt(row, column);
		cellView.setValue(modelValue(row, column));
		cellView.setNotes(visibleNotes(row, column));
	}

	/**
	 * Retourne l'ensemble des notes actives d'une cellule.
	 *
	 * @param row    ligne de la cellule
	 * @param column colonne de la cellule
	 * @return les chiffres de 1 à {@link #MAX_DIGIT} dont la note est active
	 */
	private Set<Integer> visibleNotes(int row, int column) {
		Set<Integer> visible = new HashSet<>();
		for (int digit = 1; digit <= MAX_DIGIT; digit++) {
			if (notes[row][column][digit]) {
				visible.add(digit);
			}
		}
		return visible;
	}

	// ------------------------------------------------------------------------------------------
	// Règles de résolution
	// ------------------------------------------------------------------------------------------

	/**
	 * Regroupe les cases à cocher de règles individuelles.
	 *
	 * @return la liste des règles, sans la case « ALL RULES »
	 */
	private List<CheckBox> individualRules() {
		return List.of(
				checkLastFreeCell, checkLastRemainingCell, checkLastPossibleNumber,
				checkNakedSingles, checkNakedPairs, checkNakedTriples, checkNakedTuples,
				checkHiddenSingles, checkHiddenPairs, checkHiddenTriples, checkHiddenTuples,
				checkPointingPairs, checkPointingTriples, checkBoxReduction,
				checkX_wing, checkY_wing, checkSword_fish
		);
	}

	/**
	 * Configure l'exclusion mutuelle entre « ALL RULES » et les règles individuelles, puis lie
	 * l'état du bouton de résolution automatique : il est désactivé si aucune case n'est cochée.
	 */
	private void setupRuleSelection() {
		List<CheckBox> rules = individualRules();

		checkAllRules.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (isUpdating || !newValue) {
				return;
			}
			isUpdating = true;
			rules.forEach(rule -> rule.setSelected(false));
			isUpdating = false;
		});

		for (CheckBox rule : rules) {
			rule.selectedProperty().addListener((observable, oldValue, newValue) -> {
				if (isUpdating || !newValue) {
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