package conference.clerker.global.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
@Transactional
public class S3FileService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 버킷의 폴더 내 파일 삭제.
    public void deleteFile(String dir, String filename) {
        try {
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket,  dir + "/" + filename);
            amazonS3.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file to S3: " + e.getMessage());
        }
    }

    // 버킷의 해당 폴더에 파일 저장.
    public String createFile(String dir, MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3.putObject(bucket, dir + "/" + fileName, file.getInputStream(), metadata);

            return amazonS3.getUrl(bucket, dir + "/" + fileName).toString();
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to S3: " + e.getMessage());
        }
    }
}
