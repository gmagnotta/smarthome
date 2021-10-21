package org.gmagnotta.smarthome.model;

public interface HomeObserver {

    public void temperatureChanged(Room room, Double temperature);

    public void comfortTemperatureChanged(Room room, Double temperature);

    public void ecoTemperatureChanged(Room room, Double temperature);

    public void humidityChanged(Room room, Double humidity);

    public void climateControlChanged(Home house, boolean climateControl);

    public void climateModeChanged(Home house, ClimateMode climateMode);

    public void awayModeChanged(Home house, boolean awayMode);

    public void boilerChanged(Home house, boolean boilerState);

}
