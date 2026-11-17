#!/bin/bash

readonly CONTAINER_STOP_TIMEOUT_SECONDS=30
readonly DOCKER_COMMAND_TIMEOUT_SECONDS=45

run_docker_command_with_timeout() {
  timeout --kill-after=5s "${DOCKER_COMMAND_TIMEOUT_SECONDS}s" "$@"
}

stop_and_clean_container() {
  local CONTAINER_NAME=$1
  if [ -z "${CONTAINER_NAME}" ]; then
    echo "⚠️ No container name provided to stop and clean."
    return 0
  fi

  echo "🛑 Stopping and removing container: ${CONTAINER_NAME}"
  if ! run_docker_command_with_timeout docker compose stop --timeout "${CONTAINER_STOP_TIMEOUT_SECONDS}" "${CONTAINER_NAME}"; then
    echo "⚠️ Graceful stop failed or timed out. Force removing container: ${CONTAINER_NAME}"
    if ! run_docker_command_with_timeout docker rm -f "${CONTAINER_NAME}"; then
      echo "❌ Failed to remove container: ${CONTAINER_NAME}"
      return 1
    fi
  elif ! run_docker_command_with_timeout docker compose rm -f "${CONTAINER_NAME}"; then
    echo "⚠️ Container removal failed or timed out. Force removing container: ${CONTAINER_NAME}"
    if ! run_docker_command_with_timeout docker rm -f "${CONTAINER_NAME}"; then
      echo "❌ Failed to remove container: ${CONTAINER_NAME}"
      return 1
    fi
  fi

  if ! run_docker_command_with_timeout docker image prune -a -f; then
    echo "⚠️ Image cleanup failed or timed out. Continuing deployment."
  fi
}
