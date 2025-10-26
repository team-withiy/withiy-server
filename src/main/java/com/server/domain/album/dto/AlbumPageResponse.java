package com.server.domain.album.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumPageResponse {
    private List<AlbumResponse> data;
    private Long total;
}
