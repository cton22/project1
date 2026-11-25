package com.rok.monitor.service;

import java.io.File;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
public class OcrService {
    @Value("${app.screenshot-dir:../storage/screenshots}")
    private String screenshotDir;

    @Value("${app.tesseract-path:C:/Program Files/Tesseract-OCR/tesseract.exe}")
    private String tesseractPath;

    public record OcrResult(boolean available, String text, String message) {}

    public OcrResult read(String filename) {
        File executable = new File(tesseractPath);
        if (!executable.exists()) {
            return new OcrResult(false, "", "Tesseract not found. Screenshot was saved; OCR skipped.");
        }

        try {
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(executable.getParentFile().getAbsolutePath() + File.separator + "tessdata");
            tesseract.setLanguage("eng");
            tesseract.setPageSegMode(6);
            String text = tesseract.doOCR(Path.of(screenshotDir, filename).toFile());
            return new OcrResult(true, text == null ? "" : text.trim(), "OCR completed");
        } catch (TesseractException | RuntimeException e) {
            return new OcrResult(false, "", "OCR failed: " + e.getMessage());
        }
    }
}
