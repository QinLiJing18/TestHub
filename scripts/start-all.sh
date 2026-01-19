#!/usr/bin/env bash
set -e
ROOT_DIR=$(cd "$(dirname "$0")/.." && pwd)
cd "$ROOT_DIR"

echo "Starting infrastructure with docker compose..."
# prefer classic docker-compose, fall back to `docker compose` plugin if available
if command -v docker-compose >/dev/null 2>&1; then
  COMPOSE_CMD="docker-compose"
elif docker compose version >/dev/null 2>&1; then
  COMPOSE_CMD="docker compose"
else
  echo "ERROR: neither 'docker-compose' nor 'docker compose' is available. Install Docker Compose or Docker Desktop." >&2
  exit 1
fi

echo "Using: $COMPOSE_CMD"
$COMPOSE_CMD up -d

# Wait for containers to become healthy (timeout 180s)
TIMEOUT=180
SLEEP=5
ELAPSED=0

echo "Waiting for containers to report healthy status..."
# Check health for named containers directly (avoids relying on compose CLI flags)
containers=("testhub-mysql" "testhub-redis" "testhub-nacos" "testhub-emqx")
while [ $ELAPSED -lt $TIMEOUT ]; do
  UNHEALTHY_COUNT=0
  for cname in "${containers[@]}"; do
    cid=$(docker ps -q -f "name=${cname}")
    if [ -z "$cid" ]; then
      # not running yet
      UNHEALTHY_COUNT=$((UNHEALTHY_COUNT+1))
      continue
    fi
    # prefer Health.Status if available, otherwise fall back to State.Status
    status=$(docker inspect --format='{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' $cid 2>/dev/null || true)
    if [ -z "$status" ]; then
      UNHEALTHY_COUNT=$((UNHEALTHY_COUNT+1))
      continue
    fi
    if [ "$status" != "healthy" ] && [ "$status" != "running" ]; then
      UNHEALTHY_COUNT=$((UNHEALTHY_COUNT+1))
    fi
  done

  if [ $UNHEALTHY_COUNT -eq 0 ]; then
    echo "All named containers are running/healthy."
    break
  fi
  sleep $SLEEP
  ELAPSED=$((ELAPSED+SLEEP))

if [ $ELAPSED -ge $TIMEOUT ]; then
  echo "Timeout waiting for healthy containers. Run 'docker-compose ps' and 'docker-compose logs -f' to inspect." >&2
  exit 1
fi

# Build project (skip tests for speed)
echo "Building project (mvn clean install -DskipTests)..."
mvn -T 1C clean install -DskipTests

# Start gateway and auth services in background (Linux/macOS)
echo "Starting testhub-gateway and testhub-auth"
(cd testhub-gateway && mvn spring-boot:run &)
(cd testhub-auth && mvn spring-boot:run &)

echo "Started gateway and auth (check logs in their consoles or use 'ps' to inspect)."
