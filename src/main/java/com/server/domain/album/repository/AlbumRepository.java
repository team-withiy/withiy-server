package com.server.domain.album.repository;

import com.server.domain.album.entity.Album;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlbumRepository extends JpaRepository<Album, Long> {

	@Query("SELECT a FROM Album a WHERE a.place.id = :placeId")
	Optional<Album> findByPlaceId(Long placeId);
}
