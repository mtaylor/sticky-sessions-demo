package com.example.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InstanceIdService {

    private final String instanceId;

    public InstanceIdService(@Value("${HOSTNAME:}") String hostname) {
        this.instanceId = (hostname != null && !hostname.isBlank())
                ? hostname
                : "local-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public String getInstanceId() {
        return instanceId;
    }
}
