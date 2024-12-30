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
-Dspring.datasource.url="jdbc:mysql://localhost:3306/fitmon" \
-Dspring.datasource.username="$DB_USERNAME" \
-Dspring.datasource.password="$DB_PASSWORD" \
-Dspring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect \
-Dspring.cloud.aws.credentials.accessKey="$AWS_S3_ACCESS_KEY" \
-Dspring.cloud.aws.credentials.secretKey="$AWS_S3_SECRET_ACCESS_KEY" \
-Dspring.cloud.aws.s3.bucket="$AWS_S3_BUCKET_NAME" \
-Dspring.cloud.aws.region.static="$AWS_S3_SECRET_REGION" \
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