package com.hrms.common.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String entity, Long id) {
        super(404, entity + " không tồn tại với id: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(404, message);
    }
}
