package org.gmagnotta.smarthome.model;

import org.jboss.logging.Logger;

public class Room {

    private static final Logger LOGGER = Logger.getLogger(Room.class);

    private final String name;

    private Double temperature;
    private Double humidity;
    private Double comformTemperature;
    private Double ecoTemperature;

    private Home home;

    public Room(String name, Home home) {
        this.name = name;
        this.home = home;
    }

    public String getName() {
        return name;
    }

    public Home getHome() {
        return home;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;

        for (HouseObserver observer : home.getObservers()) {
            observer.temperatureChanged(this, temperature);
        }
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;

        for (HouseObserver observer : home.getObservers()) {
            observer.humidityChanged(this, humidity);
        }
    }

    public Double getComfortTemp() {
        return comformTemperature;
    }

    public void setComfortTemp(Double comfortTemp) {
        this.comformTemperature = comfortTemp;
    }

    public Double getEcoTemp() {
        return ecoTemperature;
    }

    public void setEcoTemp(Double ecoTemp) {
        this.ecoTemperature = ecoTemp;
    }

    public Double getThresholdTemp() {
        if (home.getClimateMode().equals(ClimateMode.COMFORT)) {
            return comformTemperature;
        } else {
            return ecoTemperature;
        }
    }

    public String toString() {
        return name + " - current temperature: " + temperature + "; " +
         " current humidity: " + humidity + "; comfort temp: " + comformTemperature + ";" +
         " eco temp: " + ecoTemperature;
    }

}
