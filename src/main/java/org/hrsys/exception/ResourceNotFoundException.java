package org.hrsys.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " not found with id: " + id);
    }

    public ResourceNotFoundException(String resourceName, String name) {
        super(resourceName + " not found with name: " + name);
    }

    public ResourceNotFoundException(String resourceName) {
        super(resourceName + " not found");
    }
}