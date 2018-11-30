#!/bin/bash
# --------------------------------------------------------------
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#
# --------------------------------------------------------------

# run script sets the configurable parameters for the cartridge agent in agent.conf and
# starts the cartridge agent process.
LOG=/tmp/script.log
MOUNT_DIR=${MOUNT_DIR}

echo "NAME=${NAME}" >> /etc/environment

echo "MOUNT_DIR=$MOUNT_DIR" >> $LOG

echo "copying patches into the server" >> $LOG
cp -r $MOUNT_DIR/patches/* ${CARBON_HOME}/repository/components/patches

echo "copying lib into the server" >> $LOG
cp -r $MOUNT_DIR/lib/* ${CARBON_HOME}/repository/components/lib


echo "copying keystore into the server" >> $LOG
cp $MOUNT_DIR/keys/wso2carbon.jks ${CARBON_HOME}/repository/resources/security/wso2carbon.jks
sudo chmod +x ${CARBON_HOME}/repository/resources/security/wso2carbon.jks

echo "Starting WSO2 ESB..." >> $LOG
${CARBON_HOME}/bin/wso2server.sh -Dsetup
