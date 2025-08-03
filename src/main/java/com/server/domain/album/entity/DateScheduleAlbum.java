package com.server.domain.album.entity;

import com.server.domain.dateSchedule.entity.DateSchedule;
import jakarta.persistence.*;
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
