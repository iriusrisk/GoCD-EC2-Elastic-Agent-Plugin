package com.continuumsecurity.elasticagent.ec2;

import com.continuumsecurity.elasticagent.ec2.models.JobIdentifier;
import com.continuumsecurity.elasticagent.ec2.requests.CreateAgentRequest;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.mock;

public class Test {

    public static void main(String[] args) {

        List<String> items = Arrays.asList(Properties.SUBNETS.split("\\s*,\\s*"));
        System.out.println(items.get(new Random().nextInt(items.size())));

        Map<String, String> properties = new HashMap<>();
        properties.put("ec2_ami", Properties.AMI_ID);
        properties.put("ec2_instance_type", Properties.TYPE);
        properties.put("ec2_sg", Properties.SG_IDS);
        properties.put("ec2_subnets", Properties.SUBNETS);
        properties.put("ec2_key", Properties.KEY);
        properties.put("ec2_user_data", Properties.USERDATA);
        properties.put("ec2_instance_profile", Properties.INSTANCE_TYPE);


        JobIdentifier jobIdentifier = new JobIdentifier(
                "examplePipeName",
                45L,
                "examplePipeLabel",
                "exampleStageName",
                "stage-6",
                "exampleJobName",
                68L
        );

        ClusterProfileProperties settings = new ClusterProfileProperties();
        settings.setGoServerUrl(Properties.SERVER_URL);
        settings.setAutoRegisterTimeout(Properties.AUTO_REGISTER_TIMEOUT);
        settings.setMaxElasticAgents(Properties.MAX_ELASTIC_AGENTS);
        settings.setAwsAccessKeyId(Properties.ACCESS_KEY_ID);
        settings.setAwsSecretAccessKey(Properties.SECRET_ACCESS_KEY);
        settings.setAwsRegion(Properties.REGION);

        CreateAgentRequest createAgentRequest = new CreateAgentRequest(
                Properties.AUTO_REGISTER_KEY,
                properties,
                jobIdentifier,
                settings
        );

        Ec2Instance ec2Instance = Ec2Instance.create(createAgentRequest, settings, mock(ConsoleLogAppender.class));
        //*System.out.println(ec2Instance.toString());

        ConcurrentHashMap<String, Ec2Instance> instances = new ConcurrentHashMap<>();
        instances.put(ec2Instance.id(), ec2Instance);

        System.out.println(instances.get(ec2Instance.id()));
        System.out.println("");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}



        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(Properties.ACCESS_KEY_ID, Properties.SECRET_ACCESS_KEY);
        Ec2Client ec2 = Ec2Client.builder()
                .region(Region.EU_WEST_1)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();

        DescribeInstancesRequest request = DescribeInstancesRequest.builder()
                .filters(
                        Filter.builder()
                                .name("instance-state-name")
                                .values("running","pending","terminated")
                                .build(),
                        Filter.builder()
                                .name("tag:type")
                                .values(Constants.ELASTIC_AGENT_TAG)
                                .build()
                )
                .build();

        DescribeInstancesResponse response = ec2.describeInstances(request);
        int count = 0;
        for(Reservation reservation : response.reservations()) {
            count += reservation.instances().size();
            for(Instance instance : reservation.instances()) {
                System.out.printf(
                        "Found reservation with id %s, " +
                                "AMI %s, " +
                                "type %s, " +
                                "state %s",
                        instance.instanceId(),
                        instance.imageId(),
                        instance.instanceType(),
                        instance.state().name()
                );
                System.out.println("TAGS:");
                for (Tag tag : instance.tags()) {
                    System.out.println(tag.toString());
                }
                JobIdentifier ji = JobIdentifier.fromJson(getTag(instance.tags(),"JsonJobIdentifier"));
                System.out.println("");
            }
        }
        System.out.println(count+" instances found");

        ec2Instance.terminate(settings);

    }

    @Nullable
    private static String getTag(List<Tag> tags, String key) {
        for (Tag tag : tags) {
            if (tag.key().equals(key)) {
                return tag.value();
            }
        }
        return null;
    }

}
