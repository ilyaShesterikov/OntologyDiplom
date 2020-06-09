package com.univer.ontology.diplom.service;

import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {
    public String save(MultipartFile file);
}
