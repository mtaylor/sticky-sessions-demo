package com.example.backend.controller;

import com.example.backend.service.InstanceIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class InstanceController {

    private static final Logger log = LoggerFactory.getLogger(InstanceController.class);

    private final InstanceIdService instanceIdService;

    public InstanceController(InstanceIdService instanceIdService) {
        this.instanceIdService = instanceIdService;
    }

    @GetMapping("/instance-id")
    public ResponseEntity<Map<String, String>> getInstanceId() {
        String instanceId = instanceIdService.getInstanceId();
        log.info("Request serviced by instance: {}", instanceId);
        return ResponseEntity.ok(Map.of("instanceId", instanceId));
    }
}
