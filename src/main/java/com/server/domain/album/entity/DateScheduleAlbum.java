package com.server.domain.album.entity;

import com.server.domain.dateSchedule.entity.DateSchedule;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "date_schedule_album",
	uniqueConstraints = @UniqueConstraint(columnNames = "album_id"))
public class DateScheduleAlbum {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "album_id", nullable = false, unique = true)
	private Album album;

	@ManyToOne
	@JoinColumn(name = "date_schedule_id", nullable = false)
	private DateSchedule schedule;
}
