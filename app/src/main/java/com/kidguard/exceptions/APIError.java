package com.kidguard.exceptions;

/**
 * Created by kos on 2017/3/2.
 */

public class APIError {
    private int statusCode;
    private String message;

    public APIError() {
    }

    public int status() {
        return statusCode;
    }

    public String message() {
        return message;
    }
}
