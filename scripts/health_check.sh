#!/bin/bash

health_check() {
  local PORT=$1

  echo "▶️ Health Check on port ${PORT}..."
  for retry_count in {1..20}; do
    { set +x; } 2>/dev/null
    response=$(curl -s \
      -H "X-Api-Key: ${DEVELOPER_API_KEY}" \
      -H "X-Service-Name: developer" \
      http://localhost:${PORT}${ACTUATOR_PATH}/health)

    if echo "$response" | grep -q '^{"status":"UP"'; then
      echo "✅ Health check successful"
      return 0
    else
      echo "❌ Health check failed (try $retry_count)"
    fi
    sleep 5
  done

  return 1
}
