.PHONY: help build test run docker-up docker-down clean

help:
	@echo "Available commands:"
	@echo "  make build        - Build the project using Maven"
	@echo "  make test         - Run tests with coverage report"
	@echo "  make run          - Run the application locally"
	@echo "  make up    - Start application with Docker Compose"
	@echo "  make cleardown  - Stop Docker containers"
	@echo "  make db-container - Getting inside database container"
	@echo "  make app-db       - Start app and database containers"
	@echo "  make elk-stack    - Start ELK Stack containers (elasticsearch, logstash and kibana)"
	@echo "  make keycloak     - Start keycloak containers"
	@echo "  make clean        - Clean build artifacts"

build:
	@echo "Building the project..."
	mvn clean package -DskipTests

run:
	@echo "Starting the application..."
	mvn spring-boot:run

docker-build:
	@echo "Starting Docker containers..."
	docker-compose up --build -d
	@echo "Application is starting..."
	@echo "Use 'make logs' to view logs"

up:
	@echo "Start docker container"
	docker compose up -d
	@echo "Container are run"

up-app:
	@echo "Start app containers with database"
	docker compose up app db pgadmin -d

up-elk:
	@echo "Start elk containers ..."
	docker compose up logstash elasticsearch kibana -d

up-sonar:
	@echo "Start sonarqube container for code quality and test"
	docker compose up sonarqube sonarqube-db -d
	@echo "Containers are run"

app-container:
	@echo "Get into inside spring boot container..."
	docker exec -it supply_app bash

db-container:
	@echo "Getting inside database container ..."
	docker exec -it supply_postgres bash

app-db:
	@echo "Starting app and db containers ..."
	docker compose up app db -d

elk-stack:
	@echo "Start ELK stack containers ..."
	docker compose up elasticsearch logstash kibana -d

keycloak:
	@echo  "Start keycloak containers"
	docker compose up keycloak keycloak-db -d

down:
	@echo "Stopping Docker containers..."
	docker compose down

clean:
	@echo "Cleaning build artifacts..."
	mvn clean
	docker compose down -v