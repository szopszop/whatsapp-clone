# WhatsApp Clone

A WhatsApp-like messaging system built with **Spring Boot**, **WebFlux**, **MongoDB**, and **Kafka**. The project follows a microservices architecture with **REST API** support and integrates **WebSockets** for real-time messaging and **Firebase Cloud Messaging (FCM)** for push notifications. All classes are covered with **Unit Tests** and **Integration Tests** using **Test Containers**.

---

## **Services**

- **`user-service/`**: User authentication & management (PostgreSQL).  
- **`message-service/`**: Message handling (REST API, MongoDB) → Spring WebFlux + WebSockets for real-time messaging.  
- **`notification-service/`**: Push notifications using **Firebase Cloud Messaging (FCM)**.  
- **`queue-service/`**: Message queueing (Kafka/RabbitMQ).  

---

## **Deployment**

- **`docker-compose.yml`**: Containerized services (Spring Boot apps, MongoDB, PostgreSQL, Kafka, Redis for WebSocket session management).  

---

## **Technologies & Tools**

- **Spring Boot + WebFlux**: Reactive backend.  
- **MongoDB**: Message storage.  
- **PostgreSQL**: User management.  
- **RabbitMQ / Kafka**: Message queueing.  
- **Spring Cloud Gateway**: API Gateway.  
- **WebSockets**: Real-time messaging.  
- **Firebase Cloud Messaging (FCM)**: Push notifications for mobile and web.  
- **Docker & Docker Compose**: Containerized services.  
- **Lombok, Reactor, Spring Security**: Utility and security.  

---

## **Key Features**

- Real-time messaging via **WebSockets**.  
- Push notifications via **FCM** for offline users.  
- Scalable microservices architecture with **Kafka** for event-driven communication.  
- Integration Testing with **Test Containers**.  

---

## **How to Run**

### **1. Clone the Repository**
```bash
git clone https://github.com/your-username/whatsapp-clone.git
cd whatsapp-clone
