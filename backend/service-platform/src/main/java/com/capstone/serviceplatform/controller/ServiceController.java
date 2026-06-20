package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.entity.Service;
import com.capstone.serviceplatform.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/services")
@Tag(name = "Services", description = "Catalogue des services (public)")
public class ServiceController {
    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping
    @Operation(summary = "Lister tous les services disponibles (public)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des services",
                    content = @Content(schema = @Schema(implementation = Service.class)))
    })
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }
}