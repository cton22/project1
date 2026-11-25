package com.rok.monitor.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rok.monitor.dto.*;
import com.rok.monitor.entity.Account;
import com.rok.monitor.repository.AccountRepository;
import com.rok.monitor.repository.GameSnapshotRepository;
import com.rok.monitor.service.MonitorService;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final AccountRepository accountRepository;
    private final GameSnapshotRepository snapshotRepository;
    private final MonitorService monitorService;

    @Value("${app.screenshot-dir:../storage/screenshots}")
    private String screenshotDir;

    public ApiController(AccountRepository accountRepository,
                         GameSnapshotRepository snapshotRepository,
                         MonitorService monitorService) {
        this.accountRepository = accountRepository;
        this.snapshotRepository = snapshotRepository;
        this.monitorService = monitorService;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("status", "UP", "application", "RoK Monitor", "mode", "read-only");
    }

    @GetMapping("/accounts")
    public List<AccountResponse> accounts() {
        return accountRepository.findAll().stream().map(AccountResponse::from).toList();
    }

    @PostMapping("/accounts")
    public ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        if (accountRepository.existsByNameIgnoreCase(request.name().trim())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Account label already exists"));
        }
        Account saved = accountRepository.save(new Account(request.name().trim()));
        return ResponseEntity.ok(AccountResponse.from(saved));
    }

    @GetMapping("/snapshots")
    public List<SnapshotResponse> snapshots(@RequestParam(required = false) Long accountId) {
        var rows = accountId == null
                ? snapshotRepository.findTop100ByOrderByCapturedAtDesc()
                : snapshotRepository.findTop100ByAccountIdOrderByCapturedAtDesc(accountId);
        return rows.stream().map(SnapshotResponse::from).toList();
    }

    @PostMapping("/monitor/capture")
    public SnapshotResponse capture(@Valid @RequestBody CaptureRequest request) throws Exception {
        return SnapshotResponse.from(monitorService.capture(request));
    }

    @GetMapping(value = "/screenshots/{filename}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> screenshot(@PathVariable String filename) throws Exception {
        if (filename.contains("/") || filename.contains("\\") || filename.contains("..")) {
            return ResponseEntity.badRequest().build();
        }
        Path path = Path.of(screenshotDir).resolve(filename).normalize();
        if (!Files.exists(path) || !path.startsWith(Path.of(screenshotDir).toAbsolutePath().normalize())) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
    }
}
