package org.gmagnotta.smarthome.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Home {
    
    private String name;
    private Map<String, Room> rooms;
    private boolean climateControl;
    private ClimateMode climateMode; 
    private List<HouseObserver> observers;

    public Home(String name) {
        this.name = name;
        this.climateControl = false;
        this.climateMode = ClimateMode.ECO;
        rooms = new HashMap<String, Room>();
        observers = new ArrayList<HouseObserver>();
    }

    public void addRoom(Room room) {
        rooms.put(room.getName(), room);
    }

    public Collection<Room> getRooms() {
        return rooms.values();
    }

    public Room getRoom(String name) {
        return rooms.get(name);
    }

    public String toString() {

        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append(name + ":\n");
        for (Map.Entry<String, Room> entry : rooms.entrySet()) {
            stringBuffer.append(entry.toString() + "\n");
        }

        return stringBuffer.toString();
    }

    public boolean getClimateConfrol() {
        return climateControl;
    }

    public void setClimateControl(boolean climateControl) {
        this.climateControl = climateControl;

        for (HouseObserver observer : observers) {
            observer.climateControlChanged(this, climateControl);
        }
    }

    public void addObserver(HouseObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(HouseObserver observer) {
        observers.remove(observer);
    }

    List<HouseObserver> getObservers() {
        return observers;
    }

    public String getName() {
        return name;
    }

    public ClimateMode getClimateMode() {
        return climateMode;
    }

    public void setClimateMode(ClimateMode climateMode) {
        this.climateMode = climateMode;
    }

}
