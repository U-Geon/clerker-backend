package conference.clerker.domain.model.dto.response;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record ModelResponseDTO(
        MultipartFile mdFile,
        List<MultipartFile> imageFiles
) {}