# whatsapp-clone
A WhatsApp-like messaging system built with Spring Boot, WebFlux, MongoDB, and RabbitMQ/Kafka.
The project follows a microservices architecture with REST API support and will later integrate WebSockets for real-time messaging.

│── user-service/          # User authentication & management
│── message-service/       # Message handling (REST API, MongoDB)  -> Spring WebFlux
│── queue-service/         # Message queue (Kafka/RabbitMQ)
│── docker-compose.yml     # Deployment configuration

Technologies & Tools
- Spring Boot + WebFlux – Reactive backend
- MongoDB – Message storage
- PostgreSQL – User management
_ RabbitMQ / Kafka – Message queueing
- Spring Cloud Gateway – API Gateway
- Docker & Docker Compose – Containerized services
- Lombok, Reactor, Spring Security – Utility and security

