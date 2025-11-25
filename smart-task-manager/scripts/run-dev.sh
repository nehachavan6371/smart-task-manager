#!/usr/bin/env bash
# Run backend and frontend for local development
set -e
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
echo "Starting services from $ROOT_DIR"

cd "$ROOT_DIR/backend"
echo "Starting backend (mvn spring-boot:run) — logs: backend.log"
nohup mvn -Dspring-boot.run.profiles=dev -Dspring-boot.run.arguments=--spring.profiles.active=dev spring-boot:run > backend.log 2>&1 &
BACKEND_PID=$!
echo "Backend PID: $BACKEND_PID"

cd "$ROOT_DIR/frontend"
echo "Installing frontend deps (if needed)"
if [ ! -d node_modules ]; then
  npm ci
fi
echo "Starting frontend (Vite) — logs: frontend.log"
nohup npm run dev > frontend.log 2>&1 &
FRONTEND_PID=$!
echo "Frontend PID: $FRONTEND_PID"

echo "Services started. Tail logs with: tail -f backend/backend.log frontend/frontend.log"
echo "To stop: kill $BACKEND_PID $FRONTEND_PID"
