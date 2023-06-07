package webProject.togetherPartyTonight.domain.billing.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import webProject.togetherPartyTonight.global.error.ErrorCode;

@Getter
public class BillingException extends RuntimeException{
    private ErrorCode errorCode;

    public BillingException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode= errorCode;
    }
}
