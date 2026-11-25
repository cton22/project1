package com.rok.monitor.service;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ScreenCaptureService {
    @Value("${app.screenshot-dir:../storage/screenshots}")
    private String screenshotDir;

    public String capturePrimaryScreen() throws Exception {
        Rectangle bounds = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage image = new Robot().createScreenCapture(bounds);

        String filename = "rok-" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS")) + ".png";

        Path target = Path.of(screenshotDir).resolve(filename);
        Files.createDirectories(target.getParent());

        if (!ImageIO.write(image, "png", target.toFile())) {
            throw new IOException("PNG writer is not available");
        }
        return filename;
    }
}
