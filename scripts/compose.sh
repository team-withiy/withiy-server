#!/bin/bash

cd "$(dirname "$0")/.."
docker compose down -v

if docker compose build; then
	echo "Build success!"
else
	echo "Build failed..."
	exit 1
fi
docker compose up -d
