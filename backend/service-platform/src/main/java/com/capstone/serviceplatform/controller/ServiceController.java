package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.entity.Service;
import com.capstone.serviceplatform.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }
}