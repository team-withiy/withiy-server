package com.server.domain.place.repository;


import com.server.domain.album.entity.PlaceAlbum;
import com.server.domain.place.entity.Place;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceAlbumRepository extends JpaRepository<PlaceAlbum, Long> {

	Optional<PlaceAlbum> findByPlace(Place place);
}
