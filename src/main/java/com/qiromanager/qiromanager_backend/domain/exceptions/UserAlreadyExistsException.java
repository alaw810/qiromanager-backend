package com.qiromanager.qiromanager_backend.domain.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
  public UserAlreadyExistsException(String field) {
    super("A user with this " + field + " already exists");
  }
}
