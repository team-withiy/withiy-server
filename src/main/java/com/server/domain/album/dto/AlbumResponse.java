package com.server.domain.album.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumResponse {
    private Long albumId;
    private String title;
    private LocalDate scheduleAt;
    private List<String> photoUrl;
}
