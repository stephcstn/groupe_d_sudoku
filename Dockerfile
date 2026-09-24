FROM maven:3.9-eclipse-temurin-21

WORKDIR /groupe_d_sudoku

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package