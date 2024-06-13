package com.yappy.search_engine.repository;

import com.yappy.search_engine.model.MediaContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Repository
public interface MediaContentRepository extends JpaRepository<MediaContent, Long> {
    @Modifying
    @Query(value = "UPDATE MediaContent m SET m.indexingTime = :indexingTime WHERE m.url = :url")
    void updateIndexingTime(@Param("url") String url,
                            @Param("indexingTime") Long indexingTime);

    @Query(value = "select m.indexingTime from MediaContent m where m.uuid = :uuid")
    Long findIndexingTime(@Param("uuid") UUID uuid);
}
