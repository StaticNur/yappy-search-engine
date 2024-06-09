package com.yappy.search_engine.repository;

import com.yappy.search_engine.model.MediaContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaContentRepository extends JpaRepository<MediaContent, Long> {
}
