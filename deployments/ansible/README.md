# Deployments

## Requirements
To launch deployments on dockers we need:
* Machine with docker installed
* docker network named wazuh_network 
``` docker network create --subnet=172.100.0.0/16 wazuh_network ```
* python installed in target machine
* docker-py/docker installed on machine
