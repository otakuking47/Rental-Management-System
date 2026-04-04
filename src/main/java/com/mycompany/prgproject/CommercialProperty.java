/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prgproject;

/**
 *
 * @author amute
 */
public class CommercialProperty extends property {

    private String businessType;
    private int parkingSpaces;
    private boolean loadingDock;
    private int offices;

    public CommercialProperty(int ID, String floorSize, String fullAddress,
                              String location, double marketValue, double rentalCost,
                              boolean availability, String businessType,
                              int parkingSpaces, boolean loadingDock, int offices) {

        super(ID, "Commercial", floorSize, fullAddress, location, marketValue, rentalCost, availability);

        this.businessType = businessType;
        this.parkingSpaces = parkingSpaces;
        this.loadingDock = loadingDock;
        this.offices = offices;
    }

    @Override
    public String getDetails() {
        return "Commercial Property\n" +
               "ID: " + getID() +
               "\nAddress: " + getFullAddress() +
               "\nLocation: " + getLocation() +
               "\nFloor Size: " + getFloorSize() +
               "\nMarket Value: $" + getMarketValue() +
               "\nRental Cost: $" + getRentalCost() +
               "\nAvailable: " + isAvailability() +
               "\nBusiness Type: " + businessType +
               "\nParking Spaces: " + parkingSpaces +
               "\nLoading Dock: " + loadingDock +
               "\nOffices: " + offices;
    }
}
