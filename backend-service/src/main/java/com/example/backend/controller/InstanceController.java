package com.example.backend.controller;

import com.example.backend.service.InstanceIdService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class InstanceController {

    private final InstanceIdService instanceIdService;

    public InstanceController(InstanceIdService instanceIdService) {
        this.instanceIdService = instanceIdService;
    }

    @GetMapping("/instance-id")
    public ResponseEntity<Map<String, String>> getInstanceId() {
        return ResponseEntity.ok(Map.of("instanceId", instanceIdService.getInstanceId()));
    }
}
