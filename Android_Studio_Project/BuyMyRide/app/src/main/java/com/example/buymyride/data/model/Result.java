package com.example.buymyride.data.model;

public class Result<T> {
    private final T data;
    private final Exception exception;

    private Result(T data, Exception exception) {
        this.data = data;
        this.exception = exception;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data, null);
    }

    public static <T> Result<T> error(Exception exception) {
        return new Result<>(null, exception);
    }

    public T getData() {
        return data;
    }

    public Exception getException() {
        return exception;
    }

    public boolean isSuccessful() {
        return exception == null;
    }
}
