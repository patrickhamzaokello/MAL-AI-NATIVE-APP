package com.pkasemer.malai.Models;


public class UserModel {

    private String id, username,full_name, email, phone,password,signUpDate,profilePic,status,mwRole;

    public UserModel(String id, String full_name, String username, String email, String phone, String password, String signUpDate, String profilePic, String status, String mwRole) {
        this.id = id;
        this.full_name = full_name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.signUpDate = signUpDate;
        this.profilePic = profilePic;
        this.status = status;
        this.mwRole = mwRole;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSignUpDate() {
        return signUpDate;
    }

    public void setSignUpDate(String signUpDate) {
        this.signUpDate = signUpDate;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMwRole() {
        return mwRole;
    }

    public void setMwRole(String mwRole) {
        this.mwRole = mwRole;
    }
}