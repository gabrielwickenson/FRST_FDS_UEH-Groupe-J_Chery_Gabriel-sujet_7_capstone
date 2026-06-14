package com.capstone.serviceplatform.dto;

public class AvisRequest {
    private Integer note; // 1 à 5
    private String commentaire;

    // getters/setters
    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        this.note = note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}