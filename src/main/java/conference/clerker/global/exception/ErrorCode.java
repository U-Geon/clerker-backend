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

    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}