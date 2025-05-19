package ua.maks.prog.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum MonthView {
    JANUARY("Січень"),
    FEBRUARY("Лютий"),
    MARCH("Березень"),
    APRIL("Квітень"),
    MAY("Травень"),
    JUNE("Червень"),
    JULY("Липень"),
    AUGUST("Серпень"),
    SEPTEMBER("Вересень"),
    OCTOBER("Жовтень"),
    NOVEMBER("Листопад"),
    DECEMBER("Грудень");

    private static final Map<String, MonthView> NAME_MAP = Stream.of(values())
            .collect(Collectors.toMap(Enum::name, m -> m));

    private final String monthName;

    MonthView(String monthName) {
        this.monthName = monthName;
    }

    public String getMonthName() {
        return monthName;
    }

    public static String getName(String value) {
        if (value == null) return null;
        MonthView month = NAME_MAP.get(value.toUpperCase());
        return (month != null) ? formatName(month.name()) : null;
    }

    private static String formatName(String str) {
        return str.charAt(0) + str.substring(1).toLowerCase();
    }
}
