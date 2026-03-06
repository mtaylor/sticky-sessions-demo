#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

REGISTRY="${REGISTRY:-sticky-sessions-demo}"
VERSION="${VERSION:-latest}"

echo "Building images with registry prefix: $REGISTRY"
echo "=============================================="

# Build backend-service
echo ""
echo "Building backend-service..."
cd backend-service
mvn -q clean package -DskipTests
docker build -t "$REGISTRY/backend-service:$VERSION" .
cd ..

# Build gateway
echo ""
echo "Building gateway..."
cd gateway
mvn -q clean package -DskipTests
docker build -t "$REGISTRY/gateway:$VERSION" .
cd ..

echo ""
echo "=============================================="
echo "Build complete!"
echo ""
echo "Images created:"
echo "  - $REGISTRY/backend-service:$VERSION"
echo "  - $REGISTRY/gateway:$VERSION"
echo ""
echo "To load images into kind/minikube:"
echo "  kind load docker-image $REGISTRY/backend-service:$VERSION $REGISTRY/gateway:$VERSION"
echo ""
echo "Or for minikube:"
echo "  eval \$(minikube docker-env) && ./build.sh"
echo ""
