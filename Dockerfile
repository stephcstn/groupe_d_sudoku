# Étape 1 : Compilation du projet Java avec Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copie de la configuration des dépendances
COPY pom.xml .
# Téléchargement des dépendances hors ligne (mise en cache Docker)
RUN mvn dependency:go-offline -B

# Copie du code source et compilation du fichier .jar
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Exécution légère avec le JRE seul
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Récupération du .jar généré depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Configuration de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]