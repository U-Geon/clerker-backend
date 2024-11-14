package conference.clerker.global.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sagemakerruntime.AmazonSageMakerRuntime;
import com.amazonaws.services.sagemakerruntime.AmazonSageMakerRuntimeClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonSageMakerRuntimeConfig {

    // AWS 자격 증명 및 리전 설정
    @Value("${other.aws.access-key}")
    private String accessKey;

    @Value("${other.aws.secret-key}")
    private String secretKey;

    @Value("${other.aws.region}")
    private String awsRegion;

    @Bean
    public AmazonSageMakerRuntime amazonSageMakerRuntime() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);

        // 클라이언트 구성 설정
        ClientConfiguration clientConfig = new ClientConfiguration()
                .withConnectionTimeout(10_000) // 연결 타임아웃 설정 (예: 10초)
                .withSocketTimeout(300_000)    // 소켓 타임아웃 설정 (예: 5분)
                .withMaxErrorRetry(0);         // 자동 재시도 비활성화

        return AmazonSageMakerRuntimeClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withClientConfiguration(clientConfig)
                .withRegion(awsRegion)
                .build();
    }
}
