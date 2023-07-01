package webProject.togetherPartyTonight.global.error;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import webProject.togetherPartyTonight.domain.club.exception.ClubException;
import webProject.togetherPartyTonight.domain.member.exception.MemberException;
import webProject.togetherPartyTonight.domain.review.exception.ReviewException;
import webProject.togetherPartyTonight.global.common.ErrorResponse;
import webProject.togetherPartyTonight.global.common.response.FailureResponse;
import webProject.togetherPartyTonight.global.common.service.ResponseService;

import java.time.format.DateTimeParseException;

/**
 * ExceptionHandler를 통한 예외 처리 클래스
 * 각 exception에 맞게 메서드 작성
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * @param e 예외 종류
     * @return 정형화된 ErrorResponse
     */
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final String FAIL = "false";
    private ResponseService responseService;

    @Autowired
    public GlobalExceptionHandler(ResponseService responseService) {
        this.responseService = responseService;
    }

    /**
     * 매개변수만 다르고 메서드 내용이 중복되는 부분을 어떻게 고칠지 생각해봐야할 것 같습니다
     */


    @ExceptionHandler(CommonException.class)
    public FailureResponse commonException (CommonException e) {
        e.printStackTrace();
        log.error("commonException exception : {}", e.getErrorInterface().getErrorMessage());
        return responseService.getFailureResponse(e.getErrorInterface().getStatusCode(), e.getErrorInterface().getErrorMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public FailureResponse fieldValueException (MethodArgumentNotValidException e) {
        e.printStackTrace();
        StringBuilder sb = new StringBuilder();
        for (FieldError fe : e.getFieldErrors()) {
            sb.append(fe.getDefaultMessage()).append("  ");
        }
        return responseService.getFailureResponse(400, sb.toString());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public FailureResponse httpMessageNotReadableException (HttpMessageNotReadableException e) {
        e.printStackTrace();
        String[] split = e.getMessage().split("\\[");
        String parameter = split[2].substring(1, split[2].length() - 3);
        return responseService.getFailureResponse(400, parameter+"의 형식이 잘못되었습니다.");
    }




}
