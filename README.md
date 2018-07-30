# Wazuh-QA

[![Email](https://img.shields.io/badge/email-join-blue.svg)](https://groups.google.com/forum/#!forum/wazuh)

Wazuh-QA is a repository focused on automation of deployments and tests of wazuh source code this improve our efficiency and give us the capability of detect errors fastly, we based our work on most relevant tools used today:

* Ansible, This tools let us to provisioning all machines or containers to set them usable and ready for testing, we use Ansible also to configure and launch our tests on machines.
* Vagrant, When virtual machines is needed we use vagrant, that simplify our deployment process and give us more time to test.
* Docker, The other way of virtualization, when virtual machines don't be necessary we use dockers containers with all tools needed to test a piece of code.
* Jenkins, This tool help us to automate all previous process and generate report logs of action taken in each machine, It's the heart of testing.

## Repository folder structure

├── deployments -> We store here all kind of deployments developed and tested.
│   ├── ansible -> In this folder we store Ansible playbooks for docker containers deployment.
│   │   ├── conf -> Wazuh agent and manager configuration.
│   │   └── default.json -> Contains the deployment information such as agents and managers required to deploy.
│   └── vagrant -> Vagrantfiles of diferent kinds of deployment.
│
├── dockerfiles -> Container building scripts, needed for testing purposes.
|
├── environments -> We store here all kind of environment/infrastructure where we are going to deploy.
│   └── env-docker -> Docker infrastructure (Servers and versions).
|
├── tests -> We store all kind of test developed (Configuration, and test), here we use Ansible+Jenkins+python/pytest.
│   ├── logcollector -> Tests relative to logcollector module.
│   │   └── 01-addlog -> Test of addlog feature.
│   │       ├── pipeline -> We store here jenkins files that defines process of testing.
│   │       ├── requiredfiles -> Here are files needed to do the test.
│   │       └── test -> files of testing and checks.
│   │           ├── ...
│   └── syscheck -> Tests relative to syscheck(FIM) module.
│       └── ...
|
└── tools -> We store here tools to transform vagrant or environment+deploy information to Ansible or JSON generic data.
    ├── generateInventory.py -> Generates Ansible inventory ready to work, It need mode of generation deploy/config and JSON with all data.
    ├── generateJSON.py -> Generates a JSON with all data, It need input of environment and deploy information in JSON.
    └── vagrantJSON2Inventory.py -> Generates Ansible inventory ready to work, It need a JSON that vagrant creates and path to private key


## Documentation

* [Full documentation](http://documentation.wazuh.com)

## Branches

* `master` branch contains the latest code, be aware of possible bugs on this branch.

## Contribute

If you want to contribute to our project please don't hesitate to send a pull request. You can also join our users [mailing list](https://groups.google.com/d/forum/wazuh), by sending an email to [wazuh+subscribe@googlegroups.com](mailto:wazuh+subscribe@googlegroups.com), to ask questions and participate in discussions.

## References

* [Wazuh website](http://wazuh.com)
