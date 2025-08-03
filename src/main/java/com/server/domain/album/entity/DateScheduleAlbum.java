package com.server.domain.album.entity;

import com.server.domain.dateSchedule.entity.DateSchedule;
import jakarta.persistence.*;

@Entity
@Table(name = "date_schedule_album",
    uniqueConstraints = @UniqueConstraint(columnNames = "album_id"))
public class DateScheduleAlbum {
    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "album_id", nullable = false, unique = true)
    private Album album;

    @ManyToOne
    @JoinColumn(name = "date_schedule_id", nullable = false)
    private DateSchedule schedule;
}
