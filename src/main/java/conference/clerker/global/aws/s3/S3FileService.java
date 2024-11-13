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

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
                .region(Region.of("us-west-2"))
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
    //TODO 메타데이터 오류 분석 필요
    private String uploadFile(String key, InputStream inputStream, ObjectMetadata objectMetadata) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                awsProperty.getS3().getBucket(),
                key,
                inputStream,
                null
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

    // 외부 S3에서 파일을 InputStream으로 읽어오는 메서드
    private InputStream getInputStreamFromOtherS3(String bucketName, String fileKey) {
        URL presignedUrl = generatePresignedUrl(bucketName, fileKey);
        try {
            return presignedUrl.openStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 외부 S3에서 zip 파일을 다운로드하고, 압축을 풀어 각 파일을 내부 S3에 업로드한 뒤 URL 리스트 반환
    public Map<String, String> transferZipContentFromOtherS3(String bucketName, String zipFileKey, String targetFolder, Long meetingId) {
        Map<String, String> uploadedFileUrlMap = new HashMap<>();
        URL presignedUrl = generatePresignedUrl(bucketName, zipFileKey);

        try (InputStream zipInputStream = presignedUrl.openStream();
             ZipInputStream zis = new ZipInputStream(zipInputStream)) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                String originalFile = entryName.substring(entryName.lastIndexOf("/") + 1);

                // 특정 파일만 업로드하고 디렉토리나 MACOSX 파일을 무시
                if (entryName.startsWith("__MACOSX") || entryName.startsWith("._") || zipEntry.isDirectory()) {
                    zis.closeEntry();
                    continue;
                }

                // entry 데이터를 ByteArrayOutputStream에 복사하여 스트림 소모 방지
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                zis.transferTo(byteArrayOutputStream);

                // ByteArrayOutputStream의 데이터를 다시 InputStream으로 변환하여 S3에 업로드
                String fileName = meetingId + "_" + entryName;
                try (InputStream entryStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray())) {
                    String uploadedFileUrl = uploadFile(targetFolder, fileName, entryStream);
                    uploadedFileUrlMap.put(originalFile, uploadedFileUrl);
                }

                // entry가 끝났으니 다음 entry로 이동
                zis.closeEntry();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to transfer zip content from other S3", e);
        }
        return uploadedFileUrlMap;
    }

    // 외부 S3에서 개별 파일을 다운로드하고, 내부 S3에 업로드한 뒤 URL 반환
    public String transferFileFromOtherS3(String bucketName, String fileKey, String targetFolder, Long meetingId) {
        // 외부 S3의 파일에 대한 Presigned URL 생성
        URL presignedUrl = generatePresignedUrl(bucketName, fileKey);

        // 파일 이름 추출
        String fileName = fileKey.substring(fileKey.lastIndexOf("/") + 1);
        fileName = meetingId + "_" + fileName;

        try (InputStream inputStream = presignedUrl.openStream()) {
            // 내부 S3에 파일 업로드
            return uploadFile(targetFolder, fileName, inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to transfer file from other S3", e);
        }
    }

    public String processAndUploadMarkdownFile(
            String bucketName,
            String markdownFileKey,
            String targetFolder,
            Long meetingId,
            Map<String, String> imageUrlMap) {
        // 1. 외부 S3에서 마크다운 파일을 InputStream으로 읽어옴
        InputStream markdownInputStream = getInputStreamFromOtherS3(bucketName, markdownFileKey);
        if (markdownInputStream == null) {
            throw new RuntimeException("Failed to read markdown file from other S3");
        }

        try {
            // 2. 마크다운 파일 내용 처리
            String processedContent = processMarkdownContent(markdownInputStream, imageUrlMap);

            // 3. 처리된 마크다운 파일을 우리 서버의 S3에 업로드
            InputStream processedInputStream = new ByteArrayInputStream(processedContent.getBytes(StandardCharsets.UTF_8));
            String fileName = meetingId + "_" + markdownFileKey.substring(markdownFileKey.lastIndexOf("/") + 1);
            String uploadedMarkdownUrl = uploadFile(targetFolder, fileName, processedInputStream);

            return uploadedMarkdownUrl;
        } catch (IOException e) {
            throw new RuntimeException("Failed to process and upload markdown file", e);
        }
    }

    // report.md 파일의 이미지 경로 수정
    private String processMarkdownContent(InputStream markdownInputStream, Map<String, String> imageUrlMap) throws IOException {
        // InputStream을 문자열로 변환
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(markdownInputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        }
        String content = contentBuilder.toString();

        // 정규식을 사용하여 이미지 태그 찾기
        Pattern pattern = Pattern.compile("!\\[(.*?)\\]\\(([^\\)]+)\\)");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String altText = matcher.group(1);
            String imagePath = matcher.group(2); // 이미지의 상대 경로 또는 파일명
            String originalMarkdownImageTag = matcher.group(0);

            // 이미지 파일명 추출 (예: chunk_1.png)
            String imageFileName = imagePath.substring(imagePath.lastIndexOf('/') + 1);

            // 매핑된 S3 URL 가져오기
            String uploadedImageUrl = imageUrlMap.get(imageFileName);

            if (uploadedImageUrl != null) {
                // 마크다운 내용에서 이미지 경로를 업로드된 URL로 대체
                String newMarkdownImageTag = String.format("![%s](%s)", altText, uploadedImageUrl);
                content = content.replace(originalMarkdownImageTag, newMarkdownImageTag);
            } else {
                System.out.println("업로드된 이미지 URL을 찾을 수 없습니다: " + imageFileName);
            }
        }

        return content;
    }

    // 파일 삭제
    public void deleteFile(String key) {
        amazonS3.deleteObject(awsProperty.getS3().getBucket(), key);
    }
}