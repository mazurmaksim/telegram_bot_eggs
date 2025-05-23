package ua.maks.prog.enums;

public enum OrderStatus {
    NEW("New"),
    COMPLETED("Completed");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

