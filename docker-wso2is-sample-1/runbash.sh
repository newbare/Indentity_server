#!/bin/bash
docker run --rm -t -i \
   -p 9763:9763 \
   -p 9443:9443 \
   -v /opt/data/wso2is/repository:/opt/wso2is/repository \
   vertigo/docker-wso2is:latest bash

