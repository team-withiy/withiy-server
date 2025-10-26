package com.server.domain.album.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumDetailResponse {
    private AlbumResponse albumDetail;
    private List<AlbumCommentResponse> comments;
}
