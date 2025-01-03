#!/bin/bash

echo "Stopping the running application..."
sudo pkill -f cicd.jar || true

echo "Waiting for process to stop..."
while pgrep -f cicd.jar > /dev/null; do
  echo "Waiting..."
  sleep 1
done

echo "Creating environment variables file..."
cat > /home/ubuntu/app/application-env.properties << EOL
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.cloud.aws.credentials.access-key=${AWS_S3_ACCESS_KEY}
spring.cloud.aws.credentials.secret-key=${AWS_S3_SECRET_ACCESS_KEY}
spring.cloud.aws.s3.bucket=${AWS_S3_BUCKET_NAME}
spring.cloud.aws.region.static=${AWS_S3_REGION}
EOL

echo "Checking environment variables (masked):"
echo "DB_USERNAME is set: [$(if [ -n "$DB_USERNAME" ]; then echo "YES"; else echo "NO"; fi)]"
echo "AWS_S3_BUCKET_NAME is set: [$(if [ -n "$AWS_S3_BUCKET_NAME" ]; then echo "YES"; else echo "NO"; fi)]"
echo "AWS_S3_REGION is set: [$(if [ -n "$AWS_S3_REGION" ]; then echo "YES"; else echo "NO"; fi)]"

echo "Starting new application..."
sudo -E java -jar \
-Dspring.config.import=optional:file:/home/ubuntu/app/application-env.properties \
-Dspring.config.location=file:/home/ubuntu/app/application.yml \
cicd.jar > /home/ubuntu/app/output.log 2>&1 &

echo "Waiting for application to start..."
sleep 10

if pgrep -f cicd.jar > /dev/null; then
  echo "Application started successfully"
  echo "Last 50 lines of log:"
  tail -n 50 /home/ubuntu/app/output.log
  exit 0
else
  echo "Application failed to start. Check logs:"
  tail -n 50 /home/ubuntu/app/output.log
  exit 1
fi