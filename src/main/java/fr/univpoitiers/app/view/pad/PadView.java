package fr.univpoitiers.app.view.pad;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/** Pavé de saisie des nombres et commande du mode notes. */
public class PadView extends GridPane {

	/**
	 * Construit les boutons numériques et le bouton de mode notes.
	 *
	 * @param nbrMax Plus grand nombre proposé, à partir de 1.
	 * @param onNumberSelected Action appelée avec le nombre sélectionné.
	 * @param onNotesModeChanged Action appelée avec le nouvel état du mode notes.
	 */
	public PadView(int nbrMax, IntConsumer onNumberSelected, Consumer<Boolean> onNotesModeChanged) {
		this.setAlignment(Pos.CENTER);
		this.setHgap(4);
		this.setVgap(4);
		int columns = (int) Math.ceil(Math.sqrt(nbrMax)); // ex: 3 pour 9, 5 pour 25
		ToggleButton notesMode = new ToggleButton("NOTES");
		notesMode.setMaxWidth(Double.MAX_VALUE);
		notesMode.setOnAction(event -> onNotesModeChanged.accept(notesMode.isSelected()));
		this.add(notesMode, 0, 0, columns, 1);

		for (int i = 1; i <= nbrMax; i++) {
			Button btn = new Button(String.valueOf(i));
			btn.setPrefSize(40, 40); // Boutons compacts
			int val = i;
			btn.setOnAction(event -> onNumberSelected.accept(val));

			int row = (i - 1) / columns + 1;
			int col = (i - 1) % columns;
			this.add(btn, col, row);
        }

    }
}
