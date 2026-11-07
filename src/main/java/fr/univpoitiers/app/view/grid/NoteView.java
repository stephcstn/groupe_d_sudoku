package fr.univpoitiers.app.view.grid;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/** Vue d'une note affichée dans une cellule de la grille. */
public class NoteView extends BorderPane {

	private final Label labelNote;

	/**
	 * Crée une note initialement masquée.
	 *
	 * @param value Valeur affichée lorsque la note est active.
	 */
	public NoteView(int value) {
		this.labelNote = new Label(String.valueOf(value));
		this.labelNote.getStyleClass().add("note-label");
		this.setCenter(labelNote);
		this.getStyleClass().add("note-view");
		setNoteActive(false);
	}

	/**
	 * Affiche ou masque la note et adapte sa participation à la mise en page.
	 *
	 * @param active Vrai pour afficher la note.
	 */
	public void setNoteActive(boolean active) {
		labelNote.setVisible(active);
		labelNote.setManaged(active);
	}
}
