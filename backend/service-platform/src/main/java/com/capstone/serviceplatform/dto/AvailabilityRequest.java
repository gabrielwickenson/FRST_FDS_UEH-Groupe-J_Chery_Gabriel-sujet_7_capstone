package com.capstone.serviceplatform.dto;

public class AvailabilityRequest {
    private boolean disponible;

    public AvailabilityRequest() {}

    public AvailabilityRequest(boolean disponible) {
        this.disponible = disponible;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}