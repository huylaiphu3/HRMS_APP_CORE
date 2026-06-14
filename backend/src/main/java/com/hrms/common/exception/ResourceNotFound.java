package com.hrms.common.exception;

import javax.xml.stream.Location;

public class ResourceNotFound extends RuntimeException{
    private final String location;
    public ResourceNotFound(String message, String location) {
        super(message);
        this.location = location;
    }
    public String getLocation() {
        return location;
    }
}
