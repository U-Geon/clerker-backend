package conference.clerker.global.aws.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;

@Configuration
@RequiredArgsConstructor
public class OtherAccountS3Config {

    @Value("${other.aws.region}")
    private String otherRegion;

    @Value("${other.aws.assume-role-arn}")
    private String assumeRoleArn;

    // IAM 역할을 사용하여 자격 증명 공급자 생성
    @Bean
    public AwsCredentialsProvider otherAccountCredentialsProvider() {
        StsClient stsClient = StsClient.builder()
                .region(Region.of(otherRegion))
                .build();

        return StsAssumeRoleCredentialsProvider.builder()
                .stsClient(stsClient)
                .refreshRequest(req -> req.roleArn(assumeRoleArn).roleSessionName("session"))
                .build();
    }

    // 다른 계정의 S3Client를 통해 파일 업로드 및 다운로드
    @Bean
    public S3Client otherAccountS3Client(AwsCredentialsProvider otherAccountCredentialsProvider) {
        return S3Client.builder()
                .region(Region.of(otherRegion))
                .credentialsProvider(otherAccountCredentialsProvider)
                .build();
    }

    // Presigned URL 생성을 위한 S3Presigner
    @Bean
    public S3Presigner otherAccountS3Presigner(AwsCredentialsProvider otherAccountCredentialsProvider) {
        return S3Presigner.builder()
                .region(Region.of(otherRegion))
                .credentialsProvider(otherAccountCredentialsProvider)
                .build();
    }
}