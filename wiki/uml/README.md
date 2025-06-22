# Order Creation Flow Diagrams

This directory contains UML diagrams that illustrate the flow of order creation in the QRFood application.

## Diagrams

### 1. Order Creation Flow (`order_creation_flow.puml`)

This sequence diagram shows the step-by-step process of creating a new order, from the client request to the server response. It illustrates:

- How the client sends an order request with OrderDto
- How OrderController processes the request
- How OrderService creates Order and OrderItem entities
- How repositories interact with the database
- How the response is constructed and sent back to the client

To view this diagram, you can use a PlantUML viewer or generate an image using the PlantUML tool.

### 2. Order Entity Relationships (`order_entity_relationships.puml`)

This class diagram shows the relationships between the entities involved in order creation:

- Order
- OrderItem
- TableInEatery
- DishEntity
- Category
- Eatery
- QrCode

It illustrates how these entities are related to each other, which is essential for understanding the data model of the application.

### 3. Order Data Transfer Objects (`order_dto_relationships.puml`)

This class diagram shows the DTOs (Data Transfer Objects) used in order creation:

- OrderDto
- OrderItemDTO

It illustrates how these DTOs are related to each other and how they map to the entity objects.

## How to Use These Diagrams

These diagrams can be used to:

1. Understand the flow of order creation in the QRFood application
2. Understand the data model of the application
3. Understand how DTOs are used to transfer data between the client and server
4. Onboard new developers to the project
5. Document the application architecture

## Generating Images from PlantUML Files

To generate images from the PlantUML files, you can use the PlantUML tool:

```bash
java -jar plantuml.jar order_creation_flow.puml
java -jar plantuml.jar order_entity_relationships.puml
java -jar plantuml.jar order_dto_relationships.puml
```

This will generate PNG images of the diagrams in the same directory.

Alternatively, you can use online PlantUML viewers or IDE plugins to view the diagrams.