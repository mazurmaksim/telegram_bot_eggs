package ua.maks.prog.views;

public enum MonthView {
    JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE,
    JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER;

    public static String getName(String value) {
        try {
            return capitalize(MonthView.valueOf(value.toUpperCase()).name());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}

