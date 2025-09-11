package com.server.domain.album.service;

import com.server.domain.album.entity.Album;
import com.server.domain.album.entity.PlaceAlbum;
import com.server.domain.album.repository.AlbumRepository;
import com.server.domain.place.entity.Place;
import com.server.domain.place.repository.PlaceAlbumRepository;
import com.server.domain.user.entity.User;
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

	public Album getAlbumByPlace(Place place) {
		return placeAlbumRepository.findAlbumByPlace(place.getId())
			.orElse(null);
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

	public List<Album> getAlbumsByPlace(Long placeId) {
		return placeAlbumRepository.findAlbumsByPlaceId(placeId);
	}
}
