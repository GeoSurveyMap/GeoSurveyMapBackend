#!/bin/sh


PROJECT_NAME=geosurveymap
SCRIPT_DIR=$(cd $(dirname $0) && pwd)
PROJECT_DIR="$(cd "$(dirname $0)/.." && pwd)"
DOCKER_COMPOSE_DIR=$SCRIPT_DIR/docker
BACKEND_DIR=$PROJECT_DIR/src

VERSION=$(grep "set(\"geoSurveyMapVersion\", " $PROJECT_DIR/build.gradle.kts | grep -o "[0-9.]*")
echo "GeoSurveyMap Version: $VERSION"

COMPOSE_FILE="$DOCKER_COMPOSE_DIR/compose-dev.yml"

cd $PROJECT_DIR
gradlew clean build -x &&

docker-compose -p $PROJECT_DIR -f $COMPOSE_FILE down geosurveymap-backend postgres &&
cd $PROJECT_DIR
echo "Running Gradle tasks..."

./gradlew clean build &&
if [ $? -ne 0]; then
  echo "Backend Docker build failed, aborting."
  exit 1
fi

echo "Building backend local image..."
cd $BACKEND_DIR
docker build --build-arg JAR_FILE=./build/libs/backend-"$VERSION".jar -t local/geosurveymap-backend:lastest -f Dockerfile . &&

cd $DOCKER_COMPOSE_DIR
echo "Starting $COMPOSE_FILE..."
docker-compose -p $PROJECT_NAME -f $COMPOSE_FILE up -d --build
