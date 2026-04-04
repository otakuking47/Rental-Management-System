/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prgproject;

/**
 *
 * @author amute
 */
public class Main {
    public static void main(String[] args) {

        // Create a ResidentialProperty object
        ResidentialProperty residential = new ResidentialProperty(
            101,
            "150 sqm",
            "12 Oak Street, Windhoek",
            "Windhoek",
            1500000.00,
            8500.00,
            true,
            3,
            2,
            true,
            false
        );

        // Create a CommercialProperty object
        CommercialProperty commercial = new CommercialProperty(
            202,
            "500 sqm",
            "45 Independence Ave, Windhoek",
            "Windhoek CBD",
            5000000.00,
            25000.00,
            true,
            "Retail",
            20,
            true,
            10
        );

        // Print details
        System.out.println(residential.getDetails());
        System.out.println();
        System.out.println(commercial.getDetails());

        // Updates
        System.out.println("\n--- Updating residential property ---");
        residential.setRentalCost(9000.00);
        residential.setAvailability(false);
        System.out.println("New Rental Cost: $" + residential.getRentalCost());
        System.out.println("Available: " + residential.isAvailability());

        System.out.println("\n--- Updating commercial property ---");
        commercial.setMarketValue(5500000.00);
        System.out.println("New Market Value: $" + commercial.getMarketValue());
    }
    
}
