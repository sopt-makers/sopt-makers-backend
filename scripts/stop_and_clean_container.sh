#!/bin/bash

stop_and_clean_container() {
  local CONTAINER_NAME=$1
  if [ -z "${CONTAINER_NAME}" ]; then
    echo "⚠️ No container name provided to stop and clean."
    return 0
  fi
  echo "🛑 Stopping and removing container: ${CONTAINER_NAME}"
  docker-compose stop "${CONTAINER_NAME}"
  docker-compose rm -f "${CONTAINER_NAME}"
  docker image prune -a -f
}
