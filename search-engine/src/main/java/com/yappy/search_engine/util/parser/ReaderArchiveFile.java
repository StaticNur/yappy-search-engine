package com.yappy.search_engine.util.parser;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class ReaderArchiveFile {
    public InputStream readArchiveFile(String filePath, String fileName) throws IOException {
        Resource resource = new ClassPathResource(filePath);
        InputStream inputStream = resource.getInputStream();
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            if (entry.getName().equals(fileName)) {
                return zipInputStream;
            }
        }
        throw new FileNotFoundException("Файл не найден: " + fileName);
    }
}
