package com.server.domain.album.service;

import com.server.domain.album.entity.Album;
import com.server.domain.album.repository.AlbumRepository;
import com.server.global.error.code.AlbumErrorCode;
import com.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumService {

	private final AlbumRepository albumRepository;

    // TODO
	public Album getAlbumByPlaceId(Long placeId) {
        return null;
//		return albumRepository.findByPlaceId(placeId)
//			.orElseThrow(() -> new BusinessException(AlbumErrorCode.ALBUM_NOT_FOUND));

	}

}
