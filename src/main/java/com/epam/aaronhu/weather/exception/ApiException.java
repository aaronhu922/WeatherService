package com.epam.aaronhu.weather.exception;

import lombok.Getter;

@Getter
public class ApiException extends Exception{
    private int code;
    public ApiException (int code, String msg) {
        super(msg);
        this.code = code;
    }
}
