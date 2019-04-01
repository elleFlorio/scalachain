#! /bin/sh
SOURCE=$1
SOURCE_FOLDER=$2
if [[ ${SOURCE} == "local" ]]
then
    echo "Running local source code"
    if ! [[ -z "$SOURCE_FOLDER" ]]
    then
        echo "copying local sources from $SOURCE_FOLDER to /development"
        cp -r ${SOURCE_FOLDER}/. /development
    fi
        cd /development
else
    echo "Runnig repo souce code"
    cd scalachain
    git fetch
    git checkout $SOURCE && git pull
fi
sbt "run 0.0.0.0 8080"