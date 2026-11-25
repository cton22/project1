package com.rok.monitor.dto;

import java.time.LocalDateTime;
import com.rok.monitor.entity.GameSnapshot;

public record SnapshotResponse(
        Long id, Long accountId, String accountName, LocalDateTime capturedAt,
        String screenType, String imageUrl, String ocrText, boolean ocrAvailable
) {
    public static SnapshotResponse from(GameSnapshot s) {
        return new SnapshotResponse(
                s.getId(), s.getAccount().getId(), s.getAccount().getName(),
                s.getCapturedAt(), s.getScreenType(),
                "/api/screenshots/" + s.getImageFilename(),
                s.getOcrText(), s.isOcrAvailable()
        );
    }
}
