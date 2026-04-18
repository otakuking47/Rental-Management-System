/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prgproject;

/**
 *
 * @author amute
 */
public abstract class Property {

    // Attributes
    private int ID;
    private String type;
    private String floorSize;
    private String fullAddress;
    private String location;
    private double marketValue;
    private double rentalCost;
    private boolean availability;
    
    //constructor
    
    public Property() {
    }

    public Property(int ID, String type, String floorSize, String fullAddress, String location, double marketValue, double rentalCost, boolean availability) {
        this.ID = ID;
        this.type = type;
        this.floorSize = floorSize;
        this.fullAddress = fullAddress;
        this.location = location;
        this.marketValue = marketValue;
        this.rentalCost = rentalCost;
        this.availability = availability;
    }

    // method
    public abstract String getDetails();

    public void setRentalCost(double rentalCost) {
        this.rentalCost = rentalCost;
    }

    public void setMarketValue(double marketValue) {
        this.marketValue = marketValue;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFloorSize() {
        return floorSize;
    }

    public void setFloorSize(String floorSize) {
        this.floorSize = floorSize;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getMarketValue() {
        return marketValue;
    }

    public double getRentalCost() {
        return rentalCost;
    }

    public boolean isAvailability() {
        return availability;
    }
    
}
