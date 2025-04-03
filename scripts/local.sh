#!/bin/bash

rm -r build
# export JWT_SECRET_KEY=$(openssl rand -base64 64)
export JWT_SECRET_KEY="kMpPh2pC3BPKQKXnugMcH3X1L0pLkEogJifhqQAJScZWR/L25qrahRCcXYZZj1SAvAn4Lpm70nSE96qqd7YrOg=="

if ./gradlew clean build; then
	echo "Build success!"
else
	echo "Build failed..."
	exit 1
fi
java -jar build/libs/*SNAPSHOT.jar
