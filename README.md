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

### Saisie des notes

Sélectionnez une case, activez `NOTES`, puis utilisez le pavé numérique pour ajouter ou retirer des notes candidates. Désactivez `NOTES` pour saisir une valeur ; cette saisie remplace les notes de la case. `CLEAR` efface la valeur et les notes de la case sélectionnée ; `RESET GRID` efface toutes les valeurs et notes de la grille sans modifier la sélection ni le mode `NOTES`.

Le bouton `UNLOCK` verrouille les valeurs déjà saisies dans le modèle ; il devient `LOCK` et peut être recliqué pour les déverrouiller. Les nouvelles cases encore vides restent modifiables pendant ce verrouillage. `RESET GRID` efface les valeurs et réinitialise également l'état du cadenas. Les notes candidates restent provisoires dans l'interface.
