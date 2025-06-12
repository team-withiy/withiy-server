package com.server.domain.album.service;

import com.server.domain.album.entity.Album;
import com.server.domain.album.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    public Album save(Album album) {
        return albumRepository.save(album);

    }
}
