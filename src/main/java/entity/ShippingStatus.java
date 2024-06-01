package entity;

public enum ShippingStatus {
    COMPLETED("Completed"),
    IN_PROGRESS("In Progress"),
    RETURNS("Returns"),
    OVERDUE("Overdue");

    private final String label;

    ShippingStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public static ShippingStatus fromString(String text) {
        for (ShippingStatus b : ShippingStatus.values()) {
            if (b.label.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + text);
    }
}


