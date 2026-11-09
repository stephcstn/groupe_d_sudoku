/*
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

*/

package fr.univpoitiers.app.view.pad;

import java.util.Objects;
import java.util.function.IntConsumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

/**
 * Pavé numérique affichant un bouton par chiffre, de 1 au chiffre maximal.
 *
 * <p>Les boutons sont disposés sur {@code ceil(sqrt(nbrMax))} colonnes (3 colonnes en 9x9,
 * 4 en 16x16, 5 en 25x25). Le pavé ne connaît pas le modèle : il transmet simplement le
 * chiffre choisi au gestionnaire fourni.</p>
 *
 * <p><b>Threading :</b> le gestionnaire est appelé sur le thread JavaFX.</p>
 */
public class PadView extends GridPane {

    private static final double BUTTON_SIZE = 40.0;
    private static final double SPACING = 8.0;

    /**
     * Crée un pavé sans gestionnaire : les clics sont ignorés.
     *
     * @param nbrMax chiffre maximal du pavé, strictement positif
     * @throws IllegalArgumentException si le chiffre maximal n'est pas positif
     */
    public PadView(int nbrMax) {
        this(nbrMax, value -> { });
    }

    /**
     * Crée un pavé transmettant les chiffres choisis au gestionnaire fourni.
     *
     * @param nbrMax          chiffre maximal du pavé, strictement positif
     * @param onDigitSelected gestionnaire du chiffre choisi, non nul
     * @throws IllegalArgumentException si le chiffre maximal n'est pas positif
     * @throws NullPointerException     si le gestionnaire est nul
     */
    public PadView(int nbrMax, IntConsumer onDigitSelected) {
        if (nbrMax <= 0) {
            throw new IllegalArgumentException("Le nombre de chiffres doit être positif.");
        }
        Objects.requireNonNull(onDigitSelected, "Le gestionnaire de chiffre ne peut pas être nul.");

        setAlignment(Pos.CENTER);
        setHgap(SPACING);
        setVgap(SPACING);
        setPadding(new Insets(SPACING));

        int columns = (int) Math.ceil(Math.sqrt(nbrMax));
        for (int digit = 1; digit <= nbrMax; digit++) {
            add(createDigitButton(digit, onDigitSelected), (digit - 1) % columns, (digit - 1) / columns);
        }
    }

    /**
     * Crée le bouton associé à un chiffre.
     *
     * @param digit           chiffre affiché et transmis au clic
     * @param onDigitSelected gestionnaire appelé avec le chiffre
     * @return le bouton configuré
     */
    private Button createDigitButton(int digit, IntConsumer onDigitSelected) {
        Button button = new Button(String.valueOf(digit));
        button.setPrefSize(BUTTON_SIZE, BUTTON_SIZE);
        button.setOnAction(event -> onDigitSelected.accept(digit));
        return button;
    }
}