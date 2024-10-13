package conference.clerker.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH-001", "사용자를 찾을 수 없습니다."),

    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT-001", "프로젝트를 찾을 수 없습니다."),

    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHEDULE-001", "스케쥴을 찾을 수 없습니다."),

    MEETING_NOT_FOUND(HttpStatus.NOT_FOUND, "MEETING-001", "회의를 찾을 수 없습니다"),

    ORGANIZATION_NOT_FOUND(HttpStatus.NOT_FOUND, "ORGANIZATION-001", "소속 멤버를 찾을 수 없습니다."),
    DUPLICATED_ORGANIZATION(HttpStatus.BAD_REQUEST, "ORGANIZATION-002", "이미 가입된 멤버입니다."),
    FORBIDDEN_MEMBER(HttpStatus.FORBIDDEN, "ORGANIZATION-003", "해당 권한으로는 접근이 불가능합니다.")

    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}