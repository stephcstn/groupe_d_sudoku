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

	/*
	public static SudokuAppController create(SudokuCoreModel model, AppView view) {
		SudokuAppController s = new SudokuAppController(model, view);
		return null;
	}

	*/

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