package com.univer.ontology.diplom.service.impl;

import com.univer.ontology.diplom.service.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FilesStorageServiceImpl implements FilesStorageService {
    private final Path root = Paths.get("src/main/resources/uploads");

    @Autowired
    public FilesStorageServiceImpl() {
    }

    @Override
    public String save(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
            return file.getOriginalFilename();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }
}
