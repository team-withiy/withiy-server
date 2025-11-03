package com.server.domain.album.repository;

import com.server.domain.album.entity.Album;
import com.server.domain.user.entity.Couple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    Page<Album> findByCouple(Couple couple, Pageable pageable);

    Album findByIdAndCouple(Long id, Couple couple);
}
