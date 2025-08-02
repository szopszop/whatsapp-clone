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
