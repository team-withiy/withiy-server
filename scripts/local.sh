#!/bin/bash

rm -r build
export JWT_SECRET_KEY=$(openssl rand -base64 64)

if ./gradlew clean build; then
	echo "Build success!"
else
	echo "Build failed..."
	exit 1
fi
java -jar build/libs/*SNAPSHOT.jar
