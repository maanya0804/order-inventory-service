# Order & Inventory Service

A REST API for a quick-commerce style order flow, built with Java 21, Spring Boot and PostgreSQL.

## Features
- Product catalog with stock tracking
- Multi-item order placement with request validation
- Atomic stock reservation: orders never oversell inventory
- Consistent HTTP error responses (400, 404, 409)

## Run locally
1. Start the database: `docker compose up -d`
2. Start the app: `./mvnw spring-boot:run`
3. Try the endpoints in `requests.http`

## Endpoints
- POST /products, GET /products, GET /products/{id}
- POST /orders, GET /orders, GET /orders/{id}

## Test
`./mvnw test` runs a 20-thread concurrency test showing that stock never goes negative.