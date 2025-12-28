package com.server.domain.dateSchedule.entity;

import com.server.domain.album.entity.Album;
import com.server.domain.place.entity.Place;
import com.server.domain.route.entity.Route;
import com.server.domain.user.entity.User;
import com.server.global.common.BaseTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "date_schedule")
@EntityListeners(AuditingEntityListener.class)
public class DateSchedule extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "schedule_at")
	private LocalDate scheduleAt;

	@OneToOne(mappedBy = "dateSchedule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Route route;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "album_id",
            unique = true,      // 앨범 하나는 하나의 일정만 가질 수 있음을 DB 레벨에서 강제
            nullable = true     // 앨범 없이 일정이 존재하거나, 삭제 후 NULL 처리되도록 허용
    )
    private Album album;

    public void updateAlbum(Album album) {
        this.album = album;
    }

    public void deleteAlbum() {
        this.album = null;
    }

    /**
     * 새로운 Route로 DateSchedule 생성 (팩토리 메서드)
     */
    public static DateSchedule createWithNewRoute(
        String scheduleName,
        LocalDate scheduleAt,
        User user,
        String routeName,
        List<Place> places
    ) {
        DateSchedule dateSchedule = DateSchedule.builder()
            .name(scheduleName)
            .scheduleAt(scheduleAt)
            .user(user)
            .build();

        // Route 생성 및 연결
        Route route = Route.createForDateSchedule(dateSchedule, routeName, user, places);
        dateSchedule.route = route;

        return dateSchedule;
    }

    /**
     * 기존 Route를 복제하여 DateSchedule 생성 (팩토리 메서드)
     */
    public static DateSchedule createFromExistingRoute(
        String scheduleName,
        LocalDate scheduleAt,
        User user,
        Route sourceRoute
    ) {
        DateSchedule dateSchedule = DateSchedule.builder()
            .name(scheduleName)
            .scheduleAt(scheduleAt)
            .user(user)
            .build();

        // Route 복제 (새로운 Route 인스턴스 생성)
        Route copiedRoute = sourceRoute.copy(dateSchedule, user);
        dateSchedule.route = copiedRoute;

        return dateSchedule;
    }
}
