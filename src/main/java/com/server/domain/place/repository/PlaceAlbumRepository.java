package com.server.domain.place.repository;


import com.server.domain.album.entity.Album;
import com.server.domain.album.entity.PlaceAlbum;
import com.server.domain.place.entity.Place;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceAlbumRepository extends JpaRepository<PlaceAlbum, Long> {

	@Query("SELECT pa FROM PlaceAlbum pa " +
		"JOIN FETCH pa.album " +
		"WHERE pa.place.id IN :placeIds")
	List<PlaceAlbum> findByPlaceIds(@Param("placeIds") List<Long> placeIds);

	@Query("SELECT pa.place FROM PlaceAlbum pa " +
		"WHERE pa.album.id = :albumId")
	Optional<Place> findPlaceByAlbumId(Long albumId);

	@Query("SELECT pa.album FROM PlaceAlbum pa " +
		"WHERE pa.place.id = :placeId")
	Optional<Album> findAlbumByPlaceId(Long placeId);
}
