#!/bin/bash

cd "$(dirname "$0")/.."

# BuildKit 활성화로 병렬 빌드 및 캐시 효율 향상
export DOCKER_BUILDKIT=1
export COMPOSE_DOCKER_CLI_BUILD=1

# 개발 중에는 볼륨 유지, 완전 초기화가 필요할 때만 -v 사용
if [ "$1" = "--clean" ]; then
    echo "🧹 Clean mode: Removing volumes..."
    docker compose down -v
else
    echo "🔄 Normal mode: Preserving volumes..."
    docker compose down
fi

echo "🔨 Building images..."
if docker compose build --parallel; then
	echo "✅ Build success!"
else
	echo "❌ Build failed..."
	exit 1
fi

echo "🚀 Starting services..."
docker compose up -d
echo "✨ Services are running!"