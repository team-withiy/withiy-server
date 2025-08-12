package com.server.domain.photo.repository;

import com.server.domain.album.entity.Album;
import com.server.domain.photo.entity.Photo;
import com.server.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

	@Query("SELECT p.imgUrl FROM Photo p WHERE p.album = :album")
	List<String> findImageUrlsByAlbum(@Param("album") Album album);

	List<Photo> findAllByAlbum(Album album);

	List<Photo> findAllByAlbumAndUser(Album album, User reviewer);
}
