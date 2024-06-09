package com.yappy.search_engine.service.impl;

import com.yappy.search_engine.mapper.ExcelDataMapper;
import com.yappy.search_engine.model.MediaContent;
import com.yappy.search_engine.model.VideoFromExcel;
import com.yappy.search_engine.repository.MediaContentRepository;
import com.yappy.search_engine.service.MediaContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Взаимодействие PostgreSQL
 */
@Service
public class MediaContentServiceImpl implements MediaContentService {

    private final MediaContentRepository mediaContentRepository;
    private final ExcelDataMapper excelDataMapper;

    @Autowired
    public MediaContentServiceImpl(MediaContentRepository mediaContentRepository, ExcelDataMapper excelDataMapper) {
        this.mediaContentRepository = mediaContentRepository;
        this.excelDataMapper = excelDataMapper;
    }

    @Override
    public List<MediaContent> getAllVideo(){
        return mediaContentRepository.findAll();
    }

    @Override
    @Transactional
    public void saveAll(List<VideoFromExcel> videoFromExcels) {
        List<MediaContent> mediaContents = excelDataMapper.buildMediaContentFromVideo(videoFromExcels);
        int batchSize = 10000;
        for (int i = 0; i < mediaContents.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, mediaContents.size());
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
}
