# GoCD Elastic agent plugin for AWS EC2

[GoCD](https://www.gocd.org) server plugin for bringing up Amazon EC2 instances as its agents on demand.  
Compatible with [version 5.0](https://plugin-api.gocd.org/19.3.0/elastic-agents/) of the elastic agent endpoint (GoCD server versions starting from 19.3.0).

Table of Contents
=================

  * [Installation](#installation)
    * [Amazon Machine Image](#amazon-machine-image)
    * [Security Groups](#security-groups)
    * [Subnets](#subnets)
  * [Building the code base](#building-the-code-base)
  * [Credits](#credits)
  * [Disclaimer](#disclaimer)

## Installation

Copy the file `build/libs/gocd-ec2-elastic-agent-plugin-VERSION.jar` to the GoCD server under `${GO_SERVER_DIR}/plugins/external`
and restart the server.

Prepare [AMI](#amazon-machine-image), [security groups](#security-groups) and [subnets](#subnets) for the agents.

Tested on GoCD server versions 19.11.0 and 19.12.0.

### Amazon Machine Image

This is the most important step, where you will prepare a base image for the agents. 
Create new clean EC2 instance and install there all the tools and configurations that your agents may need. 
After that, follow up [the official guide](https://docs.gocd.org/current/installation/install/agent/linux.html) to install Go-Agent. Do not connect it to the 
server yet, nor enable auto startup of go-agent.service! All this will be done by the plugin itself with the help of the user data scripts.
Before stopping this instance perform cleanup with the following commands:
```bash
rm -rf /var/lib/cloud/*
rm -rf /var/log/cloud-init*
rm -rf /usr/share/go-agent/go-agent.pid
rm -rf /usr/share/go-agent/config/*
rm -rf /var/log/go-agent/*
rm -rf /var/lib/go-agent/config/*
```
Finally, create new [Amazon Machine Image](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/AMIs.html) from your instance. Each Elastic Agent Profile can use
different AMI to suit your needs.

### Security Groups

You will need to setup some connectivity between your GoCD server and elastic agents. You may also want to allow any other inbound/outbound traffic to the
agents, like tunnels, version control systems or repositories. The most straightforward way to achieve this is with security groups.

Once you have all your security groups defined, put their identifiers into the Elastic Agent Profile and they will be automatically assigned to every newly
created EC2 instance.

### Subnets

To be able to launch new agents you need to have at least one subnet in your VPC where you will put your newly created instances. You can define several
subnets (ideally in different availability zones) in the elastic agent profile and the plugin will choose randomly one of them each time it has to create new
instance. If the chosen availability zone has run out of your requested instance type, the plugin will try to bring up instance in the next subnet.

Also, remember to enable auto-assign public IP address to the subnets.

## Building the code base

To build the jar, run `./gradlew clean assemble`

## Credits

This project is fully based on [GoCD Elastic agent plugin skeleton](https://github.com/gocd-contrib/elastic-agent-skeleton-plugin) and
[GoCD Elastic agent plugin for Docker](https://github.com/gocd-contrib/docker-elastic-agents). The structure and some parts of code are taken directly from
these projects.

## Disclaimer

The GoCD Elastic agent plugin for AWS EC2 is supplied "AS IS", use is at your own risk. Author and contributors expressly disclaim all warranties nor support of
any kind.

