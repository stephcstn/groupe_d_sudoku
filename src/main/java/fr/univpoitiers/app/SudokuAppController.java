package fr.univpoitiers.app;

import fr.univpoitiers.App;
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

	// Ressources relatives au package de {@link App} (fr/univpoitiers).
	private static final String CSS_PATH = "css/style.css";
	private static final String LOCKED_ICON_PATH = "images/Cadenas-ferme-rouge.png";
	private static final String UNLOCKED_ICON_PATH = "images/Cadenas-ouvert-vert.png";

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
		URL url = App.class.getResource(path);
		if (url == null) {
			LOGGER.log(Level.WARNING, "Icône introuvable : {0}", path);
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
				checkXWing, checkYWing, checkSwordFish
		);
	}

	/**
	 * Configure l'exclusion mutuelle entre « ALL RULES » et les règles individuelles, puis lie
	 * l'état du bouton de résolution automatique : il est désactivé si aucune case n'est cochée.
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