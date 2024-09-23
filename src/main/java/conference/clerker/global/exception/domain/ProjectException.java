package conference.clerker.global.exception.domain;

import conference.clerker.global.exception.CustomException;
import conference.clerker.global.exception.ErrorCode;

public class ProjectException extends CustomException {
    public ProjectException(ErrorCode errorCode) {
        super(errorCode);
    }
}
