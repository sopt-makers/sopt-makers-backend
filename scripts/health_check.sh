#!/bin/bash

health_check() {
  local PORT=$1
  local URL="http://localhost:${PORT}${ACTUATOR_PATH}/health"

  echo "▶️ Health Check"

  for retry_count in {1..20}; do
    response=$(curl -s -w "\n%{http_code}" \
      -H "X-Api-Key: ${DEVELOPER_API_KEY}" \
      -H "X-Service-Name: developer" \
      "$URL")

    body=$(echo "$response" | sed '$d')
    status_code=$(echo "$response" | tail -n1)

    if [[ "$status_code" == "200" ]] && echo "$body" | grep -q '"status":"UP"'; then
      echo "✅ Health check successful"
      return 0
    fi

    echo "❌ Health check failed (try $retry_count)"
    echo "HTTP Status: $status_code"
    echo "Response: $body"

    sleep 5
  done

  return 1
}
