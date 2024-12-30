#!/bin/bash

REPOSITORY=/home/ubuntu/app

echo "> 현재 구동 중인 애플리케이션 pid 확인"
CURRENT_PID=$(lsof -ti tcp:8080)

if [ -z "$CURRENT_PID" ]; then
    echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
    echo "> kill -15 $CURRENT_PID"
    kill -15 $CURRENT_PID
    sleep 5
fi

echo "> 새 어플리케이션 배포"
cd $REPOSITORY

echo "> Git Pull"
git pull origin dev

echo "> 로컬 변경 사항을 버리고 원격 dev 브랜치와 동기화"
git reset --hard origin/dev

echo "> 필요 없는 파일 제거"
git clean -fd

echo "> 프로젝트 Build 시작"
./gradlew clean build -x test

echo "> Build 파일 복사"
cp build/libs/*.jar $REPOSITORY/

echo "> 새 어플리케이션 배포"
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"
nohup java -jar \
    -Dspring.profiles.active=dev \
    $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &