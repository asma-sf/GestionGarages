# Application de Gestion de Garages

## Description
Cette application est un microservice développé avec Spring Boot permettant la gestion des garages, des véhicules et des accessoires associés. 
Il utilise Spring Data JPA pour la gestion des données, avec une base de données H2 en mémoire. 
L'application expose des API REST avec Spring Web et intègre des tests unitaires et d'intégration avec JUnit 5 et Mockito.

## Technologies utilisées
- Java 21 & Spring Boot
- Spring Data JPA
- H2 Database (base en mémoire)
- Spring Web
- JUnit 5 & Mockito (tests unitaires et d’intégration)

## Structure du projet

```
AppGestionGarages
│── src/main/java/com/renault
│   ├── controllers         # Couche contrôleurs (API REST)
│   │   ├── AccessoireController.java
│   │   ├── GarageController.java
│   │   ├── VehiculeController.java
│   ├── entities            # Couche entités (modèle de données)
│   │   ├── Accessoire.java
│   │   ├── Garage.java
│   │   ├── GarageOpeningTime.java
│   │   ├── OpeningTime.java (ce n'est pas une entité)
│   │   ├── Vehicule.java
│   ├── enums               # Énumérations
│   │   ├── TypeVehicule.java
│   ├── exceptions          # Gestion des exceptions
│   │   ├── AccessoireNotFoundException.java
│   │   ├── GarageNotFoundException.java
│   │   ├── VehiculeNotFoundException.java
│   │   ├── ResourceNotFoundException.java
│   ├── repositories        # Couche repository (accès aux données)
│   │   ├── AccessoireRepository.java
│   │   ├── GarageRepository.java
│   │   ├── VehiculeRepository.java
│   ├── services            # Couche service (logique métier)
│   │   ├── AccessoireService.java
│   │   ├── GarageService.java
│   │   ├── VehiculeService.java
│── src/main/resources
│   ├── application.properties  # Configuration de l’application
│── src/test/java/com/renault
│   ├── controllers
│   │   ├── GarageControllerIntegrationTest.java  # Tests d'intégration
│   ├── services
│   │   ├── AccessoireServiceTest.java  # Tests unitaires
│   │   ├── GarageServiceTest.java
│   │   ├── VehiculeServiceTest.java
│── pom.xml                   # Fichier de configuration Maven


## Points d'amélioration
1. Ajout d’une couche DTO et d’un framework de mapping
   - Actuellement, les entités sont directement exposées via les contrôleurs. Pour une meilleure séparation des responsabilités et une gestion plus efficace des données exposées, il serait recommandé d’ajouter une couche DTO (Data Transfer Object) et d’utiliser un framework de mapping comme MapStruct.
   - Faute de temps, cela n’a pas été implémenté dans cette version.

2. Ajout d’un Dockerfile pour faciliter le déploiement
   - Un fichier Dockerfile pourrait être ajouté pour packager l’application sous forme de conteneur.
   - Cependant, en raison des restrictions de sécurité de l’entreprise, Docker ne peut pas être installé sur les machines locales, ce qui empêche son utilisation pour ce projet.

## Lancer l’application
L’application se lance avec Spring Boot en exécutant la classe principale. 
Un bean est créé pour initialiser des données à l'exécution de l'application, utilisant la méthode CommandLineRunner. Cela permettra de créer trois garages au démarrage de l’application.

```shell (Lancer l'application avec Maven )
mvn spring-boot:run
```
**Tests d'intégration
Si vous devez lancer les tests d'intégration, n'oubliez pas de commenter le bean qui crée les garages dans
la classe principale AppGestionGaragesApplication pour éviter l'insertion des données pendant les tests.
 // @Bean
	CommandLineRunner commandLineRunner(GarageService garageService, GarageOpeningTimeRepository garageOpeningTimeRepository) {}
	
	
**Tests des API
Pour tester les API, Swagger a été intégré à l’application. Une fois l’application démarrée, 
vous pouvez accéder à l’interface Swagger à l'adresse suivante :
http://localhost:8080/swagger-ui.html

L’interface H2 est accessible à l’URL :
http://localhost:8080/h2-console

