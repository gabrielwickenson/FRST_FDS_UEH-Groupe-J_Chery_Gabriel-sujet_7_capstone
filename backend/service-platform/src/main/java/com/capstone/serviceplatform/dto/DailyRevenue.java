package com.capstone.serviceplatform.dto;

import java.math.BigDecimal;

public class DailyRevenue {
    private String day;   // "Lun", "Mar", etc.
    private BigDecimal amount;

    public DailyRevenue(String day, BigDecimal amount) {
        this.day = day;
        this.amount = amount;
    }

    // Getters et setters
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}