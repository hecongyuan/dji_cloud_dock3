#!/bin/bash

imageName="dji/nginx:latest"

sudo docker stop $(sudo docker ps -aqf "name=cloud_api_sample_nginx_1")

sudo docker rm $(sudo docker ps -aqf "name=cloud_api_sample_nginx_1")

sudo docker rmi $(sudo docker images  -qf "reference=${imageName}")

cd source/nginx/

sudo docker build -t ${imageName} .


