package com.rok.monitor.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "game_snapshots")
public class GameSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    private LocalDateTime capturedAt = LocalDateTime.now();

    @Column(length = 40)
    private String screenType = "UNCLASSIFIED";

    @Column(length = 1200)
    private String imageFilename;

    @Column(columnDefinition = "TEXT")
    private String ocrText;

    @Column(nullable = false)
    private boolean ocrAvailable;

    protected GameSnapshot() {}

    public GameSnapshot(Account account, String imageFilename, String ocrText, boolean ocrAvailable) {
        this.account = account;
        this.imageFilename = imageFilename;
        this.ocrText = ocrText;
        this.ocrAvailable = ocrAvailable;
    }

    public Long getId() { return id; }
    public Account getAccount() { return account; }
    public LocalDateTime getCapturedAt() { return capturedAt; }
    public String getScreenType() { return screenType; }
    public String getImageFilename() { return imageFilename; }
    public String getOcrText() { return ocrText; }
    public boolean isOcrAvailable() { return ocrAvailable; }
}
