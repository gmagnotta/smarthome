package org.gmagnotta.smarthome.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.inject.Singleton;

@Singleton
public class Home {
    
    private String name;
    private Map<String, Room> rooms;
    private boolean climateControl;
    private boolean awayMode;
    private ClimateMode climateMode; 
    private List<HomeObserver> observers;
    private boolean boilerWorking;

    public Home() {
        this.awayMode = false;
        this.climateControl = false;
        this.climateMode = ClimateMode.ECO;
        this.boilerWorking = false;
        this.rooms = new HashMap<String, Room>();
        this.observers = new ArrayList<HomeObserver>();
    }



    public void addRoom(Room room) {
        rooms.put(room.getName(), room);
    }

    public Collection<Room> getRooms() {
        return rooms.values();
    }

    public boolean hasRoom(String name) {
        return rooms.get(name) != null ? true : false;
    }

    public Room getRoom(String name) {
        return rooms.get(name);
    }

    public String toString() {

        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append(name + ":\n");
        stringBuffer.append("Climate control: " + climateControl + "\n");
        stringBuffer.append("Climate mode: " + climateMode + "\n");
        for (Map.Entry<String, Room> entry : rooms.entrySet()) {
            stringBuffer.append(entry.toString() + "\n");
        }

        return stringBuffer.toString();
    }

    public boolean getClimateControl() {
        return climateControl;
    }

    public void setClimateControl(boolean climateControl) {
        this.climateControl = climateControl;

        for (HomeObserver observer : observers) {
            observer.climateControlChanged(this, climateControl);
        }
    }

    public boolean isAwayMode() {
        return awayMode;
    }

    public void setAwayMode(boolean awayMode) {
        this.awayMode = awayMode;

        for (HomeObserver observer : observers) {
            observer.awayModeChanged(this, awayMode);
        }
    }

    public void addObserver(HomeObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(HomeObserver observer) {
        observers.remove(observer);
    }

    List<HomeObserver> getObservers() {
        return observers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClimateMode getClimateMode() {
        return climateMode;
    }

    public void setClimateMode(ClimateMode climateMode) {
        this.climateMode = climateMode;

        for (HomeObserver observer : observers) {
            observer.climateModeChanged(this, climateMode);
        }
    }

    public boolean isBoilerWorking() {
        return boilerWorking;
    }

    public void setBoilerWorking(boolean boilerWorking) {
        this.boilerWorking = boilerWorking;
    }

}
