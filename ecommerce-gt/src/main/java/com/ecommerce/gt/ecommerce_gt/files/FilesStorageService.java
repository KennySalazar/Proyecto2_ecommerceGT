package com.ecommerce.gt.ecommerce_gt.files;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FilesStorageService {

    private final Path root = Paths.get("uploads");

    public FilesStorageService() {
        try {
            if (!Files.exists(root))
                Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String save(MultipartFile file) {
        try {
            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }
            String filename = UUID.randomUUID() + ext;
            Files.copy(file.getInputStream(), root.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo", e);
        }
    }

    public Path resolve(String filename) {
        return root.resolve(filename);
    }
}
