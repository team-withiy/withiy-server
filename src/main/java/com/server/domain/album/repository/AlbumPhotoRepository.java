package com.server.domain.album.repository;

import com.server.domain.album.entity.AlbumPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumPhotoRepository extends JpaRepository<AlbumPhoto, Long> {

}
