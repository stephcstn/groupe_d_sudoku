package fr.univpoitiers.app.view.pad;

import java.util.Objects;
import java.util.function.IntConsumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class PadView extends GridPane {

    public PadView(int nbrMax) {
        this(nbrMax, value -> { });
    }

    /**
     * Crée un pavé transmettant les chiffres choisis au gestionnaire fourni.
     *
     * @param nbrMax chiffre maximal du pavé, strictement positif
     * @param onDigitSelected gestionnaire du chiffre choisi
     * @throws IllegalArgumentException si le chiffre maximal n'est pas positif
     */
    public PadView(int nbrMax, IntConsumer onDigitSelected) {
        if (nbrMax <= 0) {
            throw new IllegalArgumentException("Le nombre de chiffres doit être positif.");
        }
        Objects.requireNonNull(onDigitSelected, "Le gestionnaire de chiffre ne peut pas être nul.");
        int columns = (int) Math.ceil(Math.sqrt(nbrMax));
        setAlignment(Pos.CENTER);
        setHgap(8.0);
        setVgap(8.0);
        setPadding(new Insets(8.0));

        for (int i = 1; i <= nbrMax; i++) {
            Button btn = new Button(String.valueOf(i));
            btn.setPrefSize(40, 40);
            int val = i;
            btn.setOnAction(event -> onDigitSelected.accept(val));

            int row = (i - 1) / columns;
            int col = (i - 1) % columns;
            add(btn, col, row);
        }
    }
}
