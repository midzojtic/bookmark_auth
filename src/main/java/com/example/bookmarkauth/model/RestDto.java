package com.example.bookmarkauth.model;

import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:4200")
public class RestDto<T> {

    public static <T> RestDto<T> success(T data, String message) {
        RestDto<T> model = new RestDto<>();
        model.setSuccess(true);
        model.setMessage(message);
        model.setData(data);
        return  model;
    }

    public static <T> RestDto<T> fail (String message) {
        RestDto<T> model = new RestDto<>();
        model.setSuccess(false);
        model.setMessage(message);
        return  model;
    }

    public static <T> RestDto<T> success(String message) {
        RestDto<T> model = new RestDto<>();
        model.setSuccess(true);
        model.setMessage(message);
        model.setData(null);
        return  model;
    }

    private T data;
    private boolean success;
    private String message;

    public T getData() {
        return data;
    }

    private void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
