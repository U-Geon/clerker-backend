package conference.clerker.global.exception.domain;

import conference.clerker.global.exception.CustomException;
import conference.clerker.global.exception.ErrorCode;

public class AuthException extends CustomException {
    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
