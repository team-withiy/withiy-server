package com.server.domain.photo.repository;

import com.server.domain.album.entity.Album;
import com.server.domain.photo.entity.Photo;
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

	int countPhotosByAlbum(Album album);

	@Query("SELECT COUNT(p) FROM Photo p WHERE p.album.id = :albumId")
	long countPhotosByAlbumId(Long albumId);

	@Query("SELECT p FROM Photo p " +
		"JOIN p.album a " +
		"WHERE a.id = :albumId " +
		"AND p.id > :cursor " +
		"ORDER BY p.createdAt ASC")
	List<Photo> findPrevPhotosByAlbumId(Long albumId, Long cursor, Pageable pageable);

	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
		"FROM Photo p " +
		"JOIN p.album a " +
		"WHERE a.id = :albumId " +
		"AND p.id < :cursor " +
		"ORDER BY p.createdAt DESC")
	boolean existsNextPhotoByAlbumId(Long albumId, Long cursor);

	@Query("SELECT p FROM Photo p " +
		"JOIN p.album a " +
		"WHERE a.id = :albumId " +
		"AND p.id < :cursor " +
		"ORDER BY p.createdAt DESC")
	List<Photo> findNextPhotosByAlbumId(Long albumId, Long cursor, Pageable pageable);

	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
		"FROM Photo p " +
		"JOIN p.album a " +
		"WHERE a.id = :albumIds " +
		"AND p.id > :cursor ")
	boolean existsPrevPhotoByAlbumId(Long albumId, Long cursor);

	@Query("SELECT p.imgUrl FROM Photo p " +
		"WHERE p.album.id = :albumId " +
		"AND p.user.id = :userId " +
		"ORDER BY p.createdAt DESC")
	List<String> findImageUrlsByAlbumIdAndUserId(Long albumId, Long userId, Pageable pageable);
}
