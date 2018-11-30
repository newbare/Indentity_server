#!/bin/bash

# Includes 
source $(dirname $0)/__configurations.sh
source $(dirname $0)/common.sh

docker-compose up -d

# Wait for user action ensure that action doned successfully
read -rsp $'Press enter to continue...\n'
exit 0