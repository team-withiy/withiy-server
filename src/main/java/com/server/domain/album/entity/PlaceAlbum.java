package com.server.domain.album.entity;

import com.server.domain.place.entity.Place;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
