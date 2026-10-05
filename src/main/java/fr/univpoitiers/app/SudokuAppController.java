package fr.univpoitiers.app;

import fr.univpoitiers.app.view.AppView;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class SudokuAppController {

	private SudokuCoreModel coreModel;
	private AppView appView;
	private SimpleIntegerProperty cellIndex;
	private SimpleIntegerProperty gridSizeMode;
	private ListProperty<Integer> listRules;

	private SudokuAppController() {
		// TODO - implement SudokuAppController.SudokuAppController
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param model
	 * @param view
	 */
	public static SudokuAppController create(SudokuCoreModel model, AppView view) {
		return null;

	}

	private void buildListener() {
		// TODO - implement SudokuAppController.buildListener
		throw new UnsupportedOperationException();
	}

}