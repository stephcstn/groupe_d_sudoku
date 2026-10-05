package fr.univpoitiers.app.view;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Button;

public class AppView {

	private javafx.beans.property.SimpleIntegerProperty cellIndex;
	private javafx.beans.property.SimpleIntegerProperty gridSizeMode;
	private javafx.beans.property.ListProperty<Integer> listRules;

	/**
	 * 
	 * @param cellIndex
	 * @param gridSizeMode
	 * @param listRules
	 */
	private AppView(javafx.beans.property.SimpleIntegerProperty cellIndex, javafx.beans.property.SimpleIntegerProperty gridSizeMode, javafx.beans.property.ListProperty<Boolean> listRules) {
		// TODO - implement AppView.AppView
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param cellIndex
	 * @param gridSizeMode
	 * @param listRules
	 */
	public static AppView create(javafx.beans.property.SimpleIntegerProperty cellIndex, javafx.beans.property.SimpleIntegerProperty gridSizeMode, javafx.beans.property.ListProperty<Boolean> listRules) {
		return null;
	}

	public SimpleIntegerProperty cellIndexProperty() {
		// TODO - implement AppView.cellIndexProperty
		throw new UnsupportedOperationException();
	}

	public SimpleIntegerProperty gridSizeModeProperty() {
		// TODO - implement AppView.gridSizeModeProperty
		throw new UnsupportedOperationException();
	}

	public ListProperty<Boolean> listRuleProperty() {
		// TODO - implement AppView.listRuleProperty
		throw new UnsupportedOperationException();
	}

	public Button getSolve() {
		// TODO - implement AppView.getSolve
		throw new UnsupportedOperationException();
	}

	public Button getCheckEntries() {
		// TODO - implement AppView.getCheckEntries
		throw new UnsupportedOperationException();
	}

	public Button getResetGrid() {
		// TODO - implement AppView.getResetGrid
		throw new UnsupportedOperationException();
	}

	public Button getClearGrid() {
		// TODO - implement AppView.getClearGrid
		throw new UnsupportedOperationException();
	}

}