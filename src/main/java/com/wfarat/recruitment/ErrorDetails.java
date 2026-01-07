package com.wfarat.recruitment;

public class ErrorDetails {
    public int status;
    public String message;

    public ErrorDetails(String message, int status) {
        this.message = message;
        this.status = status;
    }
}
