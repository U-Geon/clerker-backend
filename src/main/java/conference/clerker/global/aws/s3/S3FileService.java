package conference.clerker.global.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import conference.clerker.global.aws.AwsProperty;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class S3FileService {

    private final AwsProperty awsProperty;
    private final AmazonS3 amazonS3;
    private final S3Presigner presigner;

    // 생성자에 @Qualifier를 추가하여 의존성을 주입
    public S3FileService(AwsProperty awsProperty,
                         @Qualifier("defaultS3Client") AmazonS3 amazonS3,
                         @Qualifier("otherAccountCredentialsProvider") AwsCredentialsProvider otherAccountCredentialsProvider) {
        this.awsProperty = awsProperty;
        this.amazonS3 = amazonS3;
        this.presigner = S3Presigner.builder()
                .credentialsProvider(otherAccountCredentialsProvider)
                .region(Region.of("other-account-region"))
                .build();
    }

    // Content-Type 매핑
    private static final Map<String, String> CONTENT_TYPE_MAP = new HashMap<>() {{
        put("jpg", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("png", "image/png");
        put("gif", "image/gif");
        put("txt", "text/plain");
        put("md", "text/markdown");
    }};

    // 파일 확장자를 통해 Content-Type 설정
    private String determineContentType(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return CONTENT_TYPE_MAP.getOrDefault(fileExtension, "application/octet-stream");
    }


    // 파일 업로드 (MultiPart)
    public String uploadFile(String folderPath, String fileName, MultipartFile file) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        // 폴더 경로를 포함하여 key 생성
        String key = folderPath + "/" + fileName;
        return uploadFile(key, file.getInputStream(), objectMetadata);
    }

    // 파일 업로드 (InputStream)
    public String uploadFile(String folderPath, String fileName, InputStream fileStream) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(determineContentType(fileName)); // 파일 분석해서 Content Type 지정
        objectMetadata.setContentLength(fileStream.available());

        String key = folderPath + "/" + fileName;
        return uploadFile(key, fileStream, objectMetadata);
    }


    //S3에 업로드
    private String uploadFile(String key, InputStream inputStream, ObjectMetadata objectMetadata) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                awsProperty.getS3().getBucket(),
                key,
                inputStream,
                objectMetadata
        );

        amazonS3.putObject(putObjectRequest);

        return amazonS3.getUrl(awsProperty.getS3().getBucket(), key).toString();
    }

    // 파일을 다운받을 임시 Presigned URL 생성
    private URL generatePresignedUrl(String bucketName, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(getObjectRequest)
                .build();

        return presigner.presignGetObject(presignRequest).url();
    }

    // 외부 S3에서 zip 파일을 다운로드하고, 압축을 풀어 각 파일을 내부 S3에 업로드한 뒤 URL 리스트 반환
    public List<String> transferZipContentFromOtherS3(String bucketName, String zipFileKey, String targetFolder) {
        List<String> uploadedFileUrls = new ArrayList<>();
        URL presignedUrl = generatePresignedUrl(bucketName, zipFileKey);

        try (InputStream zipInputStream = presignedUrl.openStream();
             ZipInputStream zis = new ZipInputStream(zipInputStream)) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                String fileName = zipEntry.getName(); // zip 내의 개별 파일 이름
                String uploadedFileUrl = uploadFile(targetFolder, fileName, zis); // 기존 메서드를 활용하여 업로드
                uploadedFileUrls.add(uploadedFileUrl);
                zis.closeEntry();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to transfer zip content from other S3", e);
        }
        return uploadedFileUrls;
    }

    // 외부 S3에서 개별 파일을 다운로드하고, 내부 S3에 업로드한 뒤 URL 반환
    public String transferFileFromOtherS3(String bucketName, String fileKey, String targetFolder) {
        // 외부 S3의 파일에 대한 Presigned URL 생성
        URL presignedUrl = generatePresignedUrl(bucketName, fileKey);

        // 파일 이름 추출
        String fileName = fileKey.substring(fileKey.lastIndexOf("/") + 1);

        try (InputStream inputStream = presignedUrl.openStream()) {
            // 내부 S3에 파일 업로드
            return uploadFile(targetFolder, fileName, inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to transfer file from other S3", e);
        }
    }

    // 파일 삭제
    public void deleteFile(String key) {
        amazonS3.deleteObject(awsProperty.getS3().getBucket(), key);
    }
}