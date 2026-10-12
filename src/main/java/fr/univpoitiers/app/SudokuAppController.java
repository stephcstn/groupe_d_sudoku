package fr.univpoitiers.app;

import fr.univpoitiers.app.view.AppView;
import fr.univpoitiers.app.view.grid.GridView;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class SudokuAppController {

	private SudokuCoreModel coreModel;
	private AppView appView;
	private GridView gridView;
	private SimpleIntegerProperty cellIndex;
	private SimpleIntegerProperty gridSizeMode;
	private ListProperty<Integer> listRules;
	private String cssPath;



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
		gridView = new GridView(9,9,cssPath);
		centerPane.getChildren().add(gridView);



    }

}