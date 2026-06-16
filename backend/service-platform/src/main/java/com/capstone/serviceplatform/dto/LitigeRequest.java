package com.capstone.serviceplatform.dto;

import jakarta.validation.constraints.NotBlank;

public class LitigeRequest {
    @NotBlank(message = "Le motif du litige est obligatoire")
    private String motif;

    //Getters and Setters
    public String getMotif() {
        return motif;
    }
    public void setMotif(String motif) {
        this.motif = motif;
    }
}