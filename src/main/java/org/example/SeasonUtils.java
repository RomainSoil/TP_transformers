package org.example;

import java.time.LocalDate;
import java.time.Month;

public class SeasonUtils {

    public static String getCurrentSeason() {
        Month month = LocalDate.now().getMonth();

        return switch (month) {
            case DECEMBER, JANUARY, FEBRUARY -> "Hiver";
            case MARCH, APRIL, MAY -> "Printemps";
            case JUNE, JULY, AUGUST -> "Été";
            default -> "Automne";
        };
    }
}
