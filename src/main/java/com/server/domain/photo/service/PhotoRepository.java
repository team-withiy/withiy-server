package com.server.domain.photo.service;

import com.server.domain.album.entity.Album;
import com.server.domain.photo.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    @Query("SELECT p.imgUrl FROM Photo p WHERE p.album = :album")
    List<String> findImageUrlsByAlbum(@Param("album") Album album);
}
