```bash
docker build -t sudoku-app .
docker run -it --rm sudoku-app
docker compose up --build
```

mvn package
mvn compile
mvn exec:java "-Dexec.mainClass=fr.univpoitiers.App"
mvn javafx:run
mvn clean
