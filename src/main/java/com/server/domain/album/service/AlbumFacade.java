package com.server.domain.album.service;

import static com.server.global.error.code.AlbumErrorCode.ALBUM_COMMENT_OVER_10;

import com.server.domain.album.dto.AlbumCommentRequest;
import com.server.domain.album.dto.AlbumCommentResponse;
import com.server.domain.album.dto.AlbumDetailResponse;
import com.server.domain.album.dto.AlbumPageResponse;
import com.server.domain.album.dto.AlbumResponse;
import com.server.domain.album.entity.Album;
import com.server.domain.album.entity.AlbumComment;
import com.server.domain.dateSchedule.entity.DateSchedule;
import com.server.domain.dateSchedule.service.DateSchedService;
import com.server.domain.user.entity.Couple;
import com.server.domain.user.entity.User;
import com.server.domain.user.service.CoupleService;
import com.server.global.error.exception.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlbumFacade {

	private final AlbumService albumService;
	private final AlbumCommentService albumCommentService;
	private final CoupleService coupleService;
	private final DateSchedService dateSchedService;

	public AlbumPageResponse getAlbums(User user, int page, int size, String order)
		throws Exception {
		Couple couple = coupleService.getCoupleOrNull(user);
		Page<Album> albumPage = albumService.getAlbums(couple, page, size, order);
		List<AlbumResponse> albumResponses = albumPage.get().toList()
			.stream()
			.map(this::createAlbumResponse)
			.toList();

		return new AlbumPageResponse(albumResponses, albumPage.getTotalElements());
	}

	public AlbumDetailResponse getAlbum(User user, Long albumId) {
		Couple couple = coupleService.getCoupleOrNull(user);
		Album album = albumService.getAlbum(couple, albumId);
		AlbumResponse albumResponse = createAlbumResponse(album);
		List<AlbumCommentResponse> comments = album.getAlbumComments()
			.stream()
			.map(ac -> new AlbumCommentResponse(ac.getId(), ac.getComment()))
			.toList();
		return new AlbumDetailResponse(albumResponse, comments);
	}

	@Transactional
	public void deleteAlbum(User user, Long albumId) {
		DateSchedule dateSchedule = dateSchedService.findByUserAndAlbumId(user, albumId);

		Album album = dateSchedule.getAlbum();
		album.delete();
		dateSchedule.deleteAlbum();
	}

	@Transactional
	public void writeComment(User user, AlbumCommentRequest request) {
		Couple couple = coupleService.getCoupleOrNull(user);
		Album album = albumService.getAlbum(couple, request.getAlbumId());
		List<AlbumComment> albumComments = album.getAlbumComments();
		int cnt = 0;
		for (AlbumComment albumComment : albumComments) {
			if (albumComment.getUser().getId().equals(user.getId())) {
				cnt++;
			}
		}

		if (cnt == 10) {
			throw new BusinessException(ALBUM_COMMENT_OVER_10);
		}

		AlbumComment albumComment = AlbumComment.builder()
			.album(album)
			.user(user)
			.comment(request.getComment())
			.build();
		albumComments.add(albumComment);
	}

	@Transactional
	public void updateComment(User user, Long commentId, AlbumCommentRequest request) {
		Couple couple = coupleService.getCoupleOrNull(user);
		Album album = albumService.getAlbum(couple, request.getAlbumId());
		AlbumComment albumComment = albumCommentService.getAlbumComment(commentId, album, user);
		albumComment.updateComment(request.getComment());
	}

	@Transactional
	public void deleteComment(User user, Long commentId) {
		albumCommentService.deleteAlbumComment(commentId, user);
	}

	private AlbumResponse createAlbumResponse(Album album) {
		List<String> photoUrl = album.getAlbumPhotos().stream()
			.map(albumPhoto -> albumPhoto.getPhoto().getImgUrl())
			.toList();

		return new AlbumResponse(album.getId(), album.getTitle(), album.getScheduleAt(), photoUrl);
	}
}
