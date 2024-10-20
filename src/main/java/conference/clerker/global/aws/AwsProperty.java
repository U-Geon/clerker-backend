package conference.clerker.global.aws;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cloud.aws")
public class AwsProperty {

    @NotBlank
    private String region;

    @NestedConfigurationProperty
    private S3 s3;

    @NestedConfigurationProperty
    private Credentials credentials;

    @Data
    public static class S3 {

        @NotBlank
        private String bucket;
    }

    @Data
    public static class Credentials {

        @NotBlank
        private String accessKey;

        @NotBlank
        private String secretKey;
    }
}
