WSO2 Identity Server 5.0.0 as a Docker container. For development use only.

## Quickstart
You need to provide a repository folder for this Docker at runtime.
So, create a folder on Docker host.
for example :
```
mkdir /my-repository
```
If you leave it empty at runtime it will fill with the respository folder of downloaded `wso2is-#.#.#.zip` file for first time.
If you have a pre-configured WSO2IS you can copy repository folder of it into new created `/my-repository` foldr.

Then, run it with this command:
```
docker run -v /my-repository:/repository -d mcreations/openwrt-wso2is
```
For running the server in debug mode you can use --env switch to run in debug mode:
```
docker run -v /my-repository:/repository --env WSO2IS_RUN_MODE=debug -d mcreations/openwrt-wso2is
```
which will run the docker in debug mode on port 5005 and for using another debug port:
```
docker run -v /my-repository:/repository --env WSO2IS_RUN_MODE=debug --env WSO2IS_DEBUG_PORT=8000 -d mcreations/openwrt-wso2is
```
in debug mode server wait to connecting the remote debugger before start.

You can use an internet browser for checking the run docker as follows:
```
https://docker-vnet-ip:9443/
https://docker-vnet-ip:9763/
```
the `docker-vnet-ip` ip address comes from ip address of docker0 interface. use ifconfig to obtain it.

## Distribution Folder
To avoid downloading the artifact from wso2.org multiple times, you can download it manually into dist folder, you can use `download-wso2is.sh` inside dist for download the artifact .
```
cd /path/to/yours/docker-opewrt-wso2is/dist
sh download-wso2is.sh
```
You can modify artifact version in download-wso2is.sh file, the default version is 5.0.0.

## Build
In the root of cloned project run following command 
```
docker build -t mcreations/openwrt-wso2is .
```