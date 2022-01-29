package hooman.spring.cloud.function.example.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.s3.event.S3EventNotification;
//import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.google.gson.Gson;

import hooman.spring.cloud.function.example.config.AppConfigProperties;
import hooman.spring.cloud.function.example.model.ResponseObject;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.DecryptionFailureException;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.InternalServiceErrorException;
import software.amazon.awssdk.services.secretsmanager.model.InvalidParameterException;
import software.amazon.awssdk.services.secretsmanager.model.InvalidRequestException;
import software.amazon.awssdk.services.secretsmanager.model.ResourceNotFoundException;

@Component
@DependsOn({"s3Client", "snsClient", "sqsClient", "secretsClient"})
public class AWSUtil {


    //private String bucketName ="log.hooman.link";


	private AppConfigProperties config;
	
	private SecretsManagerClient secretsClient;

    private S3Client s3Client;

    public AWSUtil(AppConfigProperties config, S3Client s3Client, SecretsManagerClient secretsClient) {
    	this.config = config;
        this.s3Client = s3Client;
        this.secretsClient = secretsClient;
    }

    
    public ResponseObject readSecret(String secretName) {
    	 
    	GetSecretValueRequest secretReq = GetSecretValueRequest.builder().secretId(secretName).build();
    	
		GetSecretValueResponse valueResponse = null;
	    try {
	    	valueResponse  = secretsClient.getSecretValue(secretReq);
	    } catch (DecryptionFailureException e) {
	        // Secrets Manager can't decrypt the protected secret text using the provided KMS key.
	        // Deal with the exception here, and/or rethrow at your discretion.
	        throw e;
	    } catch (InternalServiceErrorException e) {
	        // An error occurred on the server side.
	        // Deal with the exception here, and/or rethrow at your discretion.
	        throw e;
	    } catch (InvalidParameterException e) {
	        // You provided an invalid value for a parameter.
	        // Deal with the exception here, and/or rethrow at your discretion.
	        throw e;
	    } catch (InvalidRequestException e) {
	        // You provided a parameter value that is not valid for the current state of the resource.
	        // Deal with the exception here, and/or rethrow at your discretion.
	        throw e;
	    } catch (ResourceNotFoundException e) {
	        // We can't find the resource that you asked for.
	        // Deal with the exception here, and/or rethrow at your discretion.
	        throw e;
	    }

	   	 String secret = null; 
	   	 String decodedBinarySecret = null;
	    // Decrypts secret using the associated KMS CMK.
	    // Depending on whether the secret is a string or binary, one of these fields will be populated.
	    if (valueResponse.secretString() != null) {
	        secret = valueResponse.secretString();
	    } else {
	        decodedBinarySecret = new String(Base64.getDecoder().decode(valueResponse.secretBinary().asByteBuffer()).array());
	    }

	    ResponseObject responseObject = new Gson().fromJson(secret, ResponseObject.class);

    	return responseObject;
    }
    
    public void writeS3Event(S3Event s3Event) {
    	String bucketName = s3Event.getRecords().get(0).getS3().getBucket().getName();
    	String fileName = s3Event.getRecords().get(0).getS3().getObject().getKey();
    	writeEvent("S3Event", bucketName, fileName);
    }
    
    
    public void writeSNSEvent(SNSEvent snsEvent) {
    	S3EventNotification notification = S3EventNotification.parseJson(snsEvent.getRecords().get(0).getSNS().getMessage());
    	String bucketName = notification.getRecords().get(0).getS3().getBucket().getName();
    	String fileName = notification.getRecords().get(0).getS3().getObject().getKey();
    	writeEvent("SNSEvent", bucketName, fileName);
    }

    public void writeSQSEvent(SQSEvent sqsEvent) {
    	S3EventNotification notification = S3EventNotification.parseJson(sqsEvent.getRecords().get(0).getBody());
    	String bucketName = notification.getRecords().get(0).getS3().getBucket().getName();
    	String fileName = notification.getRecords().get(0).getS3().getObject().getKey();
    	writeEvent("SQSEvent", bucketName, fileName);
    	
    }
    
    private void writeEvent(String eventName, String bucketName, String fileName) {
    	StringBuffer strBuff = new StringBuffer();
    	strBuff.append("Event Name: ");
    	strBuff.append(eventName);
    	strBuff.append("\r\n");
    	strBuff.append("Bucket Name: ");
    	strBuff.append(bucketName);
    	strBuff.append("\r\n");
    	strBuff.append("File Name: ");
    	strBuff.append(fileName);
    	byte[] content = strBuff.toString().getBytes(StandardCharsets.UTF_8);
        writeFileOnS3("","event_" + eventName + ".txt", content);
    }
    
    
    private void writeFileOnS3(String ordnerName, String fileName, byte[] content) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type","plain/text");
        metadata.put("Content-Length", String.valueOf(content.length));
        metadata.put("title", fileName);
        String fileObjKeyName = fileName;
        if(ordnerName != null && ordnerName.length() > 3) {
            fileObjKeyName = ordnerName + "/" + fileName;
        }

        PutObjectRequest request = PutObjectRequest.builder().bucket(config.getBucketName()).key(fileObjKeyName).metadata(metadata).build();
        PutObjectResponse response = s3Client.putObject(request, RequestBody.fromBytes(content));
    }

}
