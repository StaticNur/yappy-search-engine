package com.yappy.search_engine.service.impl;

import com.yappy.search_engine.model.TranscriptionAudio;
import com.yappy.search_engine.service.ImportJsonService;
import com.yappy.search_engine.service.MediaContentService;
import com.yappy.search_engine.util.parser.JsonParser;
import com.yappy.search_engine.util.parser.ReaderArchiveFile;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class JsonDataServiceImpl implements ImportJsonService {
    public static final String PATH_ZIP_FILE = "concatenated_trancriptions_by_url.json.zip";
    public static final String FILE_NAME = "concatenated_trancriptions_by_url.json";
    public static final String PATH_ZIP_FILE_HIGH_QUALITY = "stt_results_0-checkpoint1100_high_quality.json.zip";
    public static final String FILE_NAME_HIGH_QUALITY = "stt_results_0-checkpoint1100.json";
    public final JsonParser jsonParser;
    private final MediaContentService mediaContentService;
    private final ReaderArchiveFile readerArchiveFile;

    @Autowired
    public JsonDataServiceImpl(JsonParser jsonParser, MediaContentService mediaContentService,
                               ReaderArchiveFile readerArchiveFile) {
        this.jsonParser = jsonParser;
        this.mediaContentService = mediaContentService;
        this.readerArchiveFile = readerArchiveFile;
    }

    @Override
    public void importData() {
        long begin = System.currentTimeMillis();
        try (InputStream inputStream = readerArchiveFile.readArchiveFile(PATH_ZIP_FILE, FILE_NAME)) {
            List<TranscriptionAudio> transcriptionAudios = jsonParser.parseJson(inputStream);
            System.out.println("Ready parseJson transcriptionAudios: " + (System.currentTimeMillis() - begin) + "ms");
            mediaContentService.updateAllTranscriptions(transcriptionAudios);
        } catch (NotOfficeXmlFileException e) {
            System.err.println("Файл не является OOXML: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка при обработке файла: " + e.getMessage());
        }
    }

    @Override
    public void importDataHighQuality() {
        long begin = System.currentTimeMillis();
        try (InputStream inputStream = readerArchiveFile.readArchiveFile(PATH_ZIP_FILE_HIGH_QUALITY, FILE_NAME_HIGH_QUALITY)) {
            List<TranscriptionAudio> transcriptionAudios = jsonParser.parseJson(inputStream);
            System.out.println("Ready parseJson FILE_NAME_HIGH_QUALITY: " + (System.currentTimeMillis() - begin) + "ms");
            mediaContentService.updateAllTranscriptions(transcriptionAudios);
        } catch (IOException e) {
            System.err.println("Ошибка при обработке файла: " + e.getMessage());
        }
    }
}
