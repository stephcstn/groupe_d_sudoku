package fr.univpoitiers.app;

import fr.univpoitiers.app.view.AppView;
import fr.univpoitiers.app.view.grid.GridView;
import fr.univpoitiers.app.view.pad.PadView;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;

import java.util.List;

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
	@FXML public BorderPane rightPane;


	private SudokuCoreModel coreModel;
	private AppView appView;
	private GridView gridView;
	private SimpleIntegerProperty cellIndex;
	private SimpleIntegerProperty gridSizeMode;
	private ListProperty<Integer> listRules;
	private String cssPath;


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

		cssPath = getClass().getResource("/fr/univpoitiers/css/style.css").toExternalForm();
		gridView = new GridView(sizeW,sizeH,cssPath);
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
		padView=new PadView(sizeH);
		rightPane.setCenter(padView);







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