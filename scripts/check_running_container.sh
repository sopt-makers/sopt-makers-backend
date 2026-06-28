#!/bin/bash

check_running_container() {
  mapfile -t CONTAINERS < <(sudo docker ps --filter "name=makers-backend-" --format "{{.Names}}")
  local COUNT=${#CONTAINERS[@]}

  if [ "$COUNT" -eq 0 ]; then
    echo "⚠️  No running container detected. Defaulting to makers-backend-blue."
    RUNNING_CONTAINER=makers-backend-green
    NEW_CONTAINER=makers-backend-blue
    NEW_PORT=$MAKERS_BACKEND_BLUE_PORT

  elif [ "$COUNT" -ge 2 ]; then
    echo "⚠️  Both containers are running. Resolving active slot via Nginx config."
    local NGINX_CONF=/etc/nginx/conf.d/makers-backend-url.inc
    if [ ! -f "$NGINX_CONF" ]; then
      echo "❌ Both containers running but Nginx config not found at $NGINX_CONF. Manual intervention required."
      exit 1
    fi
    local ACTIVE_PORT
    ACTIVE_PORT=$(grep -oE '[0-9]+;' "$NGINX_CONF" | tr -d ';')
    if [[ "$ACTIVE_PORT" == "$MAKERS_BACKEND_BLUE_PORT" ]]; then
      RUNNING_CONTAINER=makers-backend-blue
      NEW_CONTAINER=makers-backend-green
      NEW_PORT=$MAKERS_BACKEND_GREEN_PORT
    elif [[ "$ACTIVE_PORT" == "$MAKERS_BACKEND_GREEN_PORT" ]]; then
      RUNNING_CONTAINER=makers-backend-green
      NEW_CONTAINER=makers-backend-blue
      NEW_PORT=$MAKERS_BACKEND_BLUE_PORT
    else
      echo "❌ Nginx routing to unknown port ($ACTIVE_PORT). Manual intervention required."
      exit 1
    fi
    echo "🧹 Stopping stale container: $NEW_CONTAINER"
    stop_and_clean_container "$NEW_CONTAINER"

  else
    local CONTAINER_NAME="${CONTAINERS[0]}"
    local HOST_PORT
    HOST_PORT=$(sudo docker inspect -f '{{(index (index .NetworkSettings.Ports "8080/tcp") 0).HostPort}}' "$CONTAINER_NAME")
    if [[ "$HOST_PORT" == "$MAKERS_BACKEND_BLUE_PORT" ]]; then
      RUNNING_CONTAINER=makers-backend-blue
      NEW_CONTAINER=makers-backend-green
      NEW_PORT=$MAKERS_BACKEND_GREEN_PORT
    elif [[ "$HOST_PORT" == "$MAKERS_BACKEND_GREEN_PORT" ]]; then
      RUNNING_CONTAINER=makers-backend-green
      NEW_CONTAINER=makers-backend-blue
      NEW_PORT=$MAKERS_BACKEND_BLUE_PORT
    else
      echo "⚠️  Unexpected port detected: $HOST_PORT. Defaulting to makers-backend-blue."
      RUNNING_CONTAINER=makers-backend-green
      NEW_CONTAINER=makers-backend-blue
      NEW_PORT=$MAKERS_BACKEND_BLUE_PORT
    fi
  fi

  export RUNNING_CONTAINER
  export NEW_CONTAINER
  export NEW_PORT
}
