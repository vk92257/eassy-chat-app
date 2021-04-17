package com.eassychat.response;

import com.google.gson.annotations.SerializedName;

public class AllUsers {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @SerializedName("_id")
    private String id ;
    @SerializedName("name")
    private String name ;
    @SerializedName("email")
    private String gmail ;
    @SerializedName("profilePic")
    private String profilePic ;
    @SerializedName("__v")
    private String version;
}
