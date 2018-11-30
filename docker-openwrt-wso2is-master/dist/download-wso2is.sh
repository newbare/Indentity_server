#!/bin/bash
# Download and place wso2is packs in this dist/ directory to avoid multiple times download.

WSO2IS_VERSION=5.0.0

wget --progress=dot:giga  --user-agent="testuser" --referer="http://connect.wso2.com/wso2/getform/reg/new_product_download" http://product-dist.wso2.com/products/identity-server/$WSO2IS_VERSION/wso2is-$WSO2IS_VERSION.zip
