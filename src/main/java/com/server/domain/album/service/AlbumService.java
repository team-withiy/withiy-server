package com.server.domain.album.service;

import com.server.domain.album.entity.Album;
import com.server.domain.album.entity.PlaceAlbum;
import com.server.domain.album.repository.AlbumRepository;
import com.server.domain.place.entity.Place;
import com.server.domain.place.repository.PlaceAlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final PlaceAlbumRepository placeAlbumRepository;
    private final AlbumRepository albumRepository;

    public Album save(Place place, Album album) {
        placeAlbumRepository.save(new PlaceAlbum(album, place));
        return albumRepository.save(album);
    }

    public Album getAlbum(Place place) {
        return placeAlbumRepository.findByPlace(place)
            .map(PlaceAlbum::getAlbum)
            .orElse(null);
    }
}
