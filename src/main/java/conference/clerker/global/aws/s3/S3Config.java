package conference.clerker.global.aws.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import conference.clerker.global.aws.AwsProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final AwsProperty awsProperty;

    @Bean
    public AWSCredentialsProvider awsCredentials() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(
                awsProperty.getCredentials().getAccessKey(),
                awsProperty.getCredentials().getSecretKey()
        );
        return new AWSStaticCredentialsProvider(awsCredentials);
    }

    @Bean
    @Qualifier("defaultS3Client") //스프링 서버와 연결된 S3
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(awsProperty.getRegion())
                .withCredentials(awsCredentials())
                .build();
    }
}
