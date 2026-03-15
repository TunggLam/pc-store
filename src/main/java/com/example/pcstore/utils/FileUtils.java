package com.example.pcstore.utils;

import com.example.pcstore.logging.LoggingFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
public class FileUtils {

    private static final Logger LOGGER = LoggingFactory.getLogger(FileUtils.class);

    private static final String JAVA_IO_TMPDIR = "java.io.tmpdir";

    private final Executor executor;

    public static File convertMultipartFileToFile(MultipartFile multipartFile) {
        if (Objects.isNull(multipartFile)) {
            return null;
        }

        File file = new File(System.getProperty(JAVA_IO_TMPDIR) + "tmp_" + UUID.randomUUID() + "_" + multipartFile.getName() + ".jpg");
        try (OutputStream os = Files.newOutputStream(file.toPath())) {
            os.write(multipartFile.getBytes());
        } catch (IOException ex) {
            LOGGER.error("[FILE UTILS][CONVERT MULTIPART FILE TO FILE] Exception: {}", ex.getMessage());
        }
        return file;
    }

    /*-- Xóa nhiều file theo bất đồng bộ--*/
    public void deleteMultiFileAsynchronous(File... files) {
        Arrays.stream(files).forEach(this::deleteFileAsynchronous);
    }

    /* Xóa file bất đồng bộ */
    public void deleteFileAsynchronous(final File file) {
        if (Objects.nonNull(file)) {
            CompletableFuture.runAsync(() -> {
                try {
                    Files.deleteIfExists(file.toPath());
                    LOGGER.info("[FILE UTILS] Xóa file thành công");
                } catch (Exception e) {
                    LOGGER.error("[FILE UTILS] Xóa file không thành công. Exception: {}", e.getMessage());
                }
            }, executor);
        }
    }
}

