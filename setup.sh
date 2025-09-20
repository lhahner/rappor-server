#!/bin/bash
docker --version  > /dev/null 2>&1
if [[ $? != 0 ]]; then
    echo "ERROR - Docker is not installed on your system. Please install docker and come back."
    exit 1
fi
java -version  > /dev/null 2>&1
if [[ $? != 0 ]]; then
    echo "ERROR - Java is not installed on your system, install JDK and JRE to run the server."
    exit 1
fi
if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo version "$version"
    if [[ "$version" -gt "21" ]]; then
        echo WARNING - Your Java version is greater than 21, this application was developed using OpenJdk 21.
    else         
        echo WARNING - Your Java version is less than 21, this application was developed using OpenJdk 21. 
    fi
fi
echo INFO - build server application with all required dependencies
./gradlew build
echo "INFO - All dependencies statiesfied, starting docker container..."
sudo docker compose up -d
if [[ $? != 0 ]]; then
    echo "ERROR - docker compose failed, is docker running?"
    exit 1
fi
echo INFO - Docker container is running, starting Spring Boot Server...
./gradlew bootRun 
if [[ $? != 0 ]]; then
    echo ERROR - Spring Boot Server not started, aborting and shutting down docker
    sudo docker stop $(sudo docker ps -a -q)
    exit 1
fi
