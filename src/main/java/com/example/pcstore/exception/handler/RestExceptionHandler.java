package com.example.pcstore.exception.handler;

import com.example.pcstore.constant.Constant;
import com.example.pcstore.exception.AuthenticationException;
import com.example.pcstore.exception.BusinessException;
import com.example.pcstore.exception.model.ExceptionModel;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.utils.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestControllerAdvice()
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler extends Throwable implements Serializable {

    private static final Logger LOGGER = LoggingFactory.getLogger(RestExceptionHandler.class);

    private static final long serialVersionUID = 1L;

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @Order()
    public ResponseEntity<ExceptionModel> handleAllException(Throwable ex) {
        ExceptionModel exceptionDTO = ExceptionModel.builder().message(Constant.EXCEPTION_MESSAGE_DEFAULT).description(ex.getLocalizedMessage()).build();
        LOGGER.error("[EXCEPTION] Message: {}. Description: {}", exceptionDTO.getMessage(), exceptionDTO.getDescription());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @Order(value = Ordered.HIGHEST_PRECEDENCE)
    public ResponseEntity<ExceptionModel> handleBusinessException(BusinessException ex) {
        String message = Objects.isNull(ex) || StringUtils.isNullOrEmpty(ex.getMessage()) ? Constant.EXCEPTION_MESSAGE_DEFAULT : ex.getMessage();
        String code = Objects.isNull(ex) || StringUtils.isNullOrEmpty(ex.getCode()) ? null : ex.getCode();
        Object details = Objects.isNull(ex) ? null : ex.getData();
        ExceptionModel exception = ExceptionModel.builder().message(message).code(code).detail(details).build();
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @Order(value = Ordered.HIGHEST_PRECEDENCE)
    public ResponseEntity<ExceptionModel> handleAuthenticationException(AuthenticationException ex) {
        String message = Objects.isNull(ex) || StringUtils.isNullOrEmpty(ex.getMessage()) ? "Thông tin truy cập không hợp lệ, vui lòng đăng nhập lại." : ex.getMessage();
        HttpStatus httpStatus = Objects.isNull(ex) || Objects.isNull(ex.getStatusCode()) ? HttpStatus.UNAUTHORIZED : HttpStatus.valueOf(ex.getStatusCode());
        ExceptionModel exception = ExceptionModel.builder().message(message).build();
        return new ResponseEntity<>(exception, httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @Order(value = Ordered.HIGHEST_PRECEDENCE)
    public ResponseEntity<ExceptionModel> handleValidationException(MethodArgumentNotValidException ex) {
        List<ExceptionModel> errors = getErrors(ex);
        LOGGER.error("[EXCEPTION][VALIDATION] Validation Error: {}", errors);
        ExceptionModel exception = ExceptionModel.builder().message("Thông tin đầu vào không hợp lệ.").detail(errors).build();
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    private List<ExceptionModel> getErrors(MethodArgumentNotValidException e) {
        List<ExceptionModel> validationErrorModels = new ArrayList<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            validationErrorModels.add(buildExceptionModelValidation(fieldError));
        }
        return validationErrorModels;
    }

    private static ExceptionModel buildExceptionModelValidation(FieldError fieldError) {
        ExceptionModel validationErrorModel = new ExceptionModel();
        validationErrorModel.setCode(fieldError.getCode());
        validationErrorModel.setDescription(fieldError.getObjectName() + "/" + fieldError.getField());
        validationErrorModel.setMessage(fieldError.getDefaultMessage());
        return validationErrorModel;
    }
}

