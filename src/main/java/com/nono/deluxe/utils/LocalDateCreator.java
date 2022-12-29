package com.nono.deluxe.utils;

import java.time.LocalDate;

public class LocalDateCreator {

    public static LocalDate getDateOfFirstDay(int year, int month) {
        if (year == 0) {
            year = LocalDate.now().getYear();
        }

        if (month == 0) {
            month = 1;
        }

        return LocalDate.of(year, month, 1);
    }

    public static LocalDate getDateOfLastDay(int year, int month) {
        if (year == 0) {
            year = LocalDate.now().getYear();
        }

        if (month == 0) {
            month = 12;
        }

        return LocalDate.of(year, month, LocalDate.of(year, month, 1).lengthOfMonth());
    }
}
