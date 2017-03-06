package com.zuperztarahn.aws.s3;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;

@Component
public class AmazonS3Template {

    private String defaultBucket;
    private String accessKeyId;
    private String accessKeySecret;
    private Credentials sessionCredentials;

    public AmazonS3Template(String defaultBucket, String accessKeyId, String accessKeySecret) {
        this.defaultBucket = defaultBucket;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
    }

    public PutObjectResult save(String key, File file) {
        return getAmazonS3Client().putObject(new PutObjectRequest(defaultBucket, key, file));
    }

    public S3Object get(String key) {
        return getAmazonS3Client().getObject(defaultBucket, key);
    }

    public AmazonS3 getAmazonS3Client() {
        BasicSessionCredentials basicSessionCredentials = getBasicSessionCredentials();
        return new AmazonS3Client(basicSessionCredentials);
    }

    private BasicSessionCredentials getBasicSessionCredentials() {
        if (sessionCredentials == null || sessionCredentials.getExpiration().before(new Date()))
            sessionCredentials = getSessionCredentials();

        return new BasicSessionCredentials(sessionCredentials.getAccessKeyId(),
                sessionCredentials.getSecretAccessKey(), sessionCredentials.getSessionToken());
    }

    private Credentials getSessionCredentials() {
        AWSSecurityTokenServiceClient stsClient =
                new AWSSecurityTokenServiceClient(new BasicAWSCredentials(accessKeyId, accessKeySecret));

        GetSessionTokenRequest getSessionTokenRequest =
                new GetSessionTokenRequest().withDurationSeconds(43200);

        sessionCredentials = stsClient.getSessionToken(getSessionTokenRequest).getCredentials();

        return sessionCredentials;
    }
}
