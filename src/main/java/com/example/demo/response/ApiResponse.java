package com.example.demo.response;
import lombok.*;

@Getter
@Setter
@ToString
public class ApiResponse {
    private String result;


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public ApiResponse() {
    }

    public ApiResponse(String result) {
        this.result = result;
    }

    // getter, setter 등을 추가해주세요.
}

