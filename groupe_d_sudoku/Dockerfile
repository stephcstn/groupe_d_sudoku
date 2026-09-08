# Étape 1 : Build
FROM maven:3.8-eclipse-temurin-11 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Exécution sécurisée
FROM eclipse-temurin:11-jre

WORKDIR /app

# Création d'un groupe et d'un utilisateur non-root (appuser)
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Copie du jar en donnant la propriété à appuser
COPY --from=build --chown=appuser:appgroup /app/target/*.jar app.jar

# Bascule sur l'utilisateur non privilégié (résout docker:S6471)
USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]