```bash
docker build -t sudoku-app .
docker run -it --rm sudoku-app
docker compose up --build
```

./mvnw package
./mvnw compile
./mvnw exec:java "-Dexec.mainClass=fr.univpoitiers.App"
./mvnw javafx:run
./mvnw clean test

Dans la grille, sélectionnez une case puis choisissez un chiffre sur le pavé pour y inscrire sa valeur. Activez le bouton `NOTES` pour ajouter ou retirer ce chiffre comme candidat dans la case sélectionnée. Le bouton `CLEAR` efface la valeur et les notes de la case sélectionnée. `RESET GRID` vide toutes les cases, efface leurs notes et les déverrouille.
