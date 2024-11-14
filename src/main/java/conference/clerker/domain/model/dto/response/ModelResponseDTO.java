package conference.clerker.domain.model.dto.response;


public record ModelResponseDTO(
        String report,
        String stt,
        String diagram_image,
        Long meetingId
) {}