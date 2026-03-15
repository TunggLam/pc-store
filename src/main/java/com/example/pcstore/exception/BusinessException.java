package com.example.pcstore.exception;

import java.io.Serializable;

@SuppressWarnings("all")
public class BusinessException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient Object data;

    private String message;

    private String code;

    public BusinessException(String message, String code, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public BusinessException(String message, Object data){
        super(message);
        this.data = data;
    }

    public BusinessException(String message, String code){
        super(message);
        this.code = code;
    }

    public BusinessException(String message){
        super(message);
    }

    public Object getData(){
        return data;
    }

    public String getCode(){
        return code;
    }
}