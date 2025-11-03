package com.server.domain.album.service;

import com.server.domain.album.entity.Album;
import com.server.domain.album.repository.AlbumRepository;
import com.server.domain.user.entity.Couple;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumService {

	private final AlbumRepository albumRepository;

    public Album saveAlbumAndReturn(Album album) {
        return albumRepository.save(album);
    }

    public Page<Album> getAlbums(Couple couple, int page, int size, String order) throws Exception {
        Pageable pageable;
        if (order.equals("asc")) {
            pageable = PageRequest.of(page, size, Direction.ASC, "scheduleAt");
        } else if (order.equals("desc")) {
            pageable = PageRequest.of(page, size, Direction.ASC, "scheduleAt");
        } else {
            throw new Exception("에러");
        }

        return albumRepository.findByCouple(couple, pageable);
    }

    public Album getAlbum(Couple couple, Long albumId) {
        return albumRepository.findByIdAndCouple(albumId, couple);
    }
}
