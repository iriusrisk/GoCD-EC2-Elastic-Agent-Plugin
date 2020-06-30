package com.continuumsecurity.elasticagent.ec2;

public interface Properties {

    String SERVER_URL = "https://your.gocd.server/";
    String ACCESS_KEY_ID = "ACCES_KEY_ID";
    String SECRET_ACCESS_KEY = "SECRET_ACCESS_KEY";
    String REGION = "default_aws_region";
    String AMI_ID = "ami-id";
    String KEY = "key";
    String SUBNETS = "subnet-1,subnet-2,subnet-3";
    String SG_IDS = "sg-IDS";
    String TYPE = "t3.nano";
    String USERDATA = "#!/bin/bash\n" + "echo hi";
    String INSTANCE_PROFILE = "arn:aws:iam::000000:instance-profile/InstanceProfile"
    String AUTO_REGISTER_TIMEOUT = "5";
    String MAX_ELASTIC_AGENTS = "50";
    String AUTO_REGISTER_KEY = "AUTO_REGISTER_KEY";

}
