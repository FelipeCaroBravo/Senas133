package com.mediaciondirecta.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PinService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String hash(String pin) {
        return passwordEncoder.encode(pin);
    }

    public boolean matches(String pin, String pinHash) {
        return passwordEncoder.matches(pin, pinHash);
    }
}
