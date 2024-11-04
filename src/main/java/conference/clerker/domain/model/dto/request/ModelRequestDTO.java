package conference.clerker.domain.model.dto.request;

import java.util.List;

public record ModelRequestDTO(
        List<String> keywords,
        String mp3FileUrl
) {}
