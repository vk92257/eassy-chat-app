package com.eassychat.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetAllUserResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private ArrayList<AllUsers> allUsers;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<AllUsers> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(ArrayList<AllUsers> allUsers) {
        this.allUsers = allUsers;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @SerializedName("error")
    private String error;

}

