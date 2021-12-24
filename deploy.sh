#!/bin/bash
git reset --hard
git pull
mvn -U -am clean package
docker rm -f lazy_notion
docker rmi -f lazy_notion:v1
docker build -t lazy_notion:v1 .
docker run -d --name="lazy_notion" -p 6666:6666 lazy_notion:v1
