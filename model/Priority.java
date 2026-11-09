package model;

/** Task priority levels: High, Medium, Low. */
public enum Priority {
    HIGH(1), MEDIUM(2), LOW(3);

    private final int rank;

    Priority(int rank) {
        this.rank = rank;
    }

    /** Smaller rank = more important (used for sorting). */
    public int getRank() {
        return rank;
    }

    public static Priority fromString(String s) {
        if (s != null) {
            for (Priority p : values()) {
                if (p.name().equalsIgnoreCase(s.trim())) {
                    return p;
                }
            }
        }
        return MEDIUM;
    }

    @Override
    public String toString() {
        String n = name();
        return n.charAt(0) + n.substring(1).toLowerCase();
    }
}
