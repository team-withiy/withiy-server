package com.server.domain.album.service;

import com.server.domain.album.entity.Album;
import com.server.domain.album.entity.PlaceAlbum;
import com.server.domain.album.repository.AlbumRepository;
import com.server.domain.place.entity.Place;
import com.server.domain.place.repository.PlaceAlbumRepository;
import com.server.domain.user.entity.User;
import com.server.global.error.code.AlbumErrorCode;
import com.server.global.error.exception.BusinessException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

	public Album setDefaultAlbum(Place savedPlace, User user) {
		Album album = Album.builder()
			.title(savedPlace.getName())
			.user(user)
			.build();
		return save(savedPlace, album);
	}

	public Map<Long, Album> getAlbumsByPlaceIds(List<Long> placeIds) {
		List<PlaceAlbum> placeAlbums = placeAlbumRepository.findByPlaceIds(placeIds);
		return placeAlbums.stream()
			.collect(Collectors.toMap(pa -> pa.getPlace().getId(), PlaceAlbum::getAlbum));
	}

	public Album getAlbumByPlaceId(Long placeId) {
		return placeAlbumRepository.findAlbumByPlaceId(placeId)
			.orElseThrow(() -> new BusinessException(AlbumErrorCode.ALBUM_NOT_FOUND));
	}

	public Place getPlaceByAlbumId(Long albumId) {
		return placeAlbumRepository.findPlaceByAlbumId(albumId)
			.orElseThrow(() -> new BusinessException(AlbumErrorCode.PLACE_NOT_MATCHED));
	}
}
