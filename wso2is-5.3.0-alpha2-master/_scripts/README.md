>##################################################################
>## WIP
>##################################################################

# Docker image of WSO2 Identity Server.

## Tags
- 5.3.0-alpha2 (latest)

## Description

### Files
>        - _scripts |
>                   |- __configurations.sh
>                   |- build-dockerfile.sh
>                   |- common.sh
>                   |- compose.sh
>                   |- docker-compose.yml
>                   |- Dockerfile
>                   |- run-dockerfile.sh


## Usage

## 1. Build 

* Build image from Dockerfile 

        ./build.sh

* Build image command line

        docker build . 

## 2. Run an Image

        ./run.sh

## 3. Using docker-compose



## Access

### External
        https://docker_machine:32790/carbon

### Internal
    
        https://localhost:9443/carbon




## RUN SH
        



#############################################################################
# How to connect to a docker container from outside the host (same network)
 [Windows]

        1. Open Oracle VM VirtualBox Manager
        2. Select the VM used by Docker
        3. Click Settings -> Network
        4. Adapter 1 should (default?) be "Attached to: NAT"
        5. Click Advanced -> Port Forwarding
        6. Add rule: Protocol TCP, Host Port 8080, Guest Port 8080 (leave 
           Host IP and Guest IP empty)
        7. You should now be able to browse to your container via 
           localhost:8080 and your-internal-ip:8080.


