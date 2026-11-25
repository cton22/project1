package com.rok.monitor.dto;

import jakarta.validation.constraints.NotNull;

public record CaptureRequest(
        @NotNull Long accountId,
        boolean runOcr
) {}
