#!/bin/bash

echo "Stopping the running application..."
sudo pkill -f cicd.jar || true

echo "Waiting for process to stop..."
while pgrep -f cicd.jar > /dev/null; do
  echo "Waiting..."
  sleep 1
done

echo "Starting new application..."
echo "Database username: $DB_USERNAME"
sudo -E java -jar \
-Dspring.config.location=file:/home/ubuntu/app/application.yml \
cicd.jar > output.log 2>&1 &

echo "Waiting for application to start..."
sleep 10

if pgrep -f cicd.jar > /dev/null; then
  echo "Application started successfully"
  exit 0
else
  echo "Application failed to start. Check logs:"
  tail -n 50 output.log
  exit 1
fi