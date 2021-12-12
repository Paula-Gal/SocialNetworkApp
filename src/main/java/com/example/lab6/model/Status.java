package com.example.lab6.model;

public enum Status {
    /**
     * Enum values
     */

    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private String status;

    /**
     * The constructor of the class
     *
     * @param status the request of the status as a string
     */
    Status(String status) {
        this.status = status;
    }

    /**
     * The method returns the status as enum by a given status as a string
     * @param status string status
     * @return Status if the given string is a value of enum or null, otherwise
     */
    public static Status getStatus(String status) {
        for (Status status1 : Status.values())
            if (status1.status.equals(status))
                return status1;
        return null;
    }

    /**
     * Get method for status
     * @return status as a string
     */
    public String getStatus() {
        return status;
    }
}
