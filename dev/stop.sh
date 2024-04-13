#!/bin/sh
PROJECT_NAME=geosurveymap
SCRIPT_DIR=$(dirname "$0")
cd "$SCRIPT_DIR" || exit 1

docker-compose -p $PROJECT_NAME -f ./docker/compose-dev.yml down || exit 1
