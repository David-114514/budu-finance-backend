package com.budu.finance.dto;

public record LoginRequest(
        String username,
        String password
) {}