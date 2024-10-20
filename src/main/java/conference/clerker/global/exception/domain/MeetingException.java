package conference.clerker.global.exception.domain;

import conference.clerker.global.exception.CustomException;
import conference.clerker.global.exception.ErrorCode;

public class MeetingException extends CustomException {
    public MeetingException(ErrorCode errorCode) {
        super(errorCode);
    }
}
