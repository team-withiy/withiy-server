package com.server.domain.place.repository;


import com.server.domain.album.entity.PlaceAlbum;
import com.server.domain.place.entity.Place;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceAlbumRepository extends JpaRepository<PlaceAlbum, Long> {

	Optional<PlaceAlbum> findByPlace(Place place);

	@Query("SELECT pa FROM PlaceAlbum pa " +
		"JOIN FETCH pa.album " +
		"WHERE pa.place.id IN :placeIds")
	List<PlaceAlbum> findByPlaceIds(@Param("placeIds") List<Long> placeIds);
}
