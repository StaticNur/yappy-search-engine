package com.yappy.search_engine.repository.impl;

import com.yappy.search_engine.model.Embedding;
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

    public void updateEmbeddingAudioBatch(List<Embedding> embeddings) {
        String sql = "UPDATE video_data.videos SET embedding_audio = ? WHERE url = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Embedding embeddingAudio = embeddings.get(i);
                ps.setString(1, embeddingAudio.getEmbedding());
                ps.setString(2, embeddingAudio.getUrl());
            }

            @Override
            public int getBatchSize() {
                return embeddings.size();
            }
        });
    }

    public void updateEmbeddingVideoBatch(List<Embedding> embeddings) {
        String sql = "UPDATE video_data.videos SET embedding_visual = ? WHERE url = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Embedding embeddingVisual = embeddings.get(i);
                ps.setString(1, embeddingVisual.getEmbedding());
                ps.setString(2, embeddingVisual.getUrl());
            }

            @Override
            public int getBatchSize() {
                return embeddings.size();
            }
        });
    }

    public void updateEmbeddingUserDescriptionBatch(List<Embedding> embeddings) {
        String sql = "UPDATE video_data.videos SET embedding_user_description = ? WHERE url = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Embedding embeddingUserDescription = embeddings.get(i);
                ps.setString(1, embeddingUserDescription.getEmbedding());
                ps.setString(2, embeddingUserDescription.getUrl());
            }

            @Override
            public int getBatchSize() {
                return embeddings.size();
            }
        });
    }
}
