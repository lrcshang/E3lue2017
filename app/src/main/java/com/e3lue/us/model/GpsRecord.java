package com.e3lue.us.model;

/**
 * Created by Leo on 2016/10/26.
 */

public class GpsRecord {
    private String Longitude;
    private String Latitude;
    private String Address;
    private String Province;
    private String City;

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
    }


}
