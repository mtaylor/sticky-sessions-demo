# Spring Cloud Gateway Sticky Sessions Demo

A demo showing how Spring Cloud Gateway can be configured to use sticky sessions in a Kubernetes environment, in combination with Spring Cloud Load Balancer and K8s Ingress.

## Architecture

```
Browser → Ingress (sticky cookie) → Gateway (2 pods) → Load Balancer (sticky) → Backend (2 pods)
```

- **Ingress**: Nginx Ingress with `affinity: cookie` ensures the same gateway pod receives requests from the same browser session
- **Gateway**: Spring Cloud Gateway with Spring Cloud Load Balancer's `request-based-sticky-session` ensures requests are forwarded to the same backend pod
- **Backend**: Simple Spring Boot service that returns its instance ID (pod name in K8s)

## Prerequisites

- Java 21
- Maven 3.8+
- Docker
- Kubernetes cluster (minikube, kind, or similar)
- Nginx Ingress Controller

## Quick Start

### 1. Build the images

```bash
./build.sh
```

For minikube (use the Docker daemon inside minikube):

```bash
eval $(minikube docker-env)
./build.sh
```

For kind:

```bash
./build.sh
kind load docker-image sticky-sessions-demo/backend-service:latest sticky-sessions-demo/gateway:latest
```

### 2. Deploy to Kubernetes

```bash
# Create namespace and deploy all resources
kubectl apply -f kubernetes/

# Or apply individually
kubectl apply -f kubernetes/namespace.yaml
kubectl apply -f kubernetes/backend-deployment.yaml
kubectl apply -f kubernetes/backend-service.yaml
kubectl apply -f kubernetes/gateway-rbac.yaml
kubectl apply -f kubernetes/gateway-deployment.yaml
kubectl apply -f kubernetes/gateway-service.yaml
kubectl apply -f kubernetes/ingress.yaml
```

### 3. Access the demo

**With minikube:**

```bash
# Add host entry (or use /etc/hosts)
echo "$(minikube ip) sticky-sessions.local" | sudo tee -a /etc/hosts

# Open in browser
open http://sticky-sessions.local
```

**With kind:**

```bash
# Get the ingress controller's NodePort or use port-forward
kubectl get svc -n ingress-nginx  # or your ingress namespace

# Add host entry for the node IP
# Then access http://sticky-sessions.local
```

**Alternative - port forward (no ingress):**

```bash
kubectl port-forward -n sticky-sessions-demo svc/gateway 8080:8080
# Open http://localhost:8080
# Note: Ingress sticky session won't apply, but Gateway→Backend sticky session will
```

### 4. Verify sticky sessions

1. Open the demo page in your browser
2. Click "Fetch Instance ID" multiple times
3. You should see the **same instance ID** for all requests (same browser session)
4. Open the page in an incognito/private window or different browser → you may get a different instance
5. Refresh the page and click again → same instance (cookie persists)

## Project Structure

```
├── backend-service/          # Spring Boot service
│   ├── src/main/java/
│   │   └── com/example/backend/
│   │       ├── BackendServiceApplication.java
│   │       ├── controller/InstanceController.java
│   │       └── service/InstanceIdService.java
│   ├── src/main/resources/static/index.html  # Demo frontend
│   ├── Dockerfile
│   └── pom.xml
├── gateway/                  # Spring Cloud Gateway
│   ├── src/main/java/
│   │   └── com/example/gateway/GatewayApplication.java
│   ├── src/main/resources/application.yml
│   ├── Dockerfile
│   └── pom.xml
├── kubernetes/
│   ├── namespace.yaml
│   ├── backend-deployment.yaml
│   ├── backend-service.yaml
│   ├── gateway-deployment.yaml
│   ├── gateway-service.yaml
│   ├── gateway-rbac.yaml     # RBAC for K8s discovery
│   └── ingress.yaml
├── build.sh
├── pom.xml
└── README.md
```

## Configuration

### Gateway sticky session (Spring Cloud Load Balancer)

```yaml
spring:
  cloud:
    loadbalancer:
      configurations: request-based-sticky-session
      sticky-session:
        add-service-instance-cookie: true
        instance-id-cookie-name: sc-lb-instance-id
```

The Load Balancer uses the `sc-lb-instance-id` cookie to route subsequent requests to the same backend instance. The cookie is set automatically when the first response is received.

### Ingress sticky session (Nginx)

```yaml
annotations:
  nginx.ingress.kubernetes.io/affinity: "cookie"
  nginx.ingress.kubernetes.io/affinity-mode: "persistent"
  nginx.ingress.kubernetes.io/session-cookie-name: "route"
```

## Local Development

Run without Kubernetes (for API development):

```bash
# Terminal 1 - Backend
cd backend-service && mvn spring-boot:run

# Terminal 2 - Gateway (needs discovery - use SimpleDiscoveryClient or run in K8s)
cd gateway && mvn spring-boot:run
```

For local gateway testing without K8s, you can add a `SimpleDiscoveryClient` configuration with static backend instances.
