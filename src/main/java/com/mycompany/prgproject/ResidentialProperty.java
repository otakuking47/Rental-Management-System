/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prgproject;

/**
 *
 * @author amute
 */
public class ResidentialProperty extends property {

    private int bedrooms;
    private int bathrooms;
    private boolean hasGarden;
    private boolean hasGarage;

    public ResidentialProperty(int ID, String floorSize, String fullAddress,
                               String location, double marketValue, double rentalCost,
                               boolean availability, int bedrooms, int bathrooms,
                               boolean hasGarden, boolean hasGarage) {

        super(ID, "Residential", floorSize, fullAddress, location, marketValue, rentalCost, availability);

        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.hasGarden = hasGarden;
        this.hasGarage = hasGarage;
    }

    @Override
    public String getDetails() {
        return "Residential Property\n" +
               "ID: " + getID() +
               "\nAddress: " + getFullAddress() +
               "\nLocation: " + getLocation() +
               "\nFloor Size: " + getFloorSize() +
               "\nMarket Value: $" + getMarketValue() +
               "\nRental Cost: $" + getRentalCost() +
               "\nAvailable: " + isAvailability() +
               "\nBedrooms: " + bedrooms +
               "\nBathrooms: " + bathrooms +
               "\nGarden: " + hasGarden +
               "\nGarage: " + hasGarage;
    }
}
