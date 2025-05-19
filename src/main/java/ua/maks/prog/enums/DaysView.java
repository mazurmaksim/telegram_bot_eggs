package ua.maks.prog.enums;

public enum DaysView {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, TODAY;

    public static String getName(String value) {
        try {
            return capitalize(DaysView.valueOf(value.toUpperCase()).name());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
