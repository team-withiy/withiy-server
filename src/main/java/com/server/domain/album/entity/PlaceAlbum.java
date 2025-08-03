package com.server.domain.album.entity;

import com.server.domain.place.entity.Place;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "place_album",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "album_id"),
        @UniqueConstraint(columnNames = "place_id")
    }
)
public class PlaceAlbum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "album_id", unique = true)
    private Album album;

    @OneToOne
    @JoinColumn(name = "place_id", unique = true)
    private Place place;

    public PlaceAlbum(Album album, Place place) {
        this.album = album;
        this.place = place;
    }
}
