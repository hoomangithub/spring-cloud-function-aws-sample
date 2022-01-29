package hooman.spring.cloud.function.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClientBuilder;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.SnsClientBuilder;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;


@Configuration
public class AWSConfig {


	private AppConfigProperties config;
	
    public AWSConfig(AppConfigProperties config) {
		this.config = config;
	}

	@Bean
    public S3Client s3Client() {
        S3ClientBuilder s3ClientBuilder = S3Client.builder().region(Region.EU_CENTRAL_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey())));
        return  s3ClientBuilder.build();
    }
    
    @Bean
    public SnsClient snsClient() {
        SnsClientBuilder SnsClientBuilder = SnsClient.builder().region(Region.EU_CENTRAL_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                		AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey())));
        return  SnsClientBuilder.build();
    }
    
    @Bean
    public SqsClient sqsClient() {
        SqsClientBuilder sqsClientBuilder = SqsClient.builder().region(Region.EU_CENTRAL_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                		AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey())));
        return sqsClientBuilder.build();
    }
    
    @Bean
    public SecretsManagerClient secretsClient() {
    	SecretsManagerClientBuilder secretsClientBuilder = SecretsManagerClient.builder().region(Region.EU_CENTRAL_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                		AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey())));
    	return secretsClientBuilder.build();
    }

}
