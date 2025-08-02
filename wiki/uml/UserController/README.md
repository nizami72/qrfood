# UserController Sequence Diagram

This directory contains a sequence diagram for the UserController methods in the QRFood application.

## Overview

The sequence diagram visualizes the interactions between the UserController and other components in the system, including:

- UserService
- UserRepository
- UserProfileRepository
- EateryService
- CategoryService
- DishService

The diagram shows the flow of requests and responses for all methods in the UserController:

1. getAllUsersFromAllEateries
2. getAllUsers
3. getAllEateryUsers
4. getUserById
5. getUserByUsername
6. putUser
7. deleteUser
8. registerEateryStaff
9. deleteUserByName

## Viewing the Diagram

The sequence diagram is created using PlantUML, which is a text-based diagram generation tool. To view the diagram:

### Option 1: Online PlantUML Server

1. Go to [PlantUML Online Server](https://www.plantuml.com/plantuml/uml/)
2. Copy the contents of `UserControllerSequenceDiagram.puml` and paste it into the editor
3. The diagram will be rendered automatically

### Option 2: Using IntelliJ IDEA with PlantUML Plugin

1. Install the PlantUML Integration plugin in IntelliJ IDEA
2. Open the `UserControllerSequenceDiagram.puml` file
3. Right-click in the editor and select "Show Diagram" or click the diagram icon in the gutter

### Option 3: Using VS Code with PlantUML Extension

1. Install the PlantUML extension in VS Code
2. Open the `UserControllerSequenceDiagram.puml` file
3. Press Alt+D to preview the diagram

### Option 4: Command Line with PlantUML JAR

1. Download the PlantUML JAR file from [PlantUML website](https://plantuml.com/download)
2. Run the following command:
   ```
   java -jar plantuml.jar UserControllerSequenceDiagram.puml
   ```
3. This will generate a PNG image of the diagram

## Understanding the Diagram

The sequence diagram follows standard UML notation:

- Actors and participants are represented as boxes at the top
- Time flows from top to bottom
- Arrows represent method calls and returns
- Activation boxes show when a participant is active
- Groups organize related interactions
- Alt fragments show conditional logic
- Loops show repetitive actions

Each method in the UserController is represented as a separate group in the diagram, making it easy to understand the flow of each API endpoint.