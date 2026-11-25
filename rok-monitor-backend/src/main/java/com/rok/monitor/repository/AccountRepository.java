package com.rok.monitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rok.monitor.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByNameIgnoreCase(String name);
}
