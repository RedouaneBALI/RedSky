package com.github.redouanebali;

import lombok.Data;

@Data
public class Result<T> {

  private final T      value;
  private final String error;

  private Result(T value, String error) {
    this.value = value;
    this.error = error;
  }

  public static <T> Result<T> success(T value) {
    return new Result<>(value, null);
  }

  public static <T> Result<T> failure(String error) {
    return new Result<>(null, error);
  }

  public boolean isSuccess() {
    return error == null;
  }

  public boolean isFailure() {
    return error != null;
  }

}
