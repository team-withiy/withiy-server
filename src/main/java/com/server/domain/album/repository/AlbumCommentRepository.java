package com.server.domain.album.repository;

import com.server.domain.album.entity.Album;
import com.server.domain.album.entity.AlbumComment;
import com.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumCommentRepository extends JpaRepository<AlbumComment, Long> {
    void deleteByIdAndUser(Long id, User user);
    AlbumComment findByIdAndAlbumAndUser(Long id, Album album, User user);
}
