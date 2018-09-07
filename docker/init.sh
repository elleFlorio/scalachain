#! /bin/sh
SOURCE=$1
if [ $SOURCE == "local" ]
then
    echo "Running local source code"
    cd /development
else
    echo "Runnig repo souce code"
    cd scalachain
    git fetch
    git checkout $SOURCE && git pull
fi
sbt "run 0.0.0.0 8080"