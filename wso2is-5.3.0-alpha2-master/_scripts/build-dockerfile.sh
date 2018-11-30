#!/bin/bash

################################################
# Usage:
#
# ./build-dockerfile.sh optional_args
#
################################################

#!!!!!!!!!!! PROXY settings !!!!!!!!!!!!!!!!!!!!
### Commande line:
# Add this options as parameters: -e http_proxy <HTTP_PROXY> -e https_proxy <HTTPS_PROXY>
### In Dockerfile: 
# ENV http_proxy <HTTP_PROXY>
# ENV https_proxy <HTTPS_PROXY> 

# Includes 
source $(dirname $0)/__configurations.sh
source $(dirname $0)/common.sh

#--force-rm
docker build --force-rm \
    --tag "$PRODUCT_NAME:$PRODUCT_TAG" \
    --build-arg WSO2_IS_VERSION=$PRODUCT_VERSION \
    --build-arg WSO_SOURCE_URL=$PRODUCT_SOURCE_URL \
    --build-arg ENVIRONMENT=$PRODUCT_ENVIRONMENT . $*

# Wait for user action ensure that action doned successfully
read -rsp $'Press enter to continue...\n'
exit 0