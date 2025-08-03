package com.server.domain.place.repository;


import com.server.domain.album.entity.PlaceAlbum;
import com.server.domain.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceAlbumRepository extends JpaRepository<PlaceAlbum, Long> {

    Optional<PlaceAlbum> findByPlace(Place place);
}
