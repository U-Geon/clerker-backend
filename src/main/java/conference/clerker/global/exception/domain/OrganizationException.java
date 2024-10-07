package conference.clerker.global.exception.domain;

import conference.clerker.global.exception.CustomException;
import conference.clerker.global.exception.ErrorCode;

public class OrganizationException extends CustomException {
    public OrganizationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
