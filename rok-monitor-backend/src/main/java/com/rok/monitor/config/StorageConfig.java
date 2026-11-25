package com.rok.monitor.config;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class StorageConfig {
    @Value("${app.screenshot-dir:../storage/screenshots}")
    private String screenshotDir;

    @PostConstruct
    public void createDirectories() throws Exception {
        Files.createDirectories(Path.of(screenshotDir));
        Files.createDirectories(Path.of("../storage"));
    }
}
