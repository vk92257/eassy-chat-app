package com.eassychat.response;

import com.google.gson.annotations.SerializedName;

public class SignUpResponse  {
    @SerializedName("error")
    private boolean error;
    @SerializedName("details")
    private Details details;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private String status;



}

