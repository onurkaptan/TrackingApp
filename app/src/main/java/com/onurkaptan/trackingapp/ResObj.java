package com.onurkaptan.trackingapp;

import java.util.HashMap;

public class ResObj {
  private String latitude;
  private String longitude;
  private String radius;
  private String isDone;
  private String resID;
  private String supervisorNumber;
  private HashMap<String, Object> hashMap;

  public String getSupervisorNumber() {
        return supervisorNumber;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getResID() {
        return resID;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getRadius() {
        return radius;
    }

    public String getIsDone() {
        return isDone;
    }

    public HashMap<String, Object> getHashMap() {
        return hashMap;
    }

    public ResObj(String latitude, String longitude, String radius, String isDone, String resID, String supervisorNumber) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.isDone = isDone;
        this.resID = resID;
        this.supervisorNumber = supervisorNumber;
        hashMap = new HashMap<>();
        hashMap.put("latitude",latitude);
        hashMap.put("longitude",longitude);
        hashMap.put("radius",radius);
        hashMap.put("isDone",isDone);
        hashMap.put("resID",resID);
        hashMap.put("supervisornumber", supervisorNumber);
    }

}
