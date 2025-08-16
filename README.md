# WhatsApp Clone

A WhatsApp-style chat system using Spring Boot, WebFlux, RabbitMQ, and MongoDB. It supports JWT-based authentication, WebSockets for real-time messaging, and push notifications via Firebase (FCM).
Covered with **Unit Tests** and **Integration Tests** using **Test Containers**.

![image](https://github.com/szopszop/whatsapp-clone/blob/main/architektura.png)



Configuration managed from external repository:
https://github.com/szopszop/whatsapp-clone-config

---

## Microservices Overview

| Service               | Purpose                                 | Tech Stack                        | Communication    |
|-----------------------|-----------------------------------------|------------------------------------|------------------|
| `api-gateway`         | Central entry point & routing           | Spring Cloud Gateway (Reactive), Redis    | REST (Reactive)  |
| `auth-server`         | Auth + JWT + OAuth2 + Blacklist         | PostgreSQL, Redis                  | REST (Blocking)  |
| `user-service`        | User CRUD + profile info                | PostgreSQL, Redis                  | REST (Blocking)  |
| `message-service`     | Messaging logic, WebSocket sessions     | MongoDB, WebSockets                | WS + REST (Reactive) |
| `notification-service`| Push notifications (FCM)                | Firebase Cloud Messaging           | REST (Reactive)  |
| `queue-service`       | Message broker                          | RabbitMQ                           | AMQP (Async)     |

---

## Communication Flow

| From → To                      | Type        | Protocol           | Purpose                                 |
|-------------------------------|-------------|--------------------|-----------------------------------------|
| API Gateway → All             | REST        | HTTP (Reactive)    | Routing external client requests        |
| Api Gateway ↔ Redis           | Internal    | Redis              | Token validate / revoke               |
| Auth Server ↔ User Service    | REST        | HTTP (Blocking)    | User validation     |
| Auth Server ↔ Redis           | Internal    | Redis              | Token caching / blacklist               |
| User Service → PostgreSQL     | Internal    | JDBC               | Persistent user data                    |
| User Service → Redis          | Internal    | Redis              | Cache user profile info                 |
| Message Service ↔ MongoDB     | Internal    | Mongo Driver       | Store and fetch messages                |
| Message Service → Queue Service   | Async       | AMQP (RabbitMQ)    | Message publishing                      |
| Queue Service → Notification Service  | Async       | AMQP (RabbitMQ)    | Deliver notification trigger            |
| Message Service → Notification Service| REST        | HTTP (Reactive)    | Push FCM notifications (optional path)  |
| Notification Service → FCM        | External    | HTTPS              | Send push notification to device        |
| Message Service ↔ WebSocket       | Realtime    | WS                 | Real-time messaging                     |

---

## Key Features

- JWT Authentication with role-based access (RBAC)  
- Reactive architecture using Spring WebFlux  
- Secure token handling + Redis-backed blacklist  
- FCM push notifications for offline users  
- WebSockets for live chat experience  
- Test Containers for integration testing  
- RabbitMQ for event-driven comms between services  

---

## Deployment Options

### Local Development with Docker Compose

For local development, you can use Docker Compose:

1. Make sure you have the `.env` file in the root directory with all required environment variables:
   - Database credentials (POSTGRES_USERNAME, POSTGRES_PASSWORD)
   - Service database names (AUTH_SERVER_DB_NAME, USER_SERVICE_DB_NAME, MESSAGE_SERVICE_DB_NAME)
   - MongoDB credentials (MONGO_DB_USER, MONGO_DB_PASSWORD)
   - RabbitMQ credentials (RABBITMQ_USER, RABBITMQ_PASSWORD)
   - JWT secrets and other configuration

2. Run the application using Docker Compose:
   ```bash
   docker-compose -f docker-compose/docker-compose.yml up -d
   ```

3. Access the services:
   - Frontend: http://localhost:4200
   - API Gateway: http://localhost:8050
   - Auth Server: http://localhost:8090
   - Grafana Dashboard: http://localhost:3000


### Cloud Deployment with Kubernetes

The application can be automatically deployed to Google Kubernetes Engine (GKE) using GitHub Actions:

1. Push changes to the main branch
2. GitHub Actions workflow builds Docker images and pushes them to Google Container Registry
3. Kubernetes manifests are applied to deploy the application to GKE

For detailed instructions on setting up GCP deployment, see the [Kubernetes Deployment README](k8s/README.md).
