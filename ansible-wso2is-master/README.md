ansible wso2is
=========
A role that installs an instance of the specified WSO2 product on CentOS. An example would be the [WSO2 Identity Server](http://wso2.com/products/identity-server/). This role installs the instance as a service that will start on boot.

Requirements
------------
Created and validated to run on Ansible v2.5.0

This role assumes that a Java JDK has already been installed on the target machine. If it doesn't already exist, a new service user will be created. By default, this user is created as `wso2`. The installation zip file is not downloaded from the WSO2 website. It must be provided and is assumed to be named as `<wso2app>-<wso2_version>.zip` in the `files` directory. This can be overwritten by setting the `wso2_app_archive` variable.

Role Variables
--------------
The following variables are defined in the `defaults/main.yml` file.

    ---
    # defaults file for ansible-wso2esb
    wso2_user: inope
    carbon_base: "/import/software/{{wso2_app}}"
    carbon_home: "{{carbon_base}}/current"
    
    # these are the default ports for wso2 products
    wso2_default_https_port: 9443
    wso2_default_http_port: 9763
    
    #the following is written into the init.d file for the service
    java_home: "/import/software/java17_01"
    
    # wso2
    wso2_app: wso2is
    wso2_app_version: 5.3.0
    wso2_app_directory: "{{wso2_app}}-{{ wso2_app_version }}"
    wso2_app_offset: 0
    wso2_app_https_port: "{{ (wso2_default_https_port|int) + (wso2_app_offset|int) }}"
    wso2_app_http_port: "{{ (wso2_default_http_port|int) + (wso2_app_offset|int) }}"
    wso2_app_archive: "{{wso2_app}}-{{wso2_app_version}}.zip"

Dependencies
------------
N/A

Example Playbook
----------------

    ---
    - hosts: *
      roles:
        - { role: ansible-wso2esb, wso2_app: "wso2is", wso2_app_version: "5.3.0"}

License
-------
MIT
