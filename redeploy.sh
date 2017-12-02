#!/bin/bash

printf "Pulling latest changes from GitHub\n"
git pull

sudo kill `pidof java`

printf "Building... "
mvn clean install &> build.log 
printf "[DONE]\n"

printf "Starting up..."
sudo java -jar -Dspring.profiles.active=prod target/hoodwatch-0.0.1-SNAPSHOT.jar &> run.log & 
printf "[DONE]\n"

