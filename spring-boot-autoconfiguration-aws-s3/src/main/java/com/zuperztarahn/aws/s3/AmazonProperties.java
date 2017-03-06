package com.zuperztarahn.aws.s3;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "amazon")
@Getter
@Setter
@ToString
public class AmazonProperties {

    @NestedConfigurationProperty
    private Aws aws;

    @NestedConfigurationProperty
    private S3 s3;


    @Getter
    @Setter
    @ToString
    public static class Aws {
        private String accessKeyId;
        private String accessKeySecret;
    }


    @Getter
    @Setter
    @ToString
    public static class S3 {
        private String defaultBucket;
    }

}