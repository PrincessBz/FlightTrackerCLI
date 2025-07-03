package com.flighttracker.cli.domain;

import java.util.Objects;

public class Aircraft {
    private Long id;
    private String type;
    private String airlineName;
    private int numberOfPassengers;

    public Aircraft() {}

    public Aircraft(Long id, String type, String airlineName, int numberOfPassengers) {
        this.id = id;
        this.type = type;
        this.airlineName = airlineName;
        this.numberOfPassengers = numberOfPassengers;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAirlineName() { return airlineName; }
    public void setAirlineName(String airlineName) { this.airlineName = airlineName; }
    public int getNumberOfPassengers() { return numberOfPassengers; }
    public void setNumberOfPassengers(int numberOfPassengers) { this.numberOfPassengers = numberOfPassengers; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aircraft aircraft = (Aircraft) o;
        return numberOfPassengers == aircraft.numberOfPassengers &&
                Objects.equals(id, aircraft.id) &&
                Objects.equals(type, aircraft.type) &&
                Objects.equals(airlineName, aircraft.airlineName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, airlineName, numberOfPassengers);
    }

    @Override
    public String toString() {
        return "Aircraft{id=" + id + ", type='" + type + "', airlineName='" + airlineName + "', numberOfPassengers=" + numberOfPassengers + '}';
    }
}
