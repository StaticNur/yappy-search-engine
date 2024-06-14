package com.yappy.search_engine.service.impl;

import com.yappy.search_engine.mapper.ExcelDataMapper;
import com.yappy.search_engine.model.MediaContent;
import com.yappy.search_engine.model.TranscriptionAudio;
import com.yappy.search_engine.model.VideoFromExcel;
import com.yappy.search_engine.repository.MediaContentRepository;
import com.yappy.search_engine.repository.impl.MediaContentRepositoryImpl;
import com.yappy.search_engine.service.MediaContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Взаимодействие PostgreSQL
 */
@Service
public class MediaContentServiceImpl implements MediaContentService {
    private static final int BATCH_SIZE = 10_000;
    private final MediaContentRepository mediaContentRepository;
    private final MediaContentRepositoryImpl mediaContentRepositoryImpl;

    @Autowired
    public MediaContentServiceImpl(MediaContentRepository mediaContentRepository,
                                   MediaContentRepositoryImpl mediaContentRepositoryImpl) {
        this.mediaContentRepository = mediaContentRepository;
        this.mediaContentRepositoryImpl = mediaContentRepositoryImpl;
    }

    @Override
    public List<MediaContent> getAllVideo(){
        return mediaContentRepository.findAll();
    }

    @Override
    @Transactional
    public void saveAll(List<MediaContent> mediaContents) {
        for (int i = 0; i < mediaContents.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, mediaContents.size());
            List<MediaContent> batchList = mediaContents.subList(i, endIndex);
            mediaContentRepository.saveAll(batchList);
            mediaContentRepository.flush();
        }
    }

    @Override
    @Transactional
    public void save(MediaContent video) {
        mediaContentRepository.save(video);
    }

    @Override
    @Transactional
    public void updateAllTranscriptions(List<TranscriptionAudio> transcriptionAudios) {
        int numThreads = Runtime.getRuntime().availableProcessors(); // Используйте количество процессоров
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < transcriptionAudios.size(); i += BATCH_SIZE) {
            int startIndex = i;
            int endIndex = Math.min(i + BATCH_SIZE, transcriptionAudios.size());
            List<TranscriptionAudio> batchList = transcriptionAudios.subList(startIndex, endIndex);

            executor.submit(() -> {
                try {
                    mediaContentRepositoryImpl.updateTranscriptionsBatch(batchList);
                    System.out.println("Отправлено " + startIndex + " пачка транскрипции");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    @Override
    @Transactional
    public void updateIndexingTime(String url, Long time) {
        mediaContentRepository.updateIndexingTime(url, time);
    }

    @Override
    public String getIndexingTime(String uuid) {
        return Objects.toString(mediaContentRepository.findIndexingTime(UUID.fromString(uuid)), "0");
    }

    /*@Override
    @Transactional
    public void updateAllTranscriptions(List<TranscriptionAudio> transcriptionAudios) {
        int batchSize = 10000; // Указываете желаемый размер пакета
        for (int i = 0; i < transcriptionAudios.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, transcriptionAudios.size());
            List<TranscriptionAudio> batchList = transcriptionAudios.subList(i, endIndex);

            System.out.println("Отправлено "+i+" пачка транскрипции");
            mediaContentRepositoryImpl.updateTranscriptionsBatch(batchList);
        }
    }*/
}

















