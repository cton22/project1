package com.rok.monitor.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rok.monitor.dto.CaptureRequest;
import com.rok.monitor.entity.Account;
import com.rok.monitor.entity.GameSnapshot;
import com.rok.monitor.repository.AccountRepository;
import com.rok.monitor.repository.GameSnapshotRepository;

@Service
public class MonitorService {
    private final AccountRepository accountRepository;
    private final GameSnapshotRepository snapshotRepository;
    private final ScreenCaptureService screenCaptureService;
    private final OcrService ocrService;

    public MonitorService(AccountRepository accountRepository,
                          GameSnapshotRepository snapshotRepository,
                          ScreenCaptureService screenCaptureService,
                          OcrService ocrService) {
        this.accountRepository = accountRepository;
        this.snapshotRepository = snapshotRepository;
        this.screenCaptureService = screenCaptureService;
        this.ocrService = ocrService;
    }

    @Transactional
    public GameSnapshot capture(CaptureRequest request) throws Exception {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        String filename = screenCaptureService.capturePrimaryScreen();
        OcrService.OcrResult result = request.runOcr()
                ? ocrService.read(filename)
                : new OcrService.OcrResult(false, "", "OCR was not requested");

        return snapshotRepository.save(new GameSnapshot(
                account, filename, result.text(), result.available()
        ));
    }
}
