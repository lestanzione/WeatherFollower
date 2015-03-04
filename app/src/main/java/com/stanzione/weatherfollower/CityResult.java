package com.stanzione.weatherfollower;

/**
 * Created by Leandro Stanzione on 01/03/2015.
 */
public class CityResult {

    public CityResult(String cityName, String currentTemperature){
        this.setCityName(cityName);
        this.setCurrentTemperature(currentTemperature);
    }

    private String cityName;
    private String currentTemperature;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(String currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

}
