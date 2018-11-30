FROM openjdk:7
MAINTAINER Romeo KONE <Romeo.Kone@fircosoft.com>

# ARGS
ARG WSO2_IS_VERSION=5.3.0-alpha2
ARG ENVIRONMENT=PRODUCTION
ARG WSO_SOURCE_URL=https://github.com/wso2/product-is/releases

# WSO2 Identity Server version
ENV WSO2_IS_VERSION ${WSO2_IS_VERSION}
# WSO2 Identity Server home
ENV IS_HOME=/opt/wso2is-$WSO2_IS_VERSION
# Environment
ENV ENVIRONMENT ${ENVIRONMENT}

# Update aptitude with new repo
RUN apt-get update

# ---------------------------------------------------
# WSO2 Identity Server Installation
# - Download a zip 
# - Unzip the downloaded zip 
# - Delete the downloaded zip (clean) 
# ---------------------------------------------------
RUN wget $WSO_SOURCE_URL/download/v$WSO2_IS_VERSION/wso2is-$WSO2_IS_VERSION.zip -P /opt \
    && unzip /opt/wso2is-$WSO2_IS_VERSION.zip -d /opt/ \
    && rm -rf /opt/wso2is-$WSO2_IS_VERSION.zip


# ---------------------------------------------------
# WSO2 Identity Server Configuration
# - Clone conf from a repository 
# - Backup old conf directory  
# - Move new conf to wso2 identity server  
# - Delete clonned repository (clean) 
# ---------------------------------------------------
RUN git clone https://github.com/Nakalhouele/wso2is-5.3.0-alpha2.git /opt/_tmp
RUN mv -f $IS_HOME/repository/conf $IS_HOME/repository/conf.old
RUN mv -f /opt/_tmp/conf $IS_HOME/repository/ 
RUN rm -rf /opt/_tmp 

# !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
# !!!!!!!!!!!!!!!!!! JUST FOR DEV !!!!!!!!!!!!!!!!!!!
# 
#RUN apt-get install nano vim 

# Define data volumes
#VOLUME ["$IS_HOME/repository/conf"]

# !!!!!!!!!!!!!!!!!!!!! END !!!!!!!!!!!!!!!!!!!!!!!!!
# !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


# Expose WSO2 Identity Server ports
EXPOSE 9443
EXPOSE 9763
EXPOSE 8000
EXPOSE 10500

WORKDIR $IS_HOME/bin/

# Start WSO2 Identity Server
ENTRYPOINT  ["./wso2server.sh"]