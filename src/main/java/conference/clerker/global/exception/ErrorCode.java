package conference.clerker.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ErrorCode {

    BODY_VALUE_NOT_FOUND(HttpStatus.BAD_REQUEST, "ERROR-001", "Request Body에 누락된 정보가 있습니다."),
    BODY_NOT_EMPTY(HttpStatus.BAD_REQUEST, "ERROR-002", "Container Type이 비어있습니다."),

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH-001", "사용자를 찾을 수 없습니다."),

    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT-001", "프로젝트를 찾을 수 없습니다."),

    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHEDULE-001", "스케쥴을 찾을 수 없습니다."),
    DUPLICATE_TIME(HttpStatus.CONFLICT, "SCHEDULE-002", "중복된 시간은 스케쥴에 등록할 수 없습니다."),

    MEETING_NOT_FOUND(HttpStatus.NOT_FOUND, "MEETING-001", "회의를 찾을 수 없습니다"),

    ORGANIZATION_NOT_FOUND(HttpStatus.NOT_FOUND, "ORGANIZATION-001", "소속 멤버를 찾을 수 없습니다."),
    DUPLICATED_ORGANIZATION(HttpStatus.BAD_REQUEST, "ORGANIZATION-002", "이미 가입된 멤버입니다."),
    FORBIDDEN_MEMBER(HttpStatus.FORBIDDEN, "ORGANIZATION-003", "해당 권한으로는 접근이 불가능합니다.")

    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}