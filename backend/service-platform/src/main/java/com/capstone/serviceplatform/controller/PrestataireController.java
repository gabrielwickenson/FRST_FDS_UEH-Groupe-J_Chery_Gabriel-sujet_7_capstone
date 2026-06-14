package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.entity.Prestataire;
import com.capstone.serviceplatform.repository.PrestataireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestataires")
public class PrestataireController {

    @Autowired
    private PrestataireRepository prestataireRepository;

    @GetMapping("/recherche")
    public ResponseEntity<List<Prestataire>> rechercherPrestataires(
            @RequestParam(required = false) String service,
            @RequestParam(required = false) Double noteMin,
            @RequestParam(required = false) String zone) {

        List<Prestataire> resultats = prestataireRepository.rechercherParFiltres(service, noteMin, zone);
        // Masquer le mot de passe pour la réponse
        resultats.forEach(p -> p.setMotDePasse(null));
        return ResponseEntity.ok(resultats);
    }
}