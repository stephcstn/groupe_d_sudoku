package fr.univpoitiers.app.view.pad;

import java.util.Objects;
import java.util.function.IntConsumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

/**
 * Number pad showing one button per digit, from 1 to the maximum digit.
 *
 * <p>The buttons are laid out on {@code ceil(sqrt(nbrMax))} columns (3 columns for 9x9,
 * 4 for 16x16, 5 for 25x25). The pad does not know the model: it simply passes the
 * chosen digit on to the given handler.</p>
 *
 * <p><b>Threading:</b> the handler is called on the JavaFX thread.</p>
 */
public class PadView extends GridPane {

    private static final double BUTTON_SIZE = 40.0;
    private static final double SPACING = 8.0;

    /**
     * Creates a pad without a handler: clicks are ignored.
     *
     * @param nbrMax maximum digit of the pad, strictly positive
     * @throws IllegalArgumentException if the maximum digit is not positive
     */
    public PadView(int nbrMax) {
        this(nbrMax, value -> { });
    }

    /**
     * Creates a pad that passes the chosen digits on to the given handler.
     *
     * @param nbrMax          maximum digit of the pad, strictly positive
     * @param onDigitSelected handler of the chosen digit, not null
     * @throws IllegalArgumentException if the maximum digit is not positive
     * @throws NullPointerException     if the handler is null
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
     * Creates the button associated with a digit.
     *
     * @param digit           digit shown on the button and passed on when clicked
     * @param onDigitSelected handler called with the digit
     * @return the configured button
     */
    private Button createDigitButton(int digit, IntConsumer onDigitSelected) {
        Button button = new Button(String.valueOf(digit));
        button.setPrefSize(BUTTON_SIZE, BUTTON_SIZE);
        button.setOnAction(event -> onDigitSelected.accept(digit));
        return button;
    }
}