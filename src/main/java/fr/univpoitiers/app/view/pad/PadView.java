package fr.univpoitiers.app.view.pad;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class PadView extends GridPane {


    public PadView(int nbrMax){
        int columns = (int) Math.ceil(Math.sqrt(nbrMax)); // ex: 3 pour 9, 5 pour 25

        for (int i = 1; i <= nbrMax; i++) {
            Button btn = new Button(String.valueOf(i));
            btn.setPrefSize(40, 40); // Boutons compacts
            int val = i;
            btn.setOnAction(e ->
                    System.out.println("Clic")
            );

            int row = (i - 1) / columns;
            int col = (i - 1) % columns;
            this.add(btn, col, row);
        }

    }
}
