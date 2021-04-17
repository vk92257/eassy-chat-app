package com.eassychat.response;

import com.google.gson.annotations.SerializedName;

public  class  Details{
  @SerializedName("name")
  private String name;
  @SerializedName("email")
  private String email;

  public String getName() {
      return name;
  }

  public void setName(String name) {
      this.name = name;
  }

  public String getEmail() {
      return email;
  }

  public void setEmail(String email) {
      this.email = email;
  }

  public String getToken() {
      return token;
  }

  public void setToken(String token) {
      this.token = token;
  }

  public String getProfilePic() {
      return profilePic;
  }

  public void setProfilePic(String profilePic) {
      this.profilePic = profilePic;
  }

  public String getId() {
      return id;
  }

  public void setId(String id) {
      this.id = id;
  }

  @SerializedName("token")
  private String token;
  @SerializedName("profilePic")
  private String profilePic;
  @SerializedName("id")
  private String id;
}
