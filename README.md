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
