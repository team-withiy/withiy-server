#!/bin/bash

cd "$(dirname "$0")/.."
docker compose down -v
docker rmi withiy-server-server --force

if docker compose build --no-cache; then
	echo "Build success!"
else
	echo "Build failed..."
	exit 1
fi
docker compose up -d
