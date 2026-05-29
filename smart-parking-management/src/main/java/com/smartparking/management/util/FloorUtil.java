package com.smartparking.management.util;

public class FloorUtil {

    public static String getFloorName(Integer floorNumber) {

        return switch (floorNumber) {

            case -5 -> "Basement 5";
            case -4 -> "Basement 4";
            case -3 -> "Basement 3";
            case -2 -> "Basement 2";
            case -1 -> "Basement 1";

            case 0 -> "Ground Floor";

            case 1 -> "First Floor";
            case 2 -> "Second Floor";
            case 3 -> "Third Floor";
            case 4 -> "Fourth Floor";
            case 5 -> "Fifth Floor";

            default -> floorNumber + " Floor";
        };
    }
}
