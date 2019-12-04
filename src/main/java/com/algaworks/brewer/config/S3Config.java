package com.algaworks.brewer.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(value = {"file://${HOME}/.brewer-s3.properties"},ignoreResourceNotFound = true)
public class S3Config {

    @Autowired
    private Environment env;

    @Bean
    public AmazonS3 amazonS3() {
        AWSCredentials credentials = new BasicAWSCredentials(env.getProperty("AWS_ACCESS_KEY_ID"),
                env.getProperty("AWS_SECRET_ACCESS_KEY"));

        AmazonS3 amazonS3 = new AmazonS3Client(credentials,new ClientConfiguration());
        Region region = Region.getRegion(Regions.US_EAST_1);
        amazonS3.setRegion(region);
        return amazonS3;
    }
}
