#!/bin/bash
# Apply all Kubernetes resources for the sticky sessions demo
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
kubectl apply -f "$SCRIPT_DIR/namespace.yaml"
kubectl apply -f "$SCRIPT_DIR/backend-deployment.yaml"
kubectl apply -f "$SCRIPT_DIR/backend-service.yaml"
kubectl apply -f "$SCRIPT_DIR/gateway-rbac.yaml"
kubectl apply -f "$SCRIPT_DIR/gateway-deployment.yaml"
kubectl apply -f "$SCRIPT_DIR/gateway-service.yaml"
kubectl apply -f "$SCRIPT_DIR/ingress.yaml"
echo "All resources applied. Wait for pods to be ready: kubectl get pods -n sticky-sessions-demo -w"
