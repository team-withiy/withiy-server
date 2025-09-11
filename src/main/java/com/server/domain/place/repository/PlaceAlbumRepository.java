package com.server.domain.place.repository;


import com.server.domain.album.entity.Album;
import com.server.domain.album.entity.PlaceAlbum;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceAlbumRepository extends JpaRepository<PlaceAlbum, Long> {

	@Query("SELECT a FROM PlaceAlbum pa " +
		"JOIN pa.album a " +
		"WHERE pa.place.id = :placeId")
	Optional<Album> findAlbumByPlace(Long placeId);

	@Query("SELECT pa FROM PlaceAlbum pa " +
		"JOIN FETCH pa.album " +
		"WHERE pa.place.id IN :placeIds")
	List<PlaceAlbum> findByPlaceIds(@Param("placeIds") List<Long> placeIds);


	@Query("SELECT a FROM PlaceAlbum pa " +
		"JOIN pa.album a " +
		"WHERE pa.place.id = :placeId")
	List<Album> findAlbumsByPlaceId(Long placeId);
}
