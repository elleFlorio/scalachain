# Scalachain Docker image
The Dockerfile lets you build the Docker Image of a scalachain node. In details, the Dockerfile:
* copy the ssh private key to pull from the repo - **N.B. the key is only for read operations**
* download the [sbt](https://www.scala-sbt.org/) version specified through the image ```build-arg``` parameter
* install git and openssh
* checkout the project
* copy the script file init.sh
* ```EXPOSE``` port 8080 

The image can be built using the command:

```docker build --build-arg SBT_VERSION="1.2.7" -t elleflorio/scalachain .```

The docker image is already available in the [Dockerhub](https://hub.docker.com/r/elleflorio/scalachain/)

## Run the docker container
The docker container compiles the source code and run the scalachain node. For this reason, when running the container it is required to pass an argument to the init script that indicates the source code to run.

To run a branch of the remote repo simply pass the name of the branch as the argument. e.g.:

```docker run --name scalachain-dev -p 8000:8000 elleflorio/scalachain development```

This will run the code of the branch ```development```.

To run the local source code mounting the local folder to the container ```/development``` one, use the ```-v /your/source/folder:/development``` and pass ```local``` as the argument to the script. You can optionally specify a folder inside the container where the source in the volume should be mounted, e.g. ```/tmp/scalachain```. The source will be automatically copied inside the ```/development``` folder. This is useful when you are running multiple containers in the same machine. This is a complete example:

```docker run --name scalachain -p 8080:8080 -v ~/Development/scala/scalachain/:/tmp/scalachain elleflorio/scalachain local /tmp/scalachain```

This will run the code contained in the folder ```~/Development/scala/scalachain/```.

When you stop the container and start it again, the script will pull the updated code from the repo in case you are running a remote branch, or will compile the local code in case you are running in local mode.