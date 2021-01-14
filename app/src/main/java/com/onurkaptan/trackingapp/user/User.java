package com.onurkaptan.trackingapp.user;

import java.util.HashMap;

public class User {
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private String userType;
    private String isSupervised;
    private String supervisorNumber;
    private HashMap<String,Object> hashMap;

    public User(String fullName, String email, String password, String phoneNumber, String userType) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
        if ( userType.matches("Supervisor")){
            isSupervised = "-1";
            supervisorNumber = "-1";
        }
        else {
            isSupervised = "0";
            supervisorNumber = "0";
        }

        hashMap = new HashMap<>();
        hashMap.put("fullname", fullName);
        hashMap.put("email", email);
        hashMap.put("password", password);
        hashMap.put("phonenumber", phoneNumber);
        hashMap.put("usertype", userType);
        hashMap.put("issupervised", isSupervised);
        hashMap.put("supervisornumber", supervisorNumber);
    }

    public String getIsSupervised() {
        return isSupervised;
    }

    public void setIsSupervised(String isSupervised) {
        this.isSupervised = isSupervised;
    }

    public String getSupervisorNumber() {
        return supervisorNumber;
    }

    public void setSupervisorNumber(String supervisorNumber) {
        this.supervisorNumber = supervisorNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public HashMap<String, Object> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<String, Object> hashMap) {
        this.hashMap = hashMap;
    }
}
