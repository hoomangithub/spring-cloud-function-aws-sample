package hooman.spring.cloud.function.example;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import hooman.spring.cloud.function.example.config.AppConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import hooman.spring.cloud.function.example.model.ResponseObject;
import hooman.spring.cloud.function.example.model.RequestObject;
import hooman.spring.cloud.function.example.service.AWSUtil;


/**
 * TEST mit RestClinet
 * Header:
 * 		Content-Type: application/json
 * 		spring.cloud.function.definition: Methodenname z.B runFunctionToUpperCase|runFunctionAndThen
 * Body:
 * 		{"id":120,"name": "Humi","nachname": "Paki"}
 */


// https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/example_code
// https://github.com/aws/aws-sdk-java-v2/blob/master/docs/LaunchChangelog.md


@SpringBootApplication
public class FunctionConfiguration {

	private AWSUtil awsUtil;

	public FunctionConfiguration(AWSUtil awsUtil) {
		this.awsUtil = awsUtil;
	}

	public static void main(String[] args) {
		SpringApplication.run(FunctionConfiguration.class, args);
	}

	@Bean
	public Function<SNSEvent, String> s3EventInSNS() {
		return snsEvent -> {
			awsUtil.writeSNSEvent(snsEvent);
			return "Okay";
		};
	}

	@Bean
	public Function<S3Event, String> s3EventInLambda() {
		return s3Event -> {
			awsUtil.writeS3Event(s3Event);
			return "Okay";
		};
	}
	
	@Bean
	public Function<SQSEvent, String> s3EventInSQS() {
		return sqsEvent -> {
			awsUtil.writeSQSEvent(sqsEvent);
			return "Okay";
		};
	}
	
	
	@Bean
	public Function<String, ResponseObject> readeSecret() {
		return secretName -> {
			return awsUtil.readSecret(secretName);
		};
	}

	@Bean
	public Supplier<String> getString(){
		return () -> {
			return "Hallo Spring Cloud Function";
		};
	}

	@Bean
	public Supplier<RequestObject> runSupplier(){
		return () -> {
			return new RequestObject(123456, "Vorname", "Nachname");
		};
	}

	@Bean
	public Consumer<String> runConsumer() {
		return payload -> System.out.println("Payload: "+ payload);
	}

	@Bean
	public Function<RequestObject, ResponseObject> runFunctionReqRes() {
		return requestObject -> {
			return new ResponseObject(requestObject.getId(), requestObject.getName() + " " + requestObject.getNachname());
		};
	}

	@Bean
	public Function<String, String> runFunctionToUpperCase() {
		return value -> {
			return value.toUpperCase();
		};
	}

	@Bean
	public Function<String, String> runFunctionAndThen() {
		return value -> {
			return value.replaceAll(":", " => ");
		};
	}

}
