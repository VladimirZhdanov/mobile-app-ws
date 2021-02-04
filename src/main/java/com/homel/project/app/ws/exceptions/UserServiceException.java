package com.homel.project.app.ws.exceptions;

public class UserServiceException extends RuntimeException{


    private static final long serialVersionUID = 4720765806269281075L;

    public UserServiceException(String message) {
        super(message);
    }
}
