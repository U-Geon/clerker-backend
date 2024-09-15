package conference.clerker.global.exception.domain;

import conference.clerker.global.exception.CustomException;
import conference.clerker.global.exception.ErrorCode;

public class ScheduleException extends CustomException {
    public ScheduleException(ErrorCode errorCode) {
        super(errorCode);
    }
}
