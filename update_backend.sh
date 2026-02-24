#!/bin/bash

imageName="dji/cloud_api_sample:latest"

sudo docker stop $(sudo docker ps -aqf "name=cloud_api_sample-1")

sudo docker rm $(sudo docker ps -aqf "name=cloud_api_sample-1")

sudo docker rmi $(sudo docker images  -qf "reference=${imageName}")

cd source/backend_service/

sudo docker build -t ${imageName} .

