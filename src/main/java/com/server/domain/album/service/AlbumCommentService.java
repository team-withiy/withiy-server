package com.server.domain.album.service;

import com.server.domain.album.entity.Album;
import com.server.domain.album.entity.AlbumComment;
import com.server.domain.album.repository.AlbumCommentRepository;
import com.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumCommentService {
    private final AlbumCommentRepository albumCommentRepository;

    public void deleteAlbumComment(Long commentId, User user) {
        albumCommentRepository.deleteByIdAndUser(commentId, user);
    }

    public AlbumComment getAlbumComment(Long commentId, Album album, User user) {
        return albumCommentRepository.findByIdAndAlbumAndUser(commentId, album, user);
    }

}
