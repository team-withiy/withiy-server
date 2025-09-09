package com.server.domain.photo.repository;

import com.server.domain.album.entity.Album;
import com.server.domain.photo.entity.Photo;
import com.server.domain.user.entity.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

	@Query("SELECT p.imgUrl FROM Photo p WHERE p.album = :album")
	List<String> findAllImageUrlByAlbum(@Param("album") Album album);

	@Query("SELECT p.imgUrl FROM Photo p " +
		"WHERE p.album = :album " +
		"ORDER BY p.createdAt DESC")
	List<String> findImageUrlsByAlbum(@Param("album") Album album, Pageable pageable);

	List<Photo> findAllByAlbumAndUser(Album album, User reviewer);

	@Query("SELECT p FROM Photo p " +
		"JOIN p.album " +
		"WHERE p.album = :album " +
		"ORDER BY p.createdAt DESC")
	List<Photo> findAllByAlbum(Album album, Pageable pageable);

	@Query("SELECT p FROM Photo p " +
		"JOIN FETCH p.album " +
		"WHERE p.album.id IN :albumIds " +
		"ORDER BY p.createdAt DESC")
	List<Photo> findAllByAlbumIds(List<Long> albumIds);
}
