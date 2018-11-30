FROM mcreations/openwrt-java:7

MAINTAINER Kambiz Darabi <darabi@m-creations.net>
MAINTAINER Reza Rahimi <rahimi@m-creations.net>



ENV WSO2IS_VERSION 5.0.0
ENV WSO2IS_HOME /opt/wso2is-${WSO2IS_VERSION}
ENV WSO2IS_HOME_REPOSITORY /opt/wso2is-${WSO2IS_VERSION}/repository

ENV MOUNTED_REPOSITORY_DIR /repository
ENV DIST_DIR /mnt/packs

# normal, debug
ENV WSO2IS_RUN_MODE=normal
ENV WSO2IS_DEBUG_PORT=5005

ADD image/root /

RUN mkdir -p /mnt/packs

ADD dist/ /mnt/packs

# Download WSO2IS and installing it
RUN mkdir -p ${WSO2IS_HOME}  &&  mkdir -p ${MOUNTED_REPOSITORY_DIR} && \
  opkg update && \
  opkg install unzip && \
  ([ -f $DIST_DIR/wso2is-${WSO2IS_VERSION}.zip ] ||  wget -O $DIST_DIR/wso2is-${WSO2IS_VERSION}.zip --progress=dot:giga  --user-agent="testuser" --referer="http://connect.wso2.com/wso2/getform/reg/new_product_download" http://product-dist.wso2.com/products/identity-server/${WSO2IS_VERSION}/wso2is-${WSO2IS_VERSION}.zip) && \
  unzip $DIST_DIR/wso2is-${WSO2IS_VERSION}.zip -d /tmp && \
  rm $DIST_DIR/wso2is-${WSO2IS_VERSION}.zip && \
  mv -f /tmp/wso2is-* /opt/ && \
  echo "export PATH=$PATH:$JAVA_HOME/bin/bundled:${WSO2IS_HOME}/bin" >> /etc/profile && \
  echo "export CARBON_HOME=${WSO2IS_HOME}" >> /etc/profile

# Expose ports
EXPOSE 9443 9763

CMD ["/start-wso2is"]