#!/bin/bash

# Move to the root directory (makers-backend directory)
cd "$(dirname "$0")/.."

# Load environment variables for shell scripts (port values etc.)
while read -r line; do
  [[ -z "$line" || "$line" == \#* ]] && continue
  key="${line%%=*}"
  value="${line#*=}"
  [[ -z "$key" || "$key" == "$line" ]] && continue
  export "$key=$value"
done < .env

# Load modular deployment functions
source ./scripts/check_running_container.sh
source ./scripts/pull_and_run_container.sh
source ./scripts/health_check.sh
source ./scripts/stop_and_clean_container.sh
source ./scripts/reload_nginx.sh

# 1. Determine which container is currently running (blue or green)
check_running_container

# 2. Pull and run the new container
pull_and_run_container $NEW_CONTAINER

# 3. Run health check on the new container
if ! health_check $NEW_PORT; then
  echo "❌ Health check failed. Cleaning up and aborting deployment."
  stop_and_clean_container $NEW_CONTAINER
  exit 1
fi

# 4. Reload Nginx to apply port switching
if ! reload_nginx $NEW_PORT; then
  echo "❌ Nginx reload failed. Aborting deployment."
  stop_and_clean_container $NEW_CONTAINER
  exit 1
fi

# 5. Stop and remove the previous container
stop_and_clean_container $RUNNING_CONTAINER

echo "✅ Finish Deploy"
