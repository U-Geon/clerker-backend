package conference.clerker.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH-001", "사용자를 찾을 수 없습니다."),
    ;
    // 추가적인 에러 커스터마이징 추가

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}