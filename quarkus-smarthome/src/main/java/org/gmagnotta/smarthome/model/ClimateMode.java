package org.gmagnotta.smarthome.model;

public enum ClimateMode {

    COMFORT, ECO;

    public static ClimateMode from(String string) throws IllegalArgumentException {

        if ("COMFORT".equals(string)) {
            return COMFORT;
        } else if ("ECO".equals(string)) {
            return ECO;
        }

        throw new IllegalArgumentException(string + " is not a valid ClimateMode");
    }

}