package com.yappy.search_engine.repository.impl;

import com.yappy.search_engine.model.EmbeddingAudio;
import com.yappy.search_engine.model.TranscriptionAudio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MediaContentRepositoryImpl {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MediaContentRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void updateTranscriptionsBatch(List<TranscriptionAudio> transcriptionAudios) {
        String sql = "UPDATE video_data.videos SET transcription_audio = ?, language_audio = ? WHERE url = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                TranscriptionAudio transcription = transcriptionAudios.get(i);
                ps.setString(1, transcription.getTranscription());
                ps.setString(2, transcription.getLanguage());
                ps.setString(3, transcription.getUrl().trim());
            }

            @Override
            public int getBatchSize() {
                return transcriptionAudios.size();
            }
        });
    }

    public void updateEmbeddingAudioBatch(List<EmbeddingAudio> embeddingAudios) {
        String sql = "UPDATE video_data.videos SET embedding_audio = ? WHERE url = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                EmbeddingAudio transcription = embeddingAudios.get(i);
                ps.setString(1, transcription.getEmbedding());
                ps.setString(2, transcription.getUrl());
            }

            @Override
            public int getBatchSize() {
                return embeddingAudios.size();
            }
        });
    }
}
