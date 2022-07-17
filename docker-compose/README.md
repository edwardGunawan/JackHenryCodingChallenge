# Docker compose environment

This section outlines the steps needed to run the service in Docker application.

## Pre-requisite
- docker installed and running locally
 - note: `Docker Desktop` and `Docker Toolbox` already include `Compose` for Mac. If you don't find that `docker-compose` is installed
 then you'll need to install it. See [here](https://docs.docker.com/compose/install/) for instructions.
 
 
## Deploy Images
To deploy a single image based on the current repo you need to run:
```sbt
 docker:publishLocal
``` 

Verify that the image has been published:
```sbt
$ docker images

REPOSITORY                                 TAG                 IMAGE ID            CREATED             SIZE
weather-app                                0.1                 2c4cc782aeb6        10 minutes ago      561MB
weather-app                                latest              2c4cc782aeb6        10 minutes ago      561MB

```

## Running Locally
### Start
The docker compose script relies on environment variables, set in a `.env` file. Create this file and make any modification before you first run.

```sbt
// inside docker-compose directory
cp .env.template .env
docker-compose up
```

If you want to run in detached mode (run containers in the background), add `-d` option.

### Terminate
Simply do Ctrl + C or if you use detach mode, run `docker-compose down`