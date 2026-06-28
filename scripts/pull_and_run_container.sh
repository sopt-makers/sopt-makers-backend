#!/bin/bash

pull_and_run_container() {
  local CONTAINER_NAME=$1
  echo "▶️ Pull & Run: ${CONTAINER_NAME}"
  if ! docker compose pull $CONTAINER_NAME; then
    echo "❌ Failed to pull image for ${CONTAINER_NAME}. Aborting."
    exit 1
  fi
  docker compose up -d $CONTAINER_NAME
}
