#!/bin/sh

PROJECT_NAME=geosurveymap
SCRIPT_DIR=$(cd $(dirname $0) && pwd)
PROJECT_DIR="$(cd "$(dirname $0)/.." && pwd)"
DOCKER_COMPOSE_DIR=$SCRIPT_DIR/docker
BACKEND_DIR=$PROJECT_DIR/src

VERSION=$(grep "set(\"geoSurveyMapVersion\", " $PROJECT_DIR/build.gradle.kts | grep -o "[0-9.]*")
echo "GeoSurveyMap Version: $VERSION"

COMPOSE_FILE="$DOCKER_COMPOSE_DIR/compose-dev-remote.yml"

docker image rm jkazmierczak/projecth-repository

cd $DOCKER_COMPOSE_DIR
echo "Starting $COMPOSE_FILE..."
docker-compose -p $PROJECT_NAME -f $COMPOSE_FILE up -d --build
