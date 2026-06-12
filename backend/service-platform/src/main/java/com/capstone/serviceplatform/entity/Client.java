package com.capstone.serviceplatform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "client")
@PrimaryKeyJoinColumn(name = "id")
public class Client extends User {
    private String adresseParDefaut;

    //Getters and Setters
    public String getAdresseParDefaut() {
        return adresseParDefaut;
    }
    public void setAdresseParDefaut(String adresseParDefaut) {
        this.adresseParDefaut = adresseParDefaut;
    }
}