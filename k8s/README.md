# Kubernetes Deployment for WhatsApp Clone

This directory contains Kubernetes manifests for deploying the WhatsApp Clone application to Google Kubernetes Engine (GKE).

## Architecture

The application consists of the following components:

- **Frontend**: Angular application served by Nginx
- **Gateway Server**: API Gateway using Spring Cloud Gateway
- **Auth Server**: Authentication and authorization service
- **User Service**: User management service
- **Message Service**: Message handling service
- **Notification Service**: Notification handling service
- **Config Server**: Centralized configuration service
- **Eureka Server**: Service discovery
- **Databases**: PostgreSQL for Auth and User services, MongoDB for Message service
- **RabbitMQ**: Message broker for asynchronous communication
- **Redis**: Used by Gateway Server for rate limiting

## Prerequisites

Before deploying to GKE, you need to:

1. Create a GKE cluster in Google Cloud Platform
2. Create a Google Cloud service account with the following roles:
   - Kubernetes Engine Admin
   - Storage Admin
   - Container Registry Service Agent
   - Artifact Registry Administrator (for pushing images to Artifact Registry)
   - Artifact Registry Repository Administrator (for creating repositories)
3. Download the service account key as JSON
4. Add the following secrets to your GitHub repository:
   - `GCP_PROJECT_ID`: Your Google Cloud project ID
   - `GCP_SA_KEY`: The content of the service account key JSON file
   - `GKE_CLUSTER`: The name of your GKE cluster
   - `GKE_ZONE`: The zone of your GKE cluster (e.g., us-central1-a)
   - `POSTGRES_USERNAME`: PostgreSQL username
   - `POSTGRES_PASSWORD`: PostgreSQL password
   - `AUTH_SERVER_DB_NAME`: Auth server database name
   - `USER_SERVICE_DB_NAME`: User service database name
   - `MESSAGE_SERVICE_DB_NAME`: Message service database name
   - `MONGO_DB_USER`: MongoDB username
   - `MONGO_DB_PASSWORD`: MongoDB password
   - `RABBITMQ_USER`: RabbitMQ username
   - `RABBITMQ_PASSWORD`: RabbitMQ password
   - `JWT_SECRET`: Secret for JWT token generation
   - `ISSUER_URI`: JWT issuer URI

## Deployment

The application is automatically deployed to GKE using GitHub Actions when changes are pushed to the main branch. The workflow:

1. Builds Docker images for all services
2. Pushes the images to Google Container Registry
3. Deploys the services to GKE using the Kubernetes manifests in this directory

The deployment follows this order:
1. Secrets
2. Config Server
3. Eureka Server
4. RabbitMQ
5. Auth Server and its database
6. User Service and its database
7. Message Service and its database
8. Notification Service
9. Gateway Server and Redis
10. Frontend

## Manual Deployment

If you want to deploy manually, you can use the following commands:

```bash
# Set environment variables
export PROJECT_ID=your-project-id
export TAG=latest

# Replace placeholders in manifests
find k8s -type f -name "*.yaml" -exec sed -i "s/\${PROJECT_ID}/$PROJECT_ID/g" {} \;
find k8s -type f -name "*.yaml" -exec sed -i "s/\${TAG}/$TAG/g" {} \;

# Apply manifests
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/configserver.yaml
kubectl wait --for=condition=available --timeout=300s deployment/configserver

kubectl apply -f k8s/eurekaserver.yaml
kubectl wait --for=condition=available --timeout=300s deployment/eurekaserver

kubectl apply -f k8s/rabbitmq.yaml
kubectl wait --for=condition=available --timeout=300s deployment/rabbitmq

kubectl apply -f k8s/auth-server.yaml
kubectl wait --for=condition=available --timeout=300s deployment/auth-server

kubectl apply -f k8s/user-service.yaml
kubectl wait --for=condition=available --timeout=300s deployment/user-service

kubectl apply -f k8s/message-service.yaml
kubectl wait --for=condition=available --timeout=300s deployment/message-service

kubectl apply -f k8s/notification-service.yaml
kubectl wait --for=condition=available --timeout=300s deployment/notification-service

kubectl apply -f k8s/gatewayserver.yaml
kubectl wait --for=condition=available --timeout=300s deployment/gatewayserver

kubectl apply -f k8s/frontend.yaml
kubectl wait --for=condition=available --timeout=300s deployment/frontend
```

## Accessing the Application

After deployment, you can access the application at:

- Frontend: `http://<frontend-external-ip>`
- API Gateway: `http://<gatewayserver-external-ip>:8050`
- Auth Server: `http://<auth-server-external-ip>:8090`

To get the external IPs:

```bash
kubectl get service frontend -o jsonpath='{.status.loadBalancer.ingress[0].ip}'
kubectl get service gatewayserver -o jsonpath='{.status.loadBalancer.ingress[0].ip}'
kubectl get service auth-server -o jsonpath='{.status.loadBalancer.ingress[0].ip}'
```

## Troubleshooting

If you encounter issues with the deployment, you can check the logs of the pods:

```bash
kubectl get pods
kubectl logs <pod-name>
```

You can also check the status of the deployments:

```bash
kubectl get deployments
```

For more detailed information about a deployment:

```bash
kubectl describe deployment <deployment-name>
```
