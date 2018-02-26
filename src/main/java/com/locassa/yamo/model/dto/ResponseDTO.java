package com.locassa.yamo.model.dto;

import java.io.Serializable;

public class ResponseDTO implements Serializable {

    private int readyState;

    private String responseText;

    private int status;

    private String statusText;

    public ResponseDTO() {
    }

    public ResponseDTO(int readyState, String responseText, int status, String statusText) {
        this.readyState = readyState;
        this.responseText = responseText;
        this.status = status;
        this.statusText = statusText;
    }

    public static ResponseDTO statusOK() {
        return new ResponseDTO(4, "OK", 200, "OK");
    }

    public static ResponseDTO statusNoOK() {
        return new ResponseDTO(4, "NO_OK", 200, "OK");
    }

    public int getReadyState() {
        return readyState;
    }

    public void setReadyState(int readyState) {
        this.readyState = readyState;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

}
