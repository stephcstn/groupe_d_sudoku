# Sudoku

[![Java](https://img.shields.io/badge/Java-21-blue)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/build-Maven-C71A36)](https://maven.apache.org/)
[![CI](https://github.com/stephcstn/groupe_d_sudoku/actions/workflows/maven.yml/badge.svg)](https://github.com/stephcstn/groupe_d_sudoku/actions/workflows/maven.yml)

Projet universitaire de Sudoku réalisé en équipe dans le cadre de l’UE **Génie Logiciel**. Le dépôt contient un socle Java 21, un modèle de grille et de cellules, des tests JUnit 5 et une interface JavaFX en cours de développement.

> **État du projet :** le dépôt est un prototype académique. L’interface actuelle affiche un écran d’accueil minimal et les composants de résolution, génération et contrôleur ne sont pas encore fonctionnels. Les fonctionnalités décrites comme objectifs ci-dessous ne doivent donc pas être considérées comme livrées.

## Contexte et objectifs

Le projet est mené par une équipe de cinq personnes afin de mettre en pratique les principes du génie logiciel : découpage en composants, conception d’un modèle métier, tests, automatisation du build et collaboration avec Git.

Le domaine fonctionnel visé comprend :

- la représentation d’une grille et de ses cellules ;
- la saisie et la vérification des valeurs selon les contraintes du Sudoku ;
- la résolution et, à terme, la génération de grilles ;
- des règles de résolution ;
- une interface permettant d’interagir avec la grille.

Ces éléments définissent le périmètre visé. Dans l’état actuel du dépôt, seuls certains éléments de base du modèle de grille et de cellule sont implémentés ; les autres restent à réaliser.

## Fonctionnalités et état d’avancement

| Domaine | État observé dans le dépôt |
|---|---|
| Modèle de cellule | Implémenté : valeur, verrouillage et notes candidates |
| Modèle de grille | Partiellement implémenté : dimensions, accès aux cellules, copie, remise à zéro et opérations sur les notes |
| Interface graphique | JavaFX avec chargement FXML ; la vue actuelle affiche « Hello World ! » |
| Contrôleur et modèle applicatif | Squelettes avec méthodes non implémentées |
| Validation des règles | Non finalisée ; `checkValidValues` n’est pas implémentée |
| Résolution et génération | Non implémentées ; le solveur et les stratégies sont des squelettes |

## Architecture logicielle

Le code est organisé en trois zones principales sous `fr.univpoitiers` :

- `app` : orchestration applicative envisagée, avec `SudokuAppController` et `SudokuCoreModel` ;
- `app.view` : façade de vue JavaFX (`AppView`) ;
- `core` : logique métier, séparée en `grid`, `rules` et `solver`.

L’organisation suit une **intention de séparation de type MVC** : la vue JavaFX, le contrôleur applicatif et le modèle métier sont placés dans des composants distincts. Cette architecture n’est toutefois pas achevée : les classes applicatives contiennent encore des méthodes non implémentées et la vue FXML n’est pas encore reliée à un jeu complet.

### Choix de conception visibles

| Élément | Rôle et justification |
|---|---|
| Séparation `app` / `core` | Isole le domaine Sudoku de l’interface et facilite l’évolution indépendante des deux parties. |
| `Cell` et `Grid` | Modélisent les données du jeu. La grille possède un constructeur de copie, utile pour créer des états indépendants lors d’une future recherche de solution. |
| `RuleApplier` | Définit un point de regroupement pour les stratégies de résolution envisagées. Les méthodes présentes ne contiennent pas encore les algorithmes. |
| Méthodes statiques `create` | Des points de construction sont prévus pour certaines classes d’application ; ils ne constituent pas encore une fabrique fonctionnelle, leurs implémentations étant incomplètes. |

Aucun usage achevé de patrons tels qu’Observer, Strategy ou Singleton n’est établi dans le code actuel ; ils ne sont donc pas présentés comme des choix déjà réalisés.

### Algorithmes

La résolution et la génération sont des objectifs du projet, et non des fonctions livrées. `Solver`, `ExecutionStack` et les stratégies déclarées dans `RuleApplier` restent à compléter. Le dépôt ne permet pas de confirmer une implémentation effective du backtracking, de la validation complète des contraintes, ni d’un générateur de grilles ou de niveaux.

## Stack technique et outils

| Domaine | Technologie | Détail |
|---|---|---|
| Langage | Java 21 | Niveau source et cible configuré dans Maven |
| Interface | JavaFX 16 | Contrôles JavaFX et vues FXML |
| Build et dépendances | Apache Maven | `pom.xml` et Maven Wrapper |
| Tests | JUnit Jupiter 5.13.4 | Tests unitaires du modèle et test applicatif |
| Versionnement | Git et GitHub | Dépôt distant : [stephcstn/groupe_d_sudoku](https://github.com/stephcstn/groupe_d_sudoku) |
| Intégration continue | GitHub Actions et Docker | Build déclenché sur les push et sur les pull requests vers `main` |

## Organisation et travail d’équipe

Le projet est réalisé par **cinq personnes**. Le dépôt montre un travail versionné avec Git et hébergé sur GitHub. Le workflow automatise un build Docker sur les push et sur les pull requests ciblant `main`, puis publie l’artefact JAR comme artefact GitHub Actions.

Les éléments visibles ne permettent pas d’affirmer l’adoption d’un Git Flow formalisé, de revues de code obligatoires, d’un suivi par issues ou d’un rythme de réunions agiles. Ces pratiques et la répartition des responsabilités doivent être précisées par l’équipe si elles ont été utilisées.

### Auteurs

| Membre / identité Git | Rôle |
|---|---|
| Stéphane Castan (`stephcstn`) | À confirmer par l’équipe |
| Thomas (`ThoCode` / `Novaito` dans l’historique) | À confirmer par l’équipe |
| `vmine0` | À confirmer par l’équipe |
| Jean | À confirmer par l’équipe |
| Mélodie Fleury | À confirmer par l’équipe |

## Prérequis

- **JDK 21** (nécessaire à la compilation du code configuré en Java 21) ;
- une connexion Internet lors de la première utilisation du Maven Wrapper, afin de télécharger Maven et les dépendances ;
- pour lancer l’application graphique, un environnement compatible avec JavaFX et disposant d’un affichage graphique.

Maven est fourni par le wrapper du dépôt : il n’est pas nécessaire de l’installer séparément pour les commandes ci-dessous.

## Installation et lancement

### En ligne de commande

```bash
git clone https://github.com/stephcstn/groupe_d_sudoku.git
cd groupe_d_sudoku
./mvnw clean package
./mvnw javafx:run
```

Le projet configure `fr.univpoitiers.App` comme classe principale JavaFX. Le lancement ouvre la vue FXML actuellement présente ; il ne s’agit pas encore d’un jeu Sudoku complet.

Sous Windows, utilisez le wrapper `mvnw.cmd` à la place de `./mvnw` (dans PowerShell, préfixez la commande de `.\`).

### Dans un IDE

1. Cloner le dépôt ou l’ouvrir depuis l’URL GitHub.
2. Ouvrir le fichier `pom.xml` comme projet Maven dans IntelliJ IDEA ou Eclipse.
3. Configurer un JDK 21 pour le projet et laisser l’IDE importer les dépendances Maven.
4. Exécuter `fr.univpoitiers.App` comme application Java, ou lancer l’objectif Maven `javafx:run`.
5. Pour lancer les tests, exécuter la suite JUnit du projet ou la commande Maven indiquée ci-dessous.

## Tests

Exécuter les tests unitaires avec Maven :

```bash
./mvnw clean test
```

Les tests présents couvrent notamment l’initialisation et les notes d’une cellule, le verrouillage, les dimensions et valeurs de la grille, la disponibilité des cellules, la remise à zéro et certaines mises à jour de notes. Ils ne constituent pas une validation complète des règles du Sudoku, du contrôleur JavaFX, de la résolution ou de la génération.

**Dernière vérification locale documentée :** `clean test` a réussi avec **26 tests, 0 échec et 0 erreur**. Aucun rapport de couverture n’est actuellement configuré ; aucune couverture chiffrée n’est donc publiée.

## Intégration continue

Le workflow GitHub Actions défini dans `.github/workflows/maven.yml` construit l’image Docker, laquelle exécute `mvn clean package`, puis met le JAR généré à disposition sous forme d’artefact de workflow. Le statut des exécutions est consultable depuis le badge CI en haut de ce document.