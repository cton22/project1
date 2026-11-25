package com.rok.monitor.dto;

import java.time.LocalDateTime;
import com.rok.monitor.entity.Account;

public record AccountResponse(Long id, String name, LocalDateTime createdAt) {
    public static AccountResponse from(Account a) {
        return new AccountResponse(a.getId(), a.getName(), a.getCreatedAt());
    }
}
