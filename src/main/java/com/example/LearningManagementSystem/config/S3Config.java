package com.example.LearningManagementSystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration

public class S3Config {
	
	@Value("${aws.accessKeyId}")
	private String access_key_id;
	
	@Value("${aws.secretKey}")
	private String secret_key;
	
	@Value("${aws.region}")
	private String aws_region;
	
	@Bean
	public S3Client s3Client() {
		
		AwsBasicCredentials awsBasicCredentials =  AwsBasicCredentials.create(access_key_id, secret_key);
		
		return S3Client.builder()
						.region(Region.of(aws_region))
						.credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
						.build();
	}

}
